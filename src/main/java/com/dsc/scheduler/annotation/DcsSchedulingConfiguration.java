/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dsc.scheduler.annotation;

import com.dsc.scheduler.aop.DcsScheduledAnnotationBeanPostProcessor;
import com.dsc.scheduler.ExtendedLockConfigurationExtractor;
import com.dsc.scheduler.lock.DefaultLockingTaskExecutor;
import com.dsc.scheduler.lock.LockProvider;
import com.dsc.scheduler.lock.LockingTaskExecutor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

/**
 * {@code @Configuration} class that registers a {@link ScheduledAnnotationBeanPostProcessor}
 * bean capable of processing Spring's @{@link DcsScheduled} annotation.
 *
 * <p>This configuration class is automatically imported when using the
 * {@link EnableDcsScheduling @EnableScheduling} annotation. See
 * {@code @EnableScheduling}'s javadoc for complete usage details.
 *
 * @author Typhoon
 * @since 3.1
 * @see EnableDcsScheduling
 * @see DcsScheduledAnnotationBeanPostProcessor
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class DcsSchedulingConfiguration {

	@Bean(name = "internalDcsScheduledAnnotationProcessor")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public DcsScheduledAnnotationBeanPostProcessor dcsScheduledAnnotationProcessor(@Lazy ExtendedLockConfigurationExtractor extendedLockConfigurationExtractor
			,@Lazy LockingTaskExecutor lockingTaskExecutor) {
		return new DcsScheduledAnnotationBeanPostProcessor(extendedLockConfigurationExtractor,lockingTaskExecutor);
	}

	@Bean
	public LockingTaskExecutor lockingTaskExecutor(@Lazy LockProvider lockProvider) {
		return new DefaultLockingTaskExecutor(lockProvider);
	}

}
