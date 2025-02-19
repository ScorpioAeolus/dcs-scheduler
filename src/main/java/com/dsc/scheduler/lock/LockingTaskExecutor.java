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


import com.dsc.scheduler.support.Nullable;

public interface LockingTaskExecutor {
    /** Executes task if it's not already running.
     * @param lockConfig config
     * @param task task
     */
    void executeWithLock(Runnable task, LockConfiguration lockConfig);

    /**
     *
     * @param task task
     * @param lockConfig lockConfig
     * @throws Throwable throwable
     */
    void executeWithLock(Task task, LockConfiguration lockConfig) throws Throwable;

    /** Executes task.
     *
     * @param lockConfig config
     * @param task task
     * @param <T> genericity
     * @throws UnsupportedOperationException not support operation
     * @return  task result
     */
    default <T> TaskResult<T> executeWithLock(TaskWithResult<T> task, LockConfiguration lockConfig) throws Throwable {
        throw new UnsupportedOperationException();
    }

    @FunctionalInterface
    interface Task {
        void call() throws Throwable;
    }

    @FunctionalInterface
    interface TaskWithResult<T> {
        @Nullable
        T call() throws Throwable;
    }

    final class TaskResult<T> {
        private final boolean executed;

        @Nullable
        private final T result;

        private TaskResult(boolean executed, @Nullable T result) {
            this.executed = executed;
            this.result = result;
        }

        public boolean wasExecuted() {
            return executed;
        }

        @Nullable
        public T getResult() {
            return result;
        }

        static <T> TaskResult<T> result(@Nullable T result) {
            return new TaskResult<>(true, result);
        }

        static <T> TaskResult<T> notExecuted() {
            return new TaskResult<>(false, null);
        }
    }
}
