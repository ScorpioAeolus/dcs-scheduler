package com.dsc.scheduler.config;

import com.dsc.scheduler.lock.DefaultLockingTaskExecutor;
import com.dsc.scheduler.lock.LockProvider;
import com.dsc.scheduler.lock.LockingTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.TaskUtils;

/**
 * @author typhoon
 **/
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("task-scheduler");
        taskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        taskScheduler.setErrorHandler(TaskUtils.getDefaultErrorHandler(true));
        taskScheduler.initialize();
        return taskScheduler;
    }

}
