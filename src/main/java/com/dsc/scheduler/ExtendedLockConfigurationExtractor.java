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
package com.dsc.scheduler;


import com.dsc.scheduler.lock.LockConfiguration;
import com.dsc.scheduler.lock.LockConfigurationExtractor;

import java.lang.reflect.Method;
import java.util.Optional;

public interface ExtendedLockConfigurationExtractor extends LockConfigurationExtractor {
    /** Extracts lock configuration for given method
     *
     * @param method method
     * @param object object
     * @param parameterValues values
     * @return optional
     */
    Optional<LockConfiguration> getLockConfiguration(Object object, Method method, Object[] parameterValues);
}
