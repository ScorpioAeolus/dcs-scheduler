package com.dsc.scheduler.lock.provider.db;


import com.dsc.scheduler.support.NonNull;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public enum DatabaseProduct {
//    POSTGRES_SQL("PostgreSQL"::equalsIgnoreCase, PostgresSqlServerTimeStatementsSource::new),
//    SQL_SERVER("Microsoft SQL Server"::equalsIgnoreCase, MsSqlServerTimeStatementsSource::new),
//    ORACLE("Oracle"::equalsIgnoreCase, OracleServerTimeStatementsSource::new),
    MY_SQL("MySQL"::equalsIgnoreCase, MySqlServerTimeStatementsSource::new),
//    MARIA_DB("MariaDB"::equalsIgnoreCase, MySqlServerTimeStatementsSource::new),
//    HQL("HSQL Database Engine"::equalsIgnoreCase, HsqlServerTimeStatementsSource::new),
//    H2("H2"::equalsIgnoreCase, H2ServerTimeStatementsSource::new),
//    DB2(s -> s.substring(0, 3).equalsIgnoreCase("DB2"), Db2ServerTimeStatementsSource::new),
    UNKNOWN(s -> false, configuration -> {
        throw new UnsupportedOperationException("DB time is not supported for unknown database product");
    });

    private final Predicate<String> productMatcher;

    private final Function<JdbcTemplateLockProvider.Configuration, SqlStatementsSource> serverTimeStatementsSource;

    DatabaseProduct(
            Predicate<String> productMatcher, Function<JdbcTemplateLockProvider.Configuration, SqlStatementsSource> serverTimeStatementsSource) {
        this.productMatcher = productMatcher;
        this.serverTimeStatementsSource = serverTimeStatementsSource;
    }

    SqlStatementsSource getDbTimeStatementSource(JdbcTemplateLockProvider.Configuration configuration) {
        return serverTimeStatementsSource.apply(configuration);
    }

    /**
     * Searches for the right DatabaseProduct based on the ProductName returned from
     * JDBC Connection Metadata
     *
     * @param productName
     *            Obtained from the JDBC connection. See
     *            java.sql.connection.getMetaData().getProductName().
     * @return The matching ProductName enum
     */
    @NonNull
    static DatabaseProduct matchProductName(final String productName) {
        return Arrays.stream(DatabaseProduct.values())
                .filter(databaseProduct -> databaseProduct.productMatcher.test(productName))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
