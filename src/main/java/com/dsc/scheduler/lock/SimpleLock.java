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

import java.time.Duration;
import java.util.Optional;

public interface SimpleLock {

    /**
     * Unlocks the lock. Once you unlock it, you should not use for any other
     * operation.
     *
     * @throws IllegalStateException
     *             if the lock has already been unlocked or extended
     */
    void unlock();

    /**
     * Extends the lock. If the lock can be extended a new lock is returned. After
     * calling extend, no other operation can be called on current lock.
     *
     * <p>
     * This method is NOT supported by all lock providers.
     *
     * @return a new lock or empty optional if the lock can not be extended
     * @throws IllegalStateException
     *             if the lock has already been unlocked or extended
     * @throws UnsupportedOperationException
     *             if the lock extension is not supported by LockProvider.
     *
     * @param lockAtLeastFor least
     * @param lockAtMostFor most
     */
    default Optional<SimpleLock> extend(Duration lockAtMostFor, Duration lockAtLeastFor) {
        throw new UnsupportedOperationException();
    }
}
