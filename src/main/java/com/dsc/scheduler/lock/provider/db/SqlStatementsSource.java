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

import com.dsc.scheduler.lock.ClockProvider;
import com.dsc.scheduler.lock.LockConfiguration;
import com.dsc.scheduler.support.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

class SqlStatementsSource {
    protected final JdbcTemplateLockProvider.Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(SqlStatementsSource.class);

    SqlStatementsSource(JdbcTemplateLockProvider.Configuration configuration) {
        this.configuration = configuration;
    }

    static SqlStatementsSource create(JdbcTemplateLockProvider.Configuration configuration) {
        DatabaseProduct databaseProduct = getDatabaseProduct(configuration);

        if (configuration.getUseDbTime()) {
            SqlStatementsSource statementsSource = databaseProduct.getDbTimeStatementSource(configuration);
            logger.debug("Using {}", statementsSource.getClass().getSimpleName());
            return statementsSource;
        } else {
            logger.debug("Using SqlStatementsSource");
            return new SqlStatementsSource(configuration);
//            if (Objects.equals(databaseProduct, DatabaseProduct.POSTGRES_SQL)) {
//                logger.debug("Using PostgresSqlStatementsSource");
//                return new PostgresSqlStatementsSource(configuration);
//            } else {
//                logger.debug("Using SqlStatementsSource");
//                return new SqlStatementsSource(configuration);
//            }
        }
    }

    private static DatabaseProduct getDatabaseProduct(final JdbcTemplateLockProvider.Configuration configuration) {
        if (configuration.getDatabaseProduct() != null) {
            return configuration.getDatabaseProduct();
        }
        try {
            String jdbcProductName = configuration.getJdbcTemplate().execute((ConnectionCallback<String>)
                    connection -> connection.getMetaData().getDatabaseProductName());
            return DatabaseProduct.matchProductName(jdbcProductName);
        } catch (Exception e) {
            logger.debug("Can not determine database product name " + e.getMessage());
            return DatabaseProduct.UNKNOWN;
        }
    }

    @NonNull
    Map<String, Object> params(@NonNull LockConfiguration lockConfiguration) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",lockConfiguration.getName());
        map.put("lockUntil",timestamp(lockConfiguration.getLockAtMostUntil()));
        map.put("now",timestamp(ClockProvider.now()));
        map.put("lockedBy",configuration.getLockedByValue());
        map.put("unlockTime",timestamp(lockConfiguration.getUnlockTime()));
        return map;
    }

    @NonNull
    private Object timestamp(Instant time) {
        TimeZone timeZone = configuration.getTimeZone();
        if (timeZone == null) {
            return Timestamp.from(time);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Date.from(time));
            calendar.setTimeZone(timeZone);
            return calendar;
        }
    }

    String getInsertStatement() {
        return "INSERT INTO " + tableName() + "(" + name() + ", " + lockUntil() + ", " + lockedAt() + ", " + lockedBy()
                + ") VALUES(:name, :lockUntil, :now, :lockedBy)";
    }

    public String getUpdateStatement() {
        return "UPDATE " + tableName() + " SET " + lockUntil() + " = :lockUntil, " + lockedAt() + " = :now, "
                + lockedBy() + " = :lockedBy WHERE " + name() + " = :name AND " + lockUntil() + " <= :now";
    }

    public String getExtendStatement() {
        return "UPDATE " + tableName() + " SET " + lockUntil() + " = :lockUntil WHERE " + name() + " = :name AND "
                + lockedBy() + " = :lockedBy AND " + lockUntil() + " > :now";
    }

    public String getUnlockStatement() {
        return "UPDATE " + tableName() + " SET " + lockUntil() + " = :unlockTime WHERE " + name() + " = :name";
    }

    String name() {
        return configuration.getColumnNames().getName();
    }

    String lockUntil() {
        return configuration.getColumnNames().getLockUntil();
    }

    String lockedAt() {
        return configuration.getColumnNames().getLockedAt();
    }

    String lockedBy() {
        return configuration.getColumnNames().getLockedBy();
    }

    String tableName() {
        return configuration.getTableName();
    }
}
