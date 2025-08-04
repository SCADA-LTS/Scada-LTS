package org.scada_lts.archiving;

import org.springframework.jdbc.core.JdbcTemplate;

public interface IArchiveQueryProvider {

    String showCreateTable(String tableName);
    String showColumns(String tableName);
    String getTimestampColumnName(String tableName);
    boolean tableExists(JdbcTemplate jdbc, String tableName);
}
