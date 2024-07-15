package com.dsc.scheduler.config;

import com.dsc.scheduler.lock.LockProvider;
import com.dsc.scheduler.lock.provider.redis.RedisLockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author typhoon
 * @date 2024/7/14 6:43 下午
 **/
public class RedisProviderConfig {

    @Autowired
    private Environment environment;

    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, environment.getActiveProfiles()[0]);
    }
}
