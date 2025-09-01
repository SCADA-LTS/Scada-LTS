package org.scada_lts.archive;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;


public class MySqlArchiveQueryProvider implements IArchiveQueryProvider {

    @Override
    public String showCreateTable(String tableName) {
        final String[] parts = splitTable(tableName);
        final String schema = parts[0];
        final String table  = parts[1];
        if (schema == null) {
            return "SHOW CREATE TABLE " + btq(table);
        }
        return "SHOW CREATE TABLE " + btq(schema) + "." + btq(table);
    }

    @Override
    public String getTimestampColumnName(String tableName) {
        final String t = baseName(tableName);
        switch (t) {
            case "pointValues":
                return "ts";
            case "events":
                return "activeTs";
            case "userEvents":
                return "ts";
            case "reportInstances":
                return "runStartTime";
            default:
                return "ts";
        }
    }

    @Override
    public boolean tableExists(JdbcTemplate jdbc, String tableName) {
        Objects.requireNonNull(jdbc, "jdbc");
        Objects.requireNonNull(tableName, "tableName");

        final String[] parts = splitTable(tableName);
        final String schema = parts[0];
        final String table  = parts[1];

        final String sql;
        final Object[] args;

        if (schema == null) {
            sql  = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = ?";
            args = new Object[]{ table };
        } else {
            sql  = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = ? AND table_name = ?";
            args = new Object[]{ schema, table };
        }

        Integer count = jdbc.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }

    @Override
    public List<String> listColumns(JdbcTemplate jdbc, String tableName) {
        Objects.requireNonNull(jdbc, "jdbc");
        Objects.requireNonNull(tableName, "tableName");

        final String[] parts = splitTable(tableName);
        final String schema = parts[0];
        final String table  = parts[1];

        final String sql;
        final Object[] args;

        if (schema == null) {
            sql  = "SELECT COLUMN_NAME " +
                    "FROM information_schema.columns " +
                    "WHERE table_schema = DATABASE() AND table_name = ? " +
                    "ORDER BY ordinal_position";
            args = new Object[]{ table };
        } else {
            sql  = "SELECT COLUMN_NAME " +
                    "FROM information_schema.columns " +
                    "WHERE table_schema = ? AND table_name = ? " +
                    "ORDER BY ordinal_position";
            args = new Object[]{ schema, table };
        }

        return jdbc.query(sql, (rs, rn) -> rs.getString(1), args);
    }

    @Override
    public boolean tryLockTable(JdbcTemplate archiveJdbc, String tableName) {
        // MySQL/MariaDB advisory lock by name; returns 1 if acquired, 0 if not
        String lockName = "archiver:" + tableName;
        Boolean ok = archiveJdbc.queryForObject("SELECT GET_LOCK(?, 0)", Boolean.class, lockName);
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public void releaseLockTable(JdbcTemplate archiveJdbc, String tableName) {
        String lockName = "archiver:" + tableName;
        // RELEASE_LOCK returns 1 if released, 0 if the lock was not held, NULL on error
        archiveJdbc.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, lockName);
    }

    @Override
    public void applyPerTransactionTimeout(JdbcTemplate jdbc, int seconds) {
        jdbc.execute("SET SESSION MAX_EXECUTION_TIME = " + (seconds * 1000));
    }

    @Override
    public void clearPerTransactionTimeout(JdbcTemplate jdbc) {
        jdbc.execute("SET SESSION MAX_EXECUTION_TIME = 0");
    }

    @Override
    public String buildSelectBatchSql(String tableName, String selectColumns, String tsColumn, boolean haveCursor, int limit) {
        // MySQL/MariaDB variant (LIMIT works the same)
        StringBuilder sb = new StringBuilder()
                .append("SELECT ").append(selectColumns)
                .append(" FROM ").append(tableName)
                .append(" WHERE ").append(tsColumn).append(" < ?");

        if (haveCursor) {
            sb.append(" AND (")
                    .append(tsColumn).append(" > ? OR (")
                    .append(tsColumn).append(" = ? AND id > ?))");
        }
        sb.append(" ORDER BY ").append(tsColumn).append(", id")
                .append(" LIMIT ").append(limit);
        return sb.toString();
    }

    @Override
    public List<Object> bindSelectBatchArgs(long beforeTsMillis, boolean haveCursor, long lastTs, long lastId) {
        List<Object> args = new ArrayList<>();
        args.add(beforeTsMillis);
        if (haveCursor) {
            args.add(lastTs);
            args.add(lastTs);
            args.add(lastId);
        }
        return args;
    }

    @Override
    public String buildVerifyIdsSql(String tableName, int idCount) {
        String placeholders = String.join(",", Collections.nCopies(idCount, "?"));
        return "SELECT id FROM " + tableName + " WHERE id IN (" + placeholders + ")";
    }

    @Override
    public String buildDeleteChunkSql(String tableName, int idCount) {
        String placeholders = String.join(",", Collections.nCopies(idCount, "?"));
        return "DELETE FROM " + tableName + " WHERE id IN (" + placeholders + ")";
    }

    @Override
    public String buildCountIdsSql(String tableName, int idCount) {
        String placeholders = String.join(",", Collections.nCopies(idCount, "?"));
        return "SELECT COUNT(*) AS c FROM " + tableName + " WHERE id IN (" + placeholders + ")";
    }

    @Override
    public Map<String, String> columnTypes(JdbcTemplate jdbc, String tableName) {
        final String sql =
                "SELECT COLUMN_NAME, COLUMN_TYPE " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? " +
                        "ORDER BY ORDINAL_POSITION";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, tableName);
        Map<String,String> map = new LinkedHashMap<>();
        for (Map<String,Object> r : rows) {
            map.put((String) r.get("COLUMN_NAME"), ((String) r.get("COLUMN_TYPE")).toLowerCase());
        }
        return map;
    }

    @Override
    public String buildAddColumnSql(String tableName, String columnName, String columnType) {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
    }

    @Override
    public String archivedAtColumnName() {
        return "archived_at";
    }

    @Override
    public String archivedAtSqlType() {
        // Milliseconds since epoch
        return "BIGINT";
    }

    @Override
    public String buildInsertWithExtraColumnSql(String tableName, String selectColumns, String extraColumn) {
        String[] colsArr = selectColumns.split("\\s*,\\s*");
        String placeholders = String.join(",", Collections.nCopies(colsArr.length + 1, "?"));
        return "INSERT INTO " + tableName + " (" + selectColumns + ", " + extraColumn + ") VALUES (" + placeholders + ")";
    }

    @Override
    public boolean hasColumn(JdbcTemplate jdbc, String tableName, String columnName) {
        String sql =
                "SELECT 1 FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, tableName, columnName);
        return !rows.isEmpty();
    }


    // ---------- helpers ----------

    private static String[] splitTable(String name) {
        String n = name == null ? "" : name.trim();
        int dot = n.indexOf('.');
        if (dot < 0) {
            return new String[]{ null, n };
        }
        String schema = n.substring(0, dot);
        String table  = n.substring(dot + 1);
        return new String[]{ schema, table };
    }

    private static String baseName(String name) {
        if (name == null) return "";
        int dot = name.indexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : name;
    }

    private static String btq(String ident) {
        if (ident == null) return "``";
        String safe = ident.replace("`", "``");
        return "`" + safe + "`";
    }

    @SuppressWarnings("unused")
    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }
}
