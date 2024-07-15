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
package com.dsc.scheduler.lock.provider.db;

import com.dsc.scheduler.lock.LockConfiguration;
import com.dsc.scheduler.support.NonNull;

import java.util.HashMap;
import java.util.Map;

class MySqlServerTimeStatementsSource extends SqlStatementsSource {
    private static final String now = "UTC_TIMESTAMP(3)";
    private static final String lockAtMostFor = "TIMESTAMPADD(MICROSECOND, :lockAtMostForMicros, " + now + ")";

    MySqlServerTimeStatementsSource(JdbcTemplateLockProvider.Configuration configuration) {
        super(configuration);
    }

    @Override
    String getInsertStatement() {
        return "INSERT IGNORE INTO " + tableName() + "(" + name() + ", " + lockUntil() + ", " + lockedAt() + ", "
                + lockedBy() + ") VALUES(:name, " + lockAtMostFor + ", " + now + ", :lockedBy)";
    }

    @Override
    public String getUpdateStatement() {
        return "UPDATE " + tableName() + " SET " + lockUntil() + " = " + lockAtMostFor + ", " + lockedAt() + " = " + now
                + ", " + lockedBy() + " = :lockedBy WHERE " + name() + " = :name AND " + lockUntil() + " <= " + now;
    }

    @Override
    public String getUnlockStatement() {
        String lockAtLeastFor = "TIMESTAMPADD(MICROSECOND, :lockAtLeastForMicros, " + lockedAt() + ")";
        return "UPDATE " + tableName() + " SET " + lockUntil() + " = IF (" + lockAtLeastFor + " > " + now + " , "
                + lockAtLeastFor + ", " + now + ") WHERE " + name() + " = :name AND " + lockedBy() + " = :lockedBy";
    }

    @Override
    public String getExtendStatement() {
        return "UPDATE " + tableName() + " SET " + lockUntil() + " = " + lockAtMostFor + " WHERE " + name()
                + " = :name AND " + lockedBy() + " = :lockedBy AND " + lockUntil() + " > " + now;
    }

    @Override
    @NonNull
    Map<String, Object> params(@NonNull LockConfiguration lockConfiguration) {
        Map<String,Object> map = new HashMap<>();
        map.put("name",lockConfiguration.getName());
        map.put("lockedBy",configuration.getLockedByValue());
        map.put("lockAtMostForMicros",lockConfiguration.getLockAtMostFor().toNanos() / 1_000);
        map.put("lockAtLeastForMicros",lockConfiguration.getLockAtLeastFor().toNanos() / 1_000);
        return map;
    }
}
