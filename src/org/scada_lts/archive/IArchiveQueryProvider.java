package org.scada_lts.archive;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface IArchiveQueryProvider {

    String showCreateTable(String tableName);
    String getTimestampColumnName(String tableName);
    boolean tableExists(JdbcTemplate jdbc, String tableName);
    List<String> listColumns(JdbcTemplate jdbc, String tableName);
    boolean tryLockTable(JdbcTemplate archiveJdbc, String tableName);
    void releaseLockTable(JdbcTemplate archiveJdbc, String tableName);
    void applyPerTransactionTimeout(JdbcTemplate jdbc, int seconds);
    void clearPerTransactionTimeout(JdbcTemplate jdbc);
    String buildSelectBatchSql(String tableName, String selectColumns, String tsColumn, boolean haveCursor, int limit);
    List<Object> bindSelectBatchArgs(long beforeTsMillis, boolean haveCursor, long lastTs, long lastId);
    String buildVerifyIdsSql(String tableName, int idCount);
    String buildDeleteChunkSql(String tableName, int idCount);
    String buildCountIdsSql(String tableName, int idCount);
    Map<String, String> columnTypes(JdbcTemplate jdbc, String tableName);
    String buildAddColumnSql(String tableName, String columnName, String columnType);
    String archivedAtColumnName();
    String archivedAtSqlType();
    String buildInsertWithExtraColumnSql(String tableName, String selectColumns, String extraColumn);
    boolean hasColumn(JdbcTemplate jdbc, String tableName, String columnName);

}
