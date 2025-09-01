package org.scada_lts.archive;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class PostgresArchiveQueryProvider implements IArchiveQueryProvider {

    private JdbcTemplate jdbc;

    public PostgresArchiveQueryProvider() {
    }

    public PostgresArchiveQueryProvider(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void setJdbcTemplate(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------------- IArchiveQueryProvider ----------------

    @Override
    public boolean tableExists(JdbcTemplate jdbc, String tableName) {
        Objects.requireNonNull(jdbc, "jdbc");
        Objects.requireNonNull(tableName, "tableName");

        final String[] parts = splitTable(tableName);
        final String schema = parts[0];
        final String table = parts[1];

        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                        "WHERE table_schema = ? AND table_name = ?",
                Integer.class, schema, table
        );
        return cnt != null && cnt > 0;
    }

    @Override
    public List<String> listColumns(JdbcTemplate jdbc, String tableName) {
        Objects.requireNonNull(jdbc, "jdbc");
        Objects.requireNonNull(tableName, "tableName");

        final String[] parts = splitTable(tableName);
        final String schema = parts[0];
        final String table = parts[1];

        return jdbc.query(
                "SELECT column_name " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = ? AND table_name = ? " +
                        "ORDER BY ordinal_position",
                (rs, rn) -> rs.getString(1),
                schema, table
        );
    }

    @Override
    public String getTimestampColumnName(String tableName) {
        String t = baseName(tableName);
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
    public String showCreateTable(String tableName) {
        ensureJdbcFor("showCreateTable");

        final String[] parts = splitTable(tableName);
        final String schema = parts[0];
        final String table = parts[1];

        List<ColumnDef> cols = this.jdbc.query(
                "SELECT column_name, data_type, udt_name, is_nullable, " +
                        "character_maximum_length, numeric_precision, numeric_scale " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = ? AND table_name = ? " +
                        "ORDER BY ordinal_position",
                (rs, rn) -> new ColumnDef(
                        rs.getString("column_name"),
                        safe(rs.getString("data_type")),
                        safe(rs.getString("udt_name")),
                        "YES".equalsIgnoreCase(rs.getString("is_nullable")),
                        (Integer) rs.getObject("character_maximum_length"),
                        (Integer) rs.getObject("numeric_precision"),
                        (Integer) rs.getObject("numeric_scale")
                ),
                schema, table
        );

        if (cols.isEmpty()) {
            throw new IllegalStateException("Source table not found or has no columns: " + tableName);
        }

        String columnsSql = cols.stream()
                .map(this::columnSql)
                .collect(Collectors.joining(",\n"));

        return "CREATE TABLE IF NOT EXISTS " + q(schema) + "." + q(table) + " (\n" +
                columnsSql + "\n)";
    }

    @Override
    public boolean tryLockTable(JdbcTemplate archiveJdbc, String tableName) {
        String key = "archiver:" + tableName;
        // pg_try_advisory_lock(boolean) returns boolean in modern PG; some drivers map it to Boolean
        Boolean ok = archiveJdbc.queryForObject(
                "SELECT pg_try_advisory_lock( abs(('x'||substr(md5(?),1,16))::bit(64)::bigint) )",
                Boolean.class, key
        );
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public void releaseLockTable(JdbcTemplate archiveJdbc, String tableName) {
        String key = "archiver:" + tableName;
        archiveJdbc.queryForObject(
                "SELECT pg_advisory_unlock( abs(('x'||substr(md5(?),1,16))::bit(64)::bigint) )",
                Boolean.class, key
        );
    }

    @Override
    public void applyPerTransactionTimeout(JdbcTemplate jdbc, int seconds) {
        jdbc.execute("SET LOCAL statement_timeout = " + (seconds * 1000));
    }

    @Override
    public void clearPerTransactionTimeout(JdbcTemplate jdbc) {
        jdbc.execute("RESET statement_timeout");
    }

    @Override
    public String buildSelectBatchSql(String tableName, String selectColumns, String tsColumn, boolean haveCursor, int limit) {
        // Uses LIMIT; stable ordering by (ts, id)
        // Cursor condition: (ts > ? OR (ts = ? AND id > ?))
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
        // SELECT id FROM table WHERE id IN (?,?,...)
        String placeholders = String.join(",", Collections.nCopies(idCount, "?"));
        return "SELECT id FROM " + tableName + " WHERE id IN (" + placeholders + ")";
    }

    @Override
    public String buildDeleteChunkSql(String tableName, int idCount) {
        // DELETE FROM table WHERE id IN (?,?,...)
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
        // Uses format_type(...) to get provider-native type string (e.g., "bigint", "varchar(255)").
        final String sql =
                "SELECT a.attname AS column_name, format_type(a.atttypid, a.atttypmod) AS column_type " +
                        "FROM pg_attribute a " +
                        "JOIN pg_class c ON a.attrelid = c.oid " +
                        "JOIN pg_namespace n ON c.relnamespace = n.oid " +
                        "WHERE c.relkind = 'r' " +
                        "  AND (n.nspname = current_schema() OR n.nspname = ANY (current_schemas(true))) " +
                        "  AND c.relname = ? " +
                        "  AND a.attnum > 0 AND NOT a.attisdropped " +
                        "ORDER BY a.attnum";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, tableName);
        Map<String,String> map = new LinkedHashMap<>();
        for (Map<String,Object> r : rows) {
            map.put((String) r.get("column_name"), ((String) r.get("column_type")).toLowerCase());
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
        // Store millis since epoch as BIGINT for consistency with other timestamp longs
        return "BIGINT";
    }

    @Override
    public String buildInsertWithExtraColumnSql(String tableName, String selectColumns, String extraColumn) {
        // INSERT INTO table (col1,col2,...,extra) VALUES (?,?,...,?)
        String[] colsArr = selectColumns.split("\\s*,\\s*");
        String placeholders = String.join(",", Collections.nCopies(colsArr.length + 1, "?"));
        return "INSERT INTO " + tableName + " (" + selectColumns + ", " + extraColumn + ") VALUES (" + placeholders + ")";
    }

    @Override
    public boolean hasColumn(JdbcTemplate jdbc, String tableName, String columnName) {
        String sql =
                "SELECT 1 " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = current_schema() " +
                        "  AND table_name = ? " +
                        "  AND column_name = ?";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, tableName, columnName);
        return !rows.isEmpty();
    }



    // ---------------- helpers ----------------

    private void ensureJdbcFor(String method) {
        if (this.jdbc == null) {
            throw new IllegalStateException("JdbcTemplate is required for " + method + " in PostgresArchiveQueryProvider. " +
                    "Use constructor PostgresArchiveQueryProvider(JdbcTemplate) or setJdbcTemplate(...).");
        }
    }

    private String columnSql(ColumnDef c) {
        String type = mapType(c);
        String nullness = c.nullable ? "" : " NOT NULL";
        return "  " + q(c.name) + " " + type + nullness;
    }

    private String mapType(ColumnDef c) {
        final String dt = c.dataType;
        final String udt = c.udt;

        if (dt.contains("character varying") || "varchar".equals(udt)) {
            if (c.charLen != null && c.charLen > 0) return "varchar(" + c.charLen + ")";
            return "varchar";
        }
        if ("text".equals(dt)) return "text";

        if ("integer".equals(dt) || "int4".equals(udt)) return "integer";
        if ("bigint".equals(dt) || "int8".equals(udt)) return "bigint";
        if ("smallint".equals(dt) || "int2".equals(udt)) return "smallint";

        if (dt.contains("timestamp")) return "timestamp";
        if ("date".equals(dt)) return "date";
        if ("time".equals(dt)) return "time";

        if ("boolean".equals(dt) || "bool".equals(udt)) return "boolean";

        if (dt.contains("numeric") || "numeric".equals(udt)) {
            if (c.numPrec != null && c.numScale != null) {
                return "numeric(" + c.numPrec + "," + c.numScale + ")";
            } else if (c.numPrec != null) {
                return "numeric(" + c.numPrec + ")";
            } else {
                return "numeric";
            }
        }

        if (dt.contains("double")) return "double precision";
        if ("real".equals(dt) || "float4".equals(udt)) return "real";

        if ("bytea".equals(udt)) return "bytea";

        return udt.isEmpty() ? dt : udt;
    }

    private static String[] splitTable(String name) {
        if (name.contains(".")) {
            String[] p = name.split("\\.", 2);
            return new String[]{ p[0], p[1] };
        }
        return new String[]{ "public", name };
    }

    private static String baseName(String name) {
        return name.contains(".") ? name.substring(name.indexOf('.') + 1) : name;
    }

    private static String q(String ident) {
        return "\"" + ident.replace("\"", "\"\"") + "\"";
    }

    private static String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    private static final class ColumnDef {
        final String name;
        final String dataType;
        final String udt;
        final boolean nullable;
        final Integer charLen;
        final Integer numPrec;
        final Integer numScale;

        ColumnDef(String name, String dataType, String udt, boolean nullable,
                  Integer charLen, Integer numPrec, Integer numScale) {
            this.name = name;
            this.dataType = dataType;
            this.udt = udt;
            this.nullable = nullable;
            this.charLen = charLen;
            this.numPrec = numPrec;
            this.numScale = numScale;
        }
    }
}
