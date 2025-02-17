package com.dsc.scheduler.config;

import com.dsc.scheduler.lock.LockProvider;
import com.dsc.scheduler.lock.provider.redis.RedisLockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author typhoon
 **/
public class RedisProviderConfig {

    @Autowired
    private Environment environment;

    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        String env = "default";
        if(null != environment.getActiveProfiles() && environment.getActiveProfiles().length > 0) {
            env = environment.getActiveProfiles()[0];
        }
        return new RedisLockProvider(connectionFactory, env);
    }
}
