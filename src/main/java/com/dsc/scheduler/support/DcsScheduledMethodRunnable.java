/*
 * Copyright 2002-2018 the original author or authors.
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

package com.dsc.scheduler.support;

import com.dsc.scheduler.ExtendedLockConfigurationExtractor;
import com.dsc.scheduler.annotation.DcsScheduled;
import com.dsc.scheduler.aop.DcsScheduledAnnotationBeanPostProcessor;
import com.dsc.scheduler.aop.LockingNotSupportedException;
import com.dsc.scheduler.lock.LockConfiguration;
import com.dsc.scheduler.lock.LockingTaskExecutor;
import org.springframework.scheduling.support.MethodInvokingRunnable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;

/**
 * Variant of {@link MethodInvokingRunnable} meant to be used for processing
 * of no-arg scheduled methods. Propagates user exceptions to the caller,
 * assuming that an error strategy for Runnables is in place.
 *
 * @author Typhoon
 * @since 1.0
 * @see DcsScheduledAnnotationBeanPostProcessor
 */
public class DcsScheduledMethodRunnable implements Runnable {

	private final Object target;

	private final Method method;

	private  final ExtendedLockConfigurationExtractor lockConfigurationExtractor;

	private LockingTaskExecutor lockingTaskExecutor;

	private final DcsScheduled scheduled;

	private  String name;

	private  String lockAtMostFor;

	private  String lockAtLeastFor;


	/**
	 * Create a {@code ScheduledMethodRunnable} for the given target instance,
	 * calling the specified method.
	 * @param target the target instance to call the method on
	 * @param method the target method to call
	 * @param scheduled annotation
	 * @param elce elce
	 * @param lte lte
	 */
	public DcsScheduledMethodRunnable(Object target, Method method, DcsScheduled scheduled, ExtendedLockConfigurationExtractor elce, LockingTaskExecutor lte) {
		this.target = target;
		this.method = method;
		this.scheduled = scheduled;
		this.name = scheduled.name();
		this.lockAtMostFor = scheduled.lockAtMostFor();
		this.lockAtLeastFor = scheduled.lockAtLeastFor();
		this.lockConfigurationExtractor = elce;
		this.lockingTaskExecutor = lte;

	}

	/**
	 * Create a {@code ScheduledMethodRunnable} for the given target instance,
	 * calling the specified method by name.
	 * @param target the target instance to call the method on
	 * @param methodName the name of the target method
	 * @param elce elce
	 * @param lte lte
	 * @param scheduled DcsScheduled
	 * @throws NoSuchMethodException if the specified method does not exist
	 */
	public DcsScheduledMethodRunnable(Object target, String methodName,DcsScheduled scheduled,ExtendedLockConfigurationExtractor elce,LockingTaskExecutor lte) throws NoSuchMethodException {
		this.target = target;
		this.method = target.getClass().getMethod(methodName);
		this.scheduled = scheduled;
		this.name = scheduled.name();
		this.lockAtMostFor = scheduled.lockAtMostFor();
		this.lockAtLeastFor = scheduled.lockAtLeastFor();
		this.lockConfigurationExtractor = elce;
		this.lockingTaskExecutor = lte;
	}


	/**
	 * Return the target instance to call the method on.
	 *
	 * @return object
	 */
	public Object getTarget() {
		return this.target;
	}

	/**
	 * Return the target method to call.
	 * @return Method
	 */
	public Method getMethod() {
		return this.method;
	}

	public String getName() {
		return name;
	}

	public String getLockAtMostFor() {
		return lockAtMostFor;
	}

	public String getLockAtLeastFor() {
		return lockAtLeastFor;
	}

	@Override
	public void run() {
		try {
			Class<?> returnType = this.method.getReturnType();
			if (returnType.isPrimitive() && !void.class.equals(returnType)) {
				throw new LockingNotSupportedException("Can not lock method returning primitive value");
			}
			ReflectionUtils.makeAccessible(this.method);
			//todo this.method.getParameters() 参数问题
			Optional<LockConfiguration> lockConfigurationOptional = lockConfigurationExtractor.getLockConfiguration(scheduled,this.method,this.method.getParameters());
			LockConfiguration lockConfiguration = lockConfigurationOptional.get();
//			LockConfiguration lockConfiguration = lockConfigurationExtractor
//					.getLockConfiguration(invocation.getThis(), invocation.getMethod(), invocation.getArguments())
//					.get();
			//todo锁定操作应该由当前业务线程完成,不需要再用线程池
			lockingTaskExecutor.executeWithLock((LockingTaskExecutor.Task) () -> method.invoke(target), lockConfiguration);
		} catch (InvocationTargetException ex) {
			ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
		}
		catch (IllegalAccessException ex) {
			throw new UndeclaredThrowableException(ex);
		} catch (Throwable e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return this.method.getDeclaringClass().getName() + "." + this.method.getName();
	}

}
