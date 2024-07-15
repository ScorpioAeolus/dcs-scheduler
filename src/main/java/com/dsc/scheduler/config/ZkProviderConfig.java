package com.dsc.scheduler.config;

import com.dsc.scheduler.lock.LockProvider;
import com.dsc.scheduler.lock.provider.zk.ZookeeperCuratorLockProvider;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Bean;

/**
 * @author typhoon
 **/
public class ZkProviderConfig {


    @Bean
    public LockProvider lockProvider(CuratorFramework client) {
        return new ZookeeperCuratorLockProvider(client);
    }
}
