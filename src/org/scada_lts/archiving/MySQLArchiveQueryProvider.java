package org.scada_lts.archiving;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Implementacja IArchiveQueryProvider dla MySQL.
 */
public class MySQLArchiveQueryProvider implements IArchiveQueryProvider {

    @Override
    public String showCreateTable(String tableName) {
        return "SHOW CREATE TABLE " + tableName;
    }

    @Override
    public String showColumns(String tableName) {
        return "SHOW COLUMNS FROM " + tableName;
    }

    @Override
    public String getTimestampColumnName(String tableName) {
        switch (tableName) {
            case "pointValues":
                return "ts";
            case "events":
                return "activeTs";
            default:
                throw new UnsupportedOperationException(
                        "Unknown timestamp column for table: " + tableName
                );
        }
    }


    @Override
    public boolean tableExists(JdbcTemplate jdbc, String tableName) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }

}
