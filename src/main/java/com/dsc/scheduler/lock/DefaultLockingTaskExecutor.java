/**
 * Copyright 2009 the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dsc.scheduler.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;

import static com.dsc.scheduler.lock.LockAssert.alreadyLockedBy;
import static java.util.Objects.requireNonNull;

/** Default {@link LockingTaskExecutor} implementation. */
public class DefaultLockingTaskExecutor implements LockingTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLockingTaskExecutor.class);
    private final LockProvider lockProvider;

    public DefaultLockingTaskExecutor(LockProvider lockProvider) {
        this.lockProvider = requireNonNull(lockProvider);
    }

    @Override
    public void executeWithLock(Runnable task, LockConfiguration lockConfig) {
        try {
            executeWithLock((Task) task::run, lockConfig);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable throwable) {
            // Should not happen
            throw new IllegalStateException(throwable);
        }
    }

    @Override
    public void executeWithLock(Task task, LockConfiguration lockConfig) throws Throwable {
        executeWithLock(
                () -> {
                    task.call();
                    return null;
                },
                lockConfig);
    }

    @Override
    public <T> TaskResult<T> executeWithLock(TaskWithResult<T> task, LockConfiguration lockConfig) throws Throwable {
        String lockName = lockConfig.getName();
        if (alreadyLockedBy(lockName)) {
            logger.debug("Already locked '{}'", lockName);
            return TaskResult.result(task.call());
        }

        Optional<SimpleLock> lock = lockProvider.lock(lockConfig);
        if (lock.isPresent()) {
            try {
                LockAssert.startLock(lockName);
                LockExtender.startLock(lock.get());
                logger.debug(
                        "Locked '{}', lock will be held at most until {}", lockName, lockConfig.getLockAtMostUntil());
                return TaskResult.result(task.call());
            } finally {
                LockAssert.endLock();
                SimpleLock activeLock = LockExtender.endLock();
                if (activeLock != null) {
                    activeLock.unlock();
                } else {
                    // This should never happen, but I do not know any better way to handle the null
                    // case.
                    logger.warn("No active lock, please report this as a bug.");
                    lock.get().unlock();
                }
                if (logger.isDebugEnabled()) {
                    Instant lockAtLeastUntil = lockConfig.getLockAtLeastUntil();
                    Instant now = ClockProvider.now();
                    if (lockAtLeastUntil.isAfter(now)) {
                        logger.debug("Task finished, lock '{}' will be released at {}", lockName, lockAtLeastUntil);
                    } else {
                        logger.debug("Task finished, lock '{}' released", lockName);
                    }
                }
            }
        } else {
            logger.debug("Not executing '{}'. It's locked.", lockName);
            return TaskResult.notExecuted();
        }
    }
}
