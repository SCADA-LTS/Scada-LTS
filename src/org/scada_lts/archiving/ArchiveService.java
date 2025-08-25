package org.scada_lts.archiving;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArchiveService {

    private final IArchiveQueryProvider provider;

    private final Log LOGGER = LogFactory.getLog(ArchiveService.class);

    public ArchiveService(ArchiveQueryProviderFactory archiveQueryProviderFactory) {
        this.provider = archiveQueryProviderFactory.newInstance();
    }

    public void runArchive() {
        ArchiveConfig config = SystemSettingsDAO.getArchiveConfig();

        String archiveDbUrl = config.getDbUrl();
        String archiveDbUrlUsername = config.getDbUsername();
        String archiveDbUrlPassword = config.getDbPassword();
        int batchSize = config.getBatchSize();

        if (config.getTasks() == null || config.getTasks().isEmpty()) {
            LOGGER.info("[ARCHIVER] No tasks defined.");
            return;
        }

        JdbcTemplate jdbc = DAO.getInstance().getJdbcTemp();
        DriverManagerDataSource archiveDataSource = new DriverManagerDataSource();
        archiveDataSource.setUrl(archiveDbUrl);
        archiveDataSource.setUsername(archiveDbUrlUsername);
        archiveDataSource.setPassword(archiveDbUrlPassword);
        JdbcTemplate archiveJdbc = new JdbcTemplate(archiveDataSource);
        archiveJdbc.setFetchSize(batchSize);

        for (ArchiveTask task : config.getTasks()) {
            try {
                LOGGER.info("[ARCHIVER] Running task: " + task.getFunction() + ", olderThan: " + task.getAgeValue() + " " + task.getAgeUnit());
                long now = System.currentTimeMillis();
                long deltaMs = task.getAgeUnit().toMs(task.getAgeValue());
                Timestamp beforeTs = new Timestamp(now - deltaMs);
                String tableName = task.getTable();

                String columns = getColumnList(jdbc, tableName);
                if (columns == null) {
                    LOGGER.warn("[ARCHIVER] Skipping task for table with unknown columns: " + tableName);
                    continue;
                }

                ensureTableExistsByCopy(jdbc, archiveJdbc, tableName);

                switch (task.getFunction()) {
                    case COPY_TO_ARCHIVE:
                        archiveTable(tableName, columns, beforeTs, jdbc, archiveJdbc, batchSize);
                        break;
                    case DELETE_IF_IN_ARCHIVE:
                        deleteFromSourceIfInArchive(tableName, columns, beforeTs, jdbc, archiveJdbc, batchSize);
                        break;
                    default:
                        LOGGER.warn("[ARCHIVER] Unknown function for table " + tableName + ": " + task.getFunction());
                }
            } catch (Exception ex) {
                LOGGER.error("[ARCHIVER] Task failed: table=" + task.getTable() + ", func=" + task.getFunction() + " -> " + ex.getMessage(), ex);
            }
        }
    }

    private void archiveTable(
            String tableName,
            String selectColumns,
            Timestamp beforeTs,
            JdbcTemplate jdbc,
            JdbcTemplate archiveJdbc,
            int batchSize
    ) {
        String tsColumn = provider.getTimestampColumnName(tableName);

        boolean haveCursor = false;
        long lastTs = Long.MIN_VALUE;
        long lastId = Long.MIN_VALUE;
        while (true) {
            StringBuilder sb = new StringBuilder()
                    .append("SELECT ").append(selectColumns)
                    .append(" FROM ").append(tableName)
                    .append(" WHERE ").append(tsColumn).append(" < ?");
            List<Object> args = new ArrayList<>();
            args.add(beforeTs.getTime());

            if (haveCursor) {
                sb.append(" AND (")
                        .append(tsColumn).append(" > ? OR (")
                        .append(tsColumn).append(" = ? AND id > ?))");
                args.add(lastTs);
                args.add(lastTs);
                args.add(lastId);
            }

            sb.append(" ORDER BY ").append(tsColumn).append(", id")
                    .append(" LIMIT ").append(batchSize);

            String selectSql = sb.toString();
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, args.toArray());

            if (rows.isEmpty()) break;

            List<Long> idList = rows.stream()
                    .map(r -> (Number) r.get("id"))
                    .filter(Objects::nonNull)
                    .map(Number::longValue)
                    .collect(Collectors.toList());

            Set<Long> existingIds = new HashSet<>();
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Long> chunk = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String verifySql = "SELECT id FROM " + tableName + " WHERE id IN (" +
                        buildInPlaceholders(chunk.size()) + ")";
                existingIds.addAll(
                        archiveJdbc.query(
                                verifySql,
                                (rs, rowNum) -> rs.getLong("id"),
                                chunk.toArray()
                        )
                );
            }

            List<Map<String, Object>> filteredRows = rows.stream()
                    .filter(r -> {
                        Number n = (Number) r.get("id");
                        return n != null && !existingIds.contains(n.longValue());
                    })
                    .collect(Collectors.toList());

            if (filteredRows.isEmpty()) {
                LOGGER.info("[ARCHIVER] [" + tableName + "] All rows already exist in archive, skipping insert.");
            }
            else {

                String[] colsArr = selectColumns.split("\\s*,\\s*");
                String insertSql = String.format(
                        "INSERT INTO %s (%s) VALUES (%s)",
                        tableName,
                        selectColumns,
                        String.join(",", Collections.nCopies(colsArr.length, "?"))
                );

                archiveJdbc.batchUpdate(insertSql, filteredRows, batchSize, (ps, row) -> {
                    for (int i = 0; i < colsArr.length; i++) {
                        ps.setObject(i + 1, row.get(colsArr[i].trim()));
                    }
                });

                LOGGER.info("[ARCHIVER] [" + tableName + "] Inserted batch: " + filteredRows.size());
            }
            Map<String, Object> lastRow = rows.get(rows.size() - 1);
            lastTs = ((Number) lastRow.get(tsColumn)).longValue();
            lastId = ((Number) lastRow.get("id")).longValue();
            haveCursor = true;

            if (rows.size() < batchSize) break;
        }
    }

    private void deleteFromSourceIfInArchive(
            String tableName,
            String selectColumns,
            Timestamp beforeTs,
            JdbcTemplate jdbc,
            JdbcTemplate archiveJdbc,
            int batchSize
    ) {
        String tsColumn = provider.getTimestampColumnName(tableName);

        boolean haveCursor = false;
        long lastTs = Long.MIN_VALUE;
        long lastId = Long.MIN_VALUE;
        while (true) {
            StringBuilder sb = new StringBuilder()
                    .append("SELECT ").append(selectColumns)
                    .append(" FROM ").append(tableName)
                    .append(" WHERE ").append(tsColumn).append(" < ?");
            List<Object> args = new ArrayList<>();
            args.add(beforeTs.getTime());

            if (haveCursor) {
                sb.append(" AND (")
                        .append(tsColumn).append(" > ? OR (")
                        .append(tsColumn).append(" = ? AND id > ?))");
                args.add(lastTs);
                args.add(lastTs);
                args.add(lastId);
            }

            sb.append(" ORDER BY ").append(tsColumn).append(", id")
                    .append(" LIMIT ").append(batchSize);

            String selectSql = sb.toString();
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, args.toArray());

            List<Long> idList = rows.stream()
                    .map(r -> (Number) r.get("id"))
                    .filter(Objects::nonNull)
                    .map(Number::longValue)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) break;

            List<Long> verifiedIds = new ArrayList<>();
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Long> chunk = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String verifySql = "SELECT id FROM " + tableName + " WHERE id IN (" +
                        buildInPlaceholders(chunk.size()) + ")";
                verifiedIds.addAll(
                        archiveJdbc.query(
                                verifySql,
                                (rs, rowNum) -> rs.getLong("id"),
                                chunk.toArray()
                        )
                );
            }
            LOGGER.info("[ARCHIVER] [" + tableName + "] Found in archive: " + verifiedIds.size());

            if (!verifiedIds.isEmpty()) {
                int deleted = 0;
                for (int i = 0; i < verifiedIds.size(); i += batchSize) {
                    List<Long> chunk = verifiedIds.subList(i, Math.min(i + batchSize, verifiedIds.size()));
                    String inSql = "DELETE FROM " + tableName + " WHERE id IN (" +
                            buildInPlaceholders(chunk.size()) + ")";
                    deleted += jdbc.update(inSql, chunk.toArray());
                }
                LOGGER.info("[ARCHIVER] [" + tableName + "] Deleted from source: " + deleted);
            }
            Map<String, Object> lastRow = rows.get(rows.size() - 1);
            lastTs = ((Number) lastRow.get(tsColumn)).longValue();
            lastId = ((Number) lastRow.get("id")).longValue();
            haveCursor = true;

            if (rows.size() < batchSize) break;
        }
    }

    private void ensureTableExistsByCopy(JdbcTemplate sourceJdbc, JdbcTemplate targetJdbc, String tableName) {
        if (!provider.tableExists(targetJdbc, tableName)) {
            LOGGER.warn("[ARCHIVER] Table '" + tableName + "' does not exist in archive DB. Copying structure from primary...");
            try {
                String ddl = sourceJdbc.queryForObject(
                        provider.showCreateTable(tableName),
                        (rs, rowNum) -> rs.getString(2)
                );
                ddl = sanitizeDDL(ddl);
                targetJdbc.execute(ddl);
                LOGGER.info("[ARCHIVER] Table '" + tableName + "' created in archive DB from source.");
            } catch (Exception e) {
                LOGGER.error("[ARCHIVER] Failed to copy structure for table '" + tableName + "': " + e.getMessage());
            }
        }
    }


    /**
     * Sanitizes the raw DDL statement obtained from SHOW CREATE TABLE.
     * Removes engine-specific options, auto-increment markers, foreign key constraints,
     * and indexes to make the statement safe for creating simplified archive tables.
     * This avoids issues like duplicate primary keys, missing referenced tables,
     * or MySQL-specific options when executing in a different context (e.g., archive DB).
     */
    private String sanitizeDDL(String ddl) {
        ddl = ddl.replaceAll("AUTO_INCREMENT=\\d+\\s*", "");
        ddl = ddl.replaceAll("\\bAUTO_INCREMENT\\b", "");
        ddl = ddl.replaceAll(",\\s*CONSTRAINT `[^`]+` FOREIGN KEY \\([^\\)]+\\) REFERENCES `[^`]+` \\(`[^`]+`\\)(?:\\s*ON DELETE [A-Z]+)?(?:\\s*ON UPDATE [A-Z]+)?", "");
        ddl = ddl.replaceAll(",\\s*KEY `[^`]+` \\([^\\)]+\\)", "");
        ddl = ddl.replaceAll(",\\s*INDEX `[^`]+` \\([^\\)]+\\)", "");
        ddl = ddl.replaceAll("\\s*ENGINE=\\w+\\s*", "");
        ddl = ddl.replaceAll("\\s*ROW_FORMAT=\\w+\\s*", "");
        ddl = ddl.replaceAll("\\s*DEFAULT CHARSET=\\w+\\s*", "");
        ddl = ddl.replaceAll("\\s*DEFAULT COLLATE=\\w+\\s*", "");
        ddl = ddl.replaceAll("\\s*COLLATE=\\w+\\s*", "");
        ddl = ddl.replaceAll("\\s*USING BTREE\\s*", "");
        ddl = ddl.replaceAll("\\s*COMMENT='[^']*'\\s*", "");
        return ddl;
    }

    private String getColumnList(JdbcTemplate jdbc, String tableName) {
        try {
            List<String> columnNames = jdbc.query(
                    provider.showColumns(tableName),
                    (rs, rowNum) -> rs.getString("Field")
            );
            return String.join(", ", columnNames);
        } catch (Exception ex) {
            LOGGER.error("[ARCHIVER] Failed to get columns for table: " + tableName + " — " + ex.getMessage());
            return null;
        }
    }

    // NEW: budowa placeholderów do klauzuli IN (?,?,?,...)
    private String buildInPlaceholders(int count) {
        if (count <= 0) return "";
        if (count == 1) return "?";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(",");
            sb.append("?");
        }
        return sb.toString();
    }
}
