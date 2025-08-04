package org.scada_lts.archiving;

import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArchiveService {

    private final IArchiveQueryProvider provider;

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
            System.out.println("[ARCHIVER] No tasks defined.");
            return;
        }

        JdbcTemplate jdbc = DAO.getInstance().getJdbcTemp();
        DriverManagerDataSource archiveDataSource = new DriverManagerDataSource();
        archiveDataSource.setUrl(archiveDbUrl);
        archiveDataSource.setUsername(archiveDbUrlUsername);
        archiveDataSource.setPassword(archiveDbUrlPassword);
        JdbcTemplate archiveJdbc = new JdbcTemplate(archiveDataSource);

        for (ArchiveTask task : config.getTasks()) {
            System.out.println("[ARCHIVER] Running task: " + task.getFunction() + ", olderThan: " + task.getAgeValue() + " " + task.getAgeUnit());
            long now = System.currentTimeMillis();
            long deltaMs = task.getAgeUnit().toMs(task.getAgeValue());
            Timestamp beforeTs = new Timestamp(now - deltaMs);
            String tableName = task.getTable();
            String columns;

            columns = getColumnList(jdbc, tableName);
            if (columns == null) {
                System.out.println("[ARCHIVER] Skipping task for table with unknown columns: " + tableName);
                continue;
            }

            ensureTableExistsByCopy(jdbc, archiveJdbc, task.getTable());

            switch (task.getFunction()) {
                case COPY_TO_ARCHIVE:
                    archiveTable(tableName, columns, beforeTs, jdbc, archiveJdbc, batchSize);
                    break;
                case DELETE_IF_IN_ARCHIVE:
                    deleteFromSourceIfInArchive(tableName, columns, beforeTs, jdbc, archiveJdbc, batchSize);
                    break;
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
        while (true) {
            String selectSql = String.format("SELECT %s FROM %s WHERE %s < ? LIMIT ?", selectColumns, tableName, tsColumn);
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, beforeTs.getTime(), batchSize);

            if (rows.isEmpty()) break;

            List<Integer> idList = rows.stream()
                    .map(r -> (Integer) r.get("id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Set<Integer> existingIds = new HashSet<>();
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Integer> chunk = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String verifySql = "SELECT id FROM " + tableName + " WHERE id IN (" + chunk.stream().map(x -> "?").collect(Collectors.joining(",")) + ")";
                existingIds.addAll(archiveJdbc.queryForList(verifySql, chunk.toArray(), Integer.class));
            }

            List<Map<String, Object>> filteredRows = rows.stream()
                    .filter(r -> !existingIds.contains((Integer) r.get("id")))
                    .collect(Collectors.toList());

            if (filteredRows.isEmpty()) {
                System.out.println("[ARCHIVER] [" + tableName + "] All rows already exist in archive, skipping insert.");
                break;
            }

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

            System.out.println("[ARCHIVER] [" + tableName + "] Inserted batch: " + filteredRows.size());

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
        while (true) {
            String selectSql = String.format("SELECT %s FROM %s WHERE %s < ? LIMIT ?", selectColumns, tableName, tsColumn);
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, beforeTs.getTime(), batchSize);

            List<Integer> idList = rows.stream()
                    .map(r -> (Integer) r.get("id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) break;

            List<Integer> verifiedIds = new ArrayList<>();
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Integer> chunk = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String verifySql = "SELECT id FROM " + tableName + " WHERE id IN (" + chunk.stream().map(x -> "?").collect(Collectors.joining(",")) + ")";
                verifiedIds.addAll(archiveJdbc.queryForList(verifySql, chunk.toArray(), Integer.class));
            }
            System.out.println("[ARCHIVER] [" + tableName + "] Found in archive: " + verifiedIds.size());

            if (!verifiedIds.isEmpty()) {
                int deleted = 0;
                for (int i = 0; i < verifiedIds.size(); i += batchSize) {
                    List<Integer> chunk = verifiedIds.subList(i, Math.min(i + batchSize, verifiedIds.size()));
                    String inSql = "DELETE FROM " + tableName + " WHERE id IN (" + chunk.stream().map(x -> "?").collect(Collectors.joining(",")) + ")";
                    deleted += jdbc.update(inSql, chunk.toArray());
                }
                System.out.println("[ARCHIVER] [" + tableName + "] Deleted from source: " + deleted);
            }
            if (rows.size() < batchSize) break;
        }
    }

    private void ensureTableExistsByCopy(JdbcTemplate sourceJdbc, JdbcTemplate targetJdbc, String tableName) {
        if (!provider.tableExists(targetJdbc, tableName)) {
            System.out.println("[ARCHIVER] Table '" + tableName + "' does not exist in archive DB. Copying structure from primary...");
            try {
                String ddl = sourceJdbc.queryForObject(
                        provider.showCreateTable(tableName),
                        (rs, rowNum) -> rs.getString(2)
                );
                ddl = sanitizeDDL(ddl);
                targetJdbc.execute(ddl);
                System.out.println("[ARCHIVER] Table '" + tableName + "' created in archive DB from source.");
            } catch (Exception e) {
                System.err.println("[ARCHIVER] Failed to copy structure for table '" + tableName + "': " + e.getMessage());
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
        ddl = ddl.replaceAll("AUTO_INCREMENT", "");
        ddl = ddl.replaceAll(",\\s*CONSTRAINT `[^`]+` FOREIGN KEY \\([^\\)]+\\) REFERENCES `[^`]+` \\(`[^`]+`\\)", "");
        ddl = ddl.replaceAll(",\\s*KEY `[^`]+` \\(`[^`]+`\\)", "");
        ddl = ddl.replaceAll("ENGINE=\\w+\\s*", "");
        ddl = ddl.replaceAll("DEFAULT CHARSET=\\w+\\s*", "");
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
            System.err.println("[ARCHIVER] Failed to get columns for table: " + tableName + " — " + ex.getMessage());
            return null;
        }
    }
}
