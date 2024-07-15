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
package com.dsc.scheduler.aop;

import com.dsc.scheduler.annotation.DcsSchedulingConfiguration;
import com.dsc.scheduler.annotation.EnableDcsScheduling;
import com.dsc.scheduler.config.DBProviderConfig;
import com.dsc.scheduler.config.RedisProviderConfig;
import com.dsc.scheduler.config.SchedulerConfig;
import com.dsc.scheduler.config.ZkProviderConfig;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;


public class DcsSchedulerConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableDcsScheduling.class.getName(), false));
        EnableDcsScheduling.ProviderModel mode = attributes.getEnum("providerModel");
        if(mode == EnableDcsScheduling.ProviderModel.DB) {
            return new String[] {
                    AutoProxyRegistrar.class.getName(),
                    LockConfigurationExtractorConfiguration.class.getName(),
                    SchedulerConfig.class.getName(),
                    DBProviderConfig.class.getName(),
                    DcsSchedulingConfiguration.class.getName()
            };
        } else if(mode == EnableDcsScheduling.ProviderModel.REDIS) {
            return new String[] {
                    AutoProxyRegistrar.class.getName(),
                    LockConfigurationExtractorConfiguration.class.getName(),
                    SchedulerConfig.class.getName(),
                    RedisProviderConfig.class.getName(),
                    DcsSchedulingConfiguration.class.getName()
            };
        } else if(mode == EnableDcsScheduling.ProviderModel.ZOOKEEPER) {
            return new String[] {
                    AutoProxyRegistrar.class.getName(),
                    LockConfigurationExtractorConfiguration.class.getName(),
                    SchedulerConfig.class.getName(),
                    ZkProviderConfig.class.getName(),
                    DcsSchedulingConfiguration.class.getName()
            };
        } else {
            throw new UnsupportedOperationException("Unknown provider mode " + mode);
        }
    }
}
