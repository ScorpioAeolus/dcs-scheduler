/*
 * Copyright 2002-2021 the original author or authors.
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
import com.dsc.scheduler.aop.DcsSchedulerConfigurationSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable dsc scheduler
 *
 * @author Typhoon
 * @since 1.0
 * @see DcsScheduled
 * @see DcsSchedulingConfiguration
 * @see SchedulingConfigurer
 * @see ScheduledTaskRegistrar
 * @see Trigger
 * @see DcsScheduledAnnotationBeanPostProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DcsSchedulerConfigurationSelector.class)
@Documented
public @interface EnableDcsScheduling {


    /**
     * 中心化模式
     *
     **/
    enum ProviderModel {

        /**
         * db中心化
         */
        DB,
        /**
         * redis中心化
         */
        REDIS,

        /**
         * Zookeeper中心化
         */
        ZOOKEEPER,

        ;
    }

    /**
     * provider 模式
     *
     * @return model
     */
    ProviderModel providerModel();

    /**
     * Default value how long the lock should be kept in case the machine which
     * obtained the lock died before releasing it. Can be either time with suffix
     * like 10s or ISO8601 duration as described in
     * {@link java.time.Duration#parse(CharSequence)}, for example PT30S. This is
     * just a fallback, under normal circumstances the lock is released as soon the
     * tasks finishes. Set this to some value much higher than normal task duration.
     * Can be overridden in each ScheduledLock annotation.
     * @return string
     */
    String defaultLockAtMostFor();

    /**
     * The lock will be held at least for this duration. Can be either time with
     * suffix like 10s or ISO8601 duration as described in
     * {@link java.time.Duration#parse(CharSequence)}, for example PT30S. Can be
     * used if you really need to execute the task at most once in given period of
     * time. If the duration of the task is shorter than clock difference between
     * nodes, the task can be theoretically executed more than once (one node after
     * another). By setting this parameter, you can make sure that the lock will be
     * kept at least for given period of time. Can be overridden in each
     * ScheduledLock annotation.
     * @return string
     */
    String defaultLockAtLeastFor() default "PT0S";

//    /**
//     * <p>
//     * Indicate how advice should be applied.
//     * @return mode
//     */
//    AdviceMode mode() default AdviceMode.PROXY;
//
//    /**
//     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
//     * to standard Java interface-based proxies.
//     */
//    boolean proxyTargetClass() default false;

    /**
     * Indicate the ordering of the execution of the locking advisor when multiple
     * advices are applied at a specific joinpoint.
     *
     * <p>
     * The default is {@link Ordered#LOWEST_PRECEDENCE}.
     * @return  int
     */
    int order() default Ordered.LOWEST_PRECEDENCE;


}
