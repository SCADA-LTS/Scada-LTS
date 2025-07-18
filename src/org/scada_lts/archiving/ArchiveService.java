package org.scada_lts.archiving;

import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArchiveService {

    public void runArchiving() {
        // 1. Check if archiving is enabled
        boolean archivingEnabled = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.ARCHIVE_ENABLED);
        if (!archivingEnabled) {
            System.out.println("[ARCHIVER] Archiving is disabled via settings.");
            return;
        }

        // 2. Check which table to archive
        boolean archivePointValues = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.ARCHIVE_TABLE_POINT_VALUES, true);
        boolean archiveEvents = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.ARCHIVE_TABLE_EVENTS, false);

        if (!archivePointValues && !archiveEvents) {
            System.out.println("[ARCHIVER] No tables selected for archiving, exiting.");
            return;
        }

        int totalArchived = 0;
        int totalDeleted = 0;

        // 3. Fetch all archiving parameters from system settings
        String archiveDbUrl = SystemSettingsDAO.getValue(SystemSettingsDAO.ARCHIVE_DB_URL);
        int batchSize = SystemSettingsDAO.getIntValue(SystemSettingsDAO.BATCH_SIZE, 1000);

        String dataAgeValueStr = SystemSettingsDAO.getValue(SystemSettingsDAO.DATA_ARCHIVE_AGE_VALUE, "30");
        String dataAgeUnit = SystemSettingsDAO.getValue(SystemSettingsDAO.DATA_ARCHIVE_AGE_UNIT, "DAYS");

        int dataAgeValue;
        try {
            dataAgeValue = Integer.parseInt(dataAgeValueStr);
        } catch (NumberFormatException e) {
            System.out.println("[ARCHIVER] Invalid value for dataAgeValue. Defaulting to 30.");
            dataAgeValue = 30;
        }

        // 4. Calculate the date threshold
        Instant archiveBefore = Instant.now();
        switch (dataAgeUnit.toUpperCase()) {
            case "SECONDS":
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.SECONDS);
                break;
            case "MINUTES":
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.MINUTES);
                break;
            case "HOURS":
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.HOURS);
                break;
            case "DAYS":
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.DAYS);
                break;
            case "MONTHS":
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.MONTHS);
                break;
            case "YEARS":
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.YEARS);
                break;
            default:
                archiveBefore = archiveBefore.minus(dataAgeValue, ChronoUnit.DAYS);
        }
        Timestamp archiveBeforeTs = Timestamp.from(archiveBefore);

        // 5. Connect to the archive database
        DataSource archiveDataSource = new DriverManagerDataSource(archiveDbUrl);
        JdbcTemplate archiveJdbc = new JdbcTemplate(archiveDataSource);

        System.out.println("[ARCHIVER] Starting archiving to: " + archiveDbUrl +
                ", batch size: " + batchSize +
                ", records older than: " + archiveBeforeTs);

        JdbcTemplate jdbc = DAO.getInstance().getJdbcTemp();

        // 6.pointValues archiving
        if (archivePointValues) {
            totalArchived += archiveTable(
                    "pointValues",
                    "id, dataPointId, ts, value, annotation, sourceType, sourceId",
                    "dataPointId, ts, value, annotation, sourceType, sourceId",
                    "ts",
                    archiveBeforeTs,
                    jdbc, archiveJdbc, batchSize
            );
            totalDeleted += totalArchived;
        }

        // 7. events archiving
        if (archiveEvents) {
            totalArchived += archiveTable(
                    "events",
                    "id, typeName, typeRef1, typeRef2, typeRef3, activeTs, rtnApplicable, rtnTs, rtnCause, alarmLevel, message, ackTs, ackUserId, alternateAckSource, suppressed, comments, eventType, assigneeUserId, assigneeTs",
                    "typeName, typeRef1, typeRef2, typeRef3, activeTs, rtnApplicable, rtnTs, rtnCause, alarmLevel, message, ackTs, ackUserId, alternateAckSource, suppressed, comments, eventType, assigneeUserId, assigneeTs",
                    "activeTs",
                    archiveBeforeTs,
                    jdbc, archiveJdbc, batchSize
            );
            totalDeleted += totalArchived;
        }

        System.out.println("[ARCHIVER] Archiving finished. Total archived: " + totalArchived + ", total deleted: " + totalDeleted);
    }

    /**
     * Helper method to archive one table.
     */
    private int archiveTable(
            String tableName,
            String selectColumns,
            String insertColumns,
            String timeColumn,
            Timestamp archiveBeforeTs,
            JdbcTemplate jdbc, JdbcTemplate archiveJdbc, int batchSize
    ) {
        int totalArchived = 0;

        while (true) {
            String selectSql = String.format("SELECT %s FROM %s WHERE %s < ? LIMIT ?", selectColumns, tableName, timeColumn);
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, archiveBeforeTs, batchSize);

            if (rows.isEmpty()) {
                System.out.println("[ARCHIVER] [" + tableName + "] No more data to archive. Total archived: " + totalArchived);
                break;
            }

            System.out.println("[ARCHIVER] [" + tableName + "] Found " + rows.size() + " records to archive...");

            String[] insertColsArr = insertColumns.split(",");
            String insertSql = String.format(
                    "INSERT INTO %s (%s) VALUES (%s)",
                    tableName,
                    insertColumns,
                    String.join(",", java.util.Collections.nCopies(insertColsArr.length, "?"))
            );

            archiveJdbc.batchUpdate(insertSql, rows, batchSize, (ps, row) -> {
                for (int i = 0; i < insertColsArr.length; i++) {
                    Object val = row.get(insertColsArr[i].trim());
                    ps.setObject(i + 1, val);
                }
            });

            totalArchived += rows.size();

            // Collect IDs for deletion
            List<Integer> idList = rows.stream()
                    .map(r -> (Integer) r.get("id"))
                    .collect(Collectors.toList());

            int deletedBatch = 0;
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Integer> subList = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String inSql = "DELETE FROM " + tableName + " WHERE id IN (" +
                        subList.stream().map(x -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
                int deleted = jdbc.update(inSql, subList.toArray());
                deletedBatch += deleted;
            }

            System.out.println("[ARCHIVER] [" + tableName + "] Archived batch: " + rows.size() + ", deleted: " + deletedBatch);

            if (rows.size() < batchSize) break;
        }

        return totalArchived;
    }
}
