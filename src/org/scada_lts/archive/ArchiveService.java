package org.scada_lts.archive;

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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ArchiveService {

    private final Log LOGGER = LogFactory.getLog(ArchiveService.class);

    // Max time per batch (seconds)
    private static final int BATCH_TX_TIMEOUT_SECONDS = 120;

    private static final int MAX_BATCHES_PER_TASK = 10_000;

    private static final Set<String> ALLOWED_TABLES = Set.of(
            "pointValues",
            "events",
            "userEvents",
            "reportInstances"
    );

    private final IArchiveQueryProvider queries;

    public ArchiveService(IArchiveQueryProvider queries) {
        this.queries = queries;
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

        IArchiveQueryProvider provider = ArchiveQueryProviderFactory.forTarget(archiveJdbc, jdbc);
        if (provider instanceof PostgresArchiveQueryProvider) {
            ((PostgresArchiveQueryProvider) provider).setJdbcTemplate(jdbc);
        }

        for (ArchiveTask task : config.getTasks()) {
            final String tableName = task.getTable();

            try {
                // --- SAFETY: allow-list guard (prevents unintended tables) ---
                ensureTableAllowed(tableName);

                // --- GLOBAL DB-LEVEL LOCK (via provider) ---
                // Prevents concurrent processing of the same table across multiple JVMs/nodes/schedulers.
                if (!provider.tryLockTable(archiveJdbc, tableName)) {
                    LOGGER.info("[ARCHIVER] [" + tableName + "] Skipped (locked by another worker)");
                    continue;
                }

                try {
                    // --- TASK CONTEXT LOG ---
                    LOGGER.info("[ARCHIVER] Running task: " + task.getFunction() +
                            ", table: " + tableName +
                            ", olderThan: " + task.getAgeValue() + " " + task.getAgeUnit());

                    // --- TIME WINDOW CALCULATION ---
                    long now = System.currentTimeMillis();
                    long deltaMs = task.getAgeUnit().toMs(task.getAgeValue());
                    Timestamp beforeTs = new Timestamp(now - deltaMs);

                    // --- COLUMN DISCOVERY ---
                    String columns = getColumnList(provider, jdbc, tableName);
                    if (columns == null) {
                        LOGGER.warn("[ARCHIVER] Skipping task for table with unknown columns: " + tableName);
                        continue;
                    }

                    // --- ENSURE ARCHIVE TABLE EXISTS (copies/sanitizes DDL if needed) ---
                    ensureTableExistsByCopy(provider, jdbc, archiveJdbc, tableName);

                    // Schema guard: require archive to have at least all source columns; add missing if allowed
                    boolean schemaOk = ensureSchemaCompatible(provider, jdbc, archiveJdbc, tableName, true /* autoSyncAddColumns */);
                    if (!schemaOk) {
                        LOGGER.error("[ARCHIVER] [" + tableName + "] Schema not compatible; task skipped.");
                        continue;
                    }

                    ensureArchivedAtColumn(provider, archiveJdbc, tableName);

                    // --- EXECUTE TASK FUNCTION ---
                    switch (task.getFunction()) {
                        case COPY_TO_ARCHIVE:
                            archiveTable(provider, tableName, columns, beforeTs, jdbc, archiveJdbc, batchSize);
                            break;
                        case DELETE_IF_IN_ARCHIVE:
                            deleteFromSourceIfInArchive(provider, tableName, columns, beforeTs, jdbc, archiveJdbc, batchSize);
                            break;
                        default:
                            LOGGER.warn("[ARCHIVER] Unknown function for table " + tableName + ": " + task.getFunction());
                    }
                } finally {
                    try {
                        provider.releaseLockTable(archiveJdbc, tableName);
                    } catch (Exception e) {
                        LOGGER.warn("[ARCHIVER] Unlock failed for " + tableName + ": " + e.getMessage());
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("[ARCHIVER] Task failed: table=" + tableName + ", func=" + task.getFunction()
                        + " -> " + ex.getMessage(), ex);
            }
        }
    }

    private void archiveTable(
            IArchiveQueryProvider provider,
            String tableName,
            String selectColumns,
            Timestamp beforeTs,
            JdbcTemplate jdbc,
            JdbcTemplate archiveJdbc,
            int batchSize
    ) {
        String tsColumn = provider.getTimestampColumnName(tableName);
        // Batch counter to guard against infinite loops or runaway jobs.
        int batches = 0;

        boolean haveCursor = false;
        long lastTs = Long.MIN_VALUE;
        long lastId = Long.MIN_VALUE;
        while (true) {
            // --- Build SQL via provider ---
            String selectSql = provider.buildSelectBatchSql(tableName, selectColumns, tsColumn, haveCursor, batchSize);
            List<Object> args = provider.bindSelectBatchArgs(beforeTs.getTime(), haveCursor, lastTs, lastId);

            // --- Fetch batch from SOURCE DB ---
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, args.toArray());
            if (rows.isEmpty()) break;

            // --- Extract IDs from current window ---
            List<Long> idList = rows.stream()
                    .map(r -> (Number) r.get("id"))
                    .filter(Objects::nonNull)
                    .map(Number::longValue)
                    .collect(Collectors.toList());

            // --- Verify which IDs already exist in ARCHIVE DB ---
            Set<Long> existingIds = new HashSet<>();
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Long> chunk = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String verifySql = provider.buildVerifyIdsSql(tableName, chunk.size());
                existingIds.addAll(
                        archiveJdbc.query(
                                verifySql,
                                (rs, rowNum) -> rs.getLong("id"),
                                chunk.toArray()
                        )
                );
            }

            // --- Filter out rows already present in archive ---
            List<Map<String, Object>> filteredRows = rows.stream()
                    .filter(r -> {
                        Number n = (Number) r.get("id");
                        return n != null && !existingIds.contains(n.longValue());
                    })
                    .collect(Collectors.toList());

            if (filteredRows.isEmpty()) {
                LOGGER.info("[ARCHIVER] [" + tableName + "] All rows already exist in archive, skipping insert.");
            } else {
                // --- Build INSERT via provider (placeholders and column list) ---
                String auditCol = provider.archivedAtColumnName();
                String insertSql = provider.buildInsertWithExtraColumnSql(tableName, selectColumns, auditCol);
                String[] colsArr = selectColumns.split("\\s*,\\s*");

                final long archivedAtMillis = System.currentTimeMillis();

                withArchiveTx(provider, archiveJdbc, () ->
                        archiveJdbc.batchUpdate(insertSql, filteredRows, batchSize, (ps, row) -> {
                            for (int i = 0; i < colsArr.length; i++) {
                                ps.setObject(i + 1, row.get(colsArr[i].trim()));
                            }
                            ps.setObject(colsArr.length + 1, archivedAtMillis);
                        })
                );

                // --- Post-insert verify (count-based) ---
                List<Long> insertedIds = filteredRows.stream()
                        .map(r -> (Number) r.get("id"))
                        .filter(Objects::nonNull)
                        .map(Number::longValue)
                        .collect(Collectors.toList());

                int verifiedInArchive = 0;
                for (int i = 0; i < insertedIds.size(); i += batchSize) {
                    List<Long> chunk = insertedIds.subList(i, Math.min(i + batchSize, insertedIds.size()));
                    String countSql = provider.buildCountIdsSql(tableName, chunk.size());
                    Integer c = archiveJdbc.queryForObject(countSql, Integer.class, chunk.toArray());
                    if (c != null) verifiedInArchive += c;
                }

                if (verifiedInArchive != insertedIds.size()) {
                    LOGGER.warn("[ARCHIVER] [" + tableName + "] Post-insert verify mismatch: expected=" +
                            insertedIds.size() + ", found=" + verifiedInArchive + " in archive.");
                } else {
                    LOGGER.info("[ARCHIVER] [" + tableName + "] Post-insert verify OK: " + verifiedInArchive + " rows present.");
                }

                LOGGER.info("[ARCHIVER] [" + tableName + "] Inserted batch: " + filteredRows.size());
            }

            batches++;
            if (batches >= MAX_BATCHES_PER_TASK) {
                LOGGER.warn("[ARCHIVER] [" + tableName + "] Reached max batches per task (" + MAX_BATCHES_PER_TASK + "), stopping early.");
                break;
            }

            // --- Advance cursor (stable pagination by ts,id) ---
            Map<String, Object> lastRow = rows.get(rows.size() - 1);
            lastTs = ((Number) lastRow.get(tsColumn)).longValue();
            lastId = ((Number) lastRow.get("id")).longValue();
            haveCursor = true;

            if (rows.size() < batchSize) break;
        }
    }

    private void deleteFromSourceIfInArchive(
            IArchiveQueryProvider provider,
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
            // --- Build SQL via provider ---
            String selectSql = provider.buildSelectBatchSql(tableName, selectColumns, tsColumn, haveCursor, batchSize);
            List<Object> args = provider.bindSelectBatchArgs(beforeTs.getTime(), haveCursor, lastTs, lastId);

            // --- Fetch batch from SOURCE DB ---
            List<Map<String, Object>> rows = jdbc.queryForList(selectSql, args.toArray());

            // --- Extract IDs from current window ---
            List<Long> idList = rows.stream()
                    .map(r -> (Number) r.get("id"))
                    .filter(Objects::nonNull)
                    .map(Number::longValue)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) break;

            // --- Verify which IDs exist in ARCHIVE DB ---
            List<Long> verifiedIds = new ArrayList<>();
            for (int i = 0; i < idList.size(); i += batchSize) {
                List<Long> chunk = idList.subList(i, Math.min(i + batchSize, idList.size()));
                String verifySql = provider.buildVerifyIdsSql(tableName, chunk.size());
                verifiedIds.addAll(
                        archiveJdbc.query(
                                verifySql,
                                (rs, rowNum) -> rs.getLong("id"),
                                chunk.toArray()
                        )
                );
            }
            LOGGER.info("[ARCHIVER] [" + tableName + "] Found in archive: " + verifiedIds.size());

            // --- Delete verified IDs from SOURCE DB in chunks (transaction per chunk) ---
            if (!verifiedIds.isEmpty()) {
                final AtomicInteger deletedCounter = new AtomicInteger(0);
                for (int i = 0; i < verifiedIds.size(); i += batchSize) {
                    List<Long> chunk = verifiedIds.subList(i, Math.min(i + batchSize, verifiedIds.size()));
                    String deleteSql = provider.buildDeleteChunkSql(tableName, chunk.size());

                    withSourceTx(jdbc, () -> {
                        int d = jdbc.update(deleteSql, chunk.toArray());
                        deletedCounter.addAndGet(d);
                    });
                }
                LOGGER.info("[ARCHIVER] [" + tableName + "] Deleted from source: " + deletedCounter.get());
            }

            // --- Advance cursor ---
            Map<String, Object> lastRow = rows.get(rows.size() - 1);
            lastTs = ((Number) lastRow.get(tsColumn)).longValue();
            lastId = ((Number) lastRow.get("id")).longValue();
            haveCursor = true;

            if (rows.size() < batchSize) break;
        }
    }

    private void ensureTableExistsByCopy(IArchiveQueryProvider provider,
                                         JdbcTemplate sourceJdbc,
                                         JdbcTemplate targetJdbc,
                                         String tableName) {
        if (!provider.tableExists(targetJdbc, tableName)) {
            LOGGER.warn("[ARCHIVER] Table '" + tableName + "' does not exist in archive DB. Copying structure from primary...");
            try {
                String ddl;
                if (provider instanceof PostgresArchiveQueryProvider) {
                    ddl = provider.showCreateTable(tableName);
                    targetJdbc.execute(ddl);
                } else {
                    ddl = sourceJdbc.queryForObject(
                            provider.showCreateTable(tableName),
                            (rs, rowNum) -> rs.getString(2) // kolumna z CREATE TABLE w SHOW CREATE
                    );
                    ddl = sanitizeDDL(ddl);
                    targetJdbc.execute(ddl);
                }
                LOGGER.info("[ARCHIVER] Table '" + tableName + "' created in archive DB from source.");
            } catch (Exception e) {
                LOGGER.error("[ARCHIVER] Failed to copy structure for table '" + tableName + "': " + e.getMessage(), e);
            }
        }
    }

    // Ensure the archive table has the 'archived_at' audit column; add it if missing.
    private void ensureArchivedAtColumn(IArchiveQueryProvider provider, JdbcTemplate archiveJdbc, String tableName) {
        final String col = provider.archivedAtColumnName();
        try {
            if (!provider.hasColumn(archiveJdbc, tableName, col)) {
                String ddl = provider.buildAddColumnSql(tableName, col, provider.archivedAtSqlType());
                archiveJdbc.execute(ddl);
                LOGGER.info("[ARCHIVER] [" + tableName + "] Added audit column: " + col + " " + provider.archivedAtSqlType());
            }
        } catch (Exception e) {
            LOGGER.error("[ARCHIVER] [" + tableName + "] Failed to ensure audit column '" + col + "': " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Sanitizes the raw DDL statement obtained from SHOW CREATE TABLE (MySQL/MariaDB).
     * Removes engine-specific options, auto-increment markers, foreign key constraints,
     * and indexes to make the statement safe for creating simplified archive tables.
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

    private String getColumnList(IArchiveQueryProvider provider, JdbcTemplate jdbc, String tableName) {
        try {
            List<String> columnNames = provider.listColumns(jdbc, tableName);
            if (columnNames == null || columnNames.isEmpty()) {
                return null;
            }
            return String.join(", ", columnNames);
        } catch (Exception ex) {
            LOGGER.error("[ARCHIVER] Failed to get columns for table: " + tableName + " — " + ex.getMessage());
            return null;
        }
    }


    private void ensureTableAllowed(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Empty table name");
        }
        String base = tableName.contains(".") ? tableName.substring(tableName.indexOf('.') + 1) : tableName;
        if (!ALLOWED_TABLES.contains(base)) {
            throw new IllegalArgumentException("Table not allowed for archiving: " + tableName);
        }
    }

    // Ensure archive table schema is compatible with source schema.
// If autoSyncAddColumns = true, we will attempt to ADD any missing columns into archive (types copied from source).
// We do NOT drop or alter existing columns in archive. We only require that archive ⊇ source.
    private boolean ensureSchemaCompatible(IArchiveQueryProvider provider,
                                           JdbcTemplate sourceJdbc,
                                           JdbcTemplate archiveJdbc,
                                           String tableName,
                                           boolean autoSyncAddColumns) {
        try {
            List<String> srcCols = provider.listColumns(sourceJdbc, tableName);
            List<String> dstCols = provider.listColumns(archiveJdbc, tableName);

            if (srcCols == null || srcCols.isEmpty()) {
                LOGGER.error("[ARCHIVER] [" + tableName + "] Source columns not found; skipping.");
                return false;
            }
            if (dstCols == null) {
                LOGGER.warn("[ARCHIVER] [" + tableName + "] Archive columns not found; will try to create table first.");
                return false;
            }

            // Compute missing columns: present in source but absent in archive
            Set<String> dstSet = new HashSet<>(dstCols);
            List<String> missing = new ArrayList<>();
            for (String c : srcCols) {
                if (!dstSet.contains(c)) missing.add(c);
            }

            if (missing.isEmpty()) {
                // Archive has at least all source columns (extra columns are fine).
                return true;
            }

            LOGGER.warn("[ARCHIVER] [" + tableName + "] Archive is missing columns: " + missing);

            if (!autoSyncAddColumns) {
                // Strict mode: do not touch schema, skip task.
                LOGGER.error("[ARCHIVER] [" + tableName + "] Schema mismatch (strict). Skipping task.");
                return false;
            }

            // Auto-sync: add only missing columns with their source types
            Map<String,String> srcTypes = provider.columnTypes(sourceJdbc, tableName);
            for (String col : missing) {
                String t = srcTypes.get(col);
                if (t == null || t.isBlank()) {
                    LOGGER.warn("[ARCHIVER] [" + tableName + "] Missing column type for '" + col + "'. Skipping column.");
                    continue;
                }
                String ddl = provider.buildAddColumnSql(tableName, col, t);
                try {
                    archiveJdbc.execute(ddl);
                    LOGGER.info("[ARCHIVER] [" + tableName + "] Added missing column in archive: " + col + " " + t);
                } catch (Exception e) {
                    LOGGER.error("[ARCHIVER] [" + tableName + "] Failed to add column '" + col + "': " + e.getMessage(), e);
                    // If adding a column fails, better stop the task to avoid partial state.
                    return false;
                }
            }

            // Re-check after sync
            List<String> dstCols2 = provider.listColumns(archiveJdbc, tableName);
            Set<String> dstSet2 = new HashSet<>(dstCols2);
            for (String c : srcCols) {
                if (!dstSet2.contains(c)) {
                    LOGGER.error("[ARCHIVER] [" + tableName + "] Schema still incompatible after sync; skipping.");
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            LOGGER.error("[ARCHIVER] [" + tableName + "] ensureSchemaCompatible failed: " + e.getMessage(), e);
            return false;
        }
    }


    // --- LOCKS (anti-double-run) ---
    // Try to acquire a DB-level lock for a given table name in the ARCHIVE database.
    // This prevents parallel workers (other JVMs/nodes/CRONs) from touching the same table concurrently.
    private boolean tryLockTable(IArchiveQueryProvider provider, JdbcTemplate archiveJdbc, String tableName) {
        try {
            if (provider instanceof MySqlArchiveQueryProvider) {
                String lockName = "archiver:" + tableName;
                Boolean ok = archiveJdbc.queryForObject("SELECT GET_LOCK(?, 0)", Boolean.class, lockName);
                return Boolean.TRUE.equals(ok);
            } else if (provider instanceof PostgresArchiveQueryProvider) {
                // PostgreSQL advisory lock uses a BIGINT key. We derive a stable 64-bit key from md5(tableName).
                String key = "archiver:" + tableName;
                Boolean ok = archiveJdbc.queryForObject(
                        "SELECT pg_try_advisory_lock( abs(('x'||substr(md5(?),1,16))::bit(64)::bigint) )",
                        Boolean.class, key
                );
                return Boolean.TRUE.equals(ok);
            } else {
                // Fallback for other providers: do not block the job if we cannot lock at DB level.
                return true;
            }
        } catch (Exception e) {
            // If locking fails for any reason, skip the task to avoid race conditions.
            LOGGER.warn("[ARCHIVER] Lock attempt failed for " + tableName + ": " + e.getMessage());
            return false;
        }
    }

    // Release the DB-level advisory lock for the given table.
    private void releaseLockTable(IArchiveQueryProvider provider, JdbcTemplate archiveJdbc, String tableName) {
        try {
            if (provider instanceof MySqlArchiveQueryProvider) {
                String lockName = "archiver:" + tableName;
                archiveJdbc.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, lockName);
            } else if (provider instanceof PostgresArchiveQueryProvider) {
                String key = "archiver:" + tableName;
                archiveJdbc.queryForObject(
                        "SELECT pg_advisory_unlock( abs(('x'||substr(md5(?),1,16))::bit(64)::bigint) )",
                        Boolean.class, key
                );
            }
        } catch (Exception e) {
            // Unlock errors are non-fatal; just log them.
            LOGGER.warn("[ARCHIVER] Unlock failed for " + tableName + ": " + e.getMessage());
        }
    }

    // --- TX HELPERS & TIMEOUTS ---

    // Wrap archive DB work in a transaction with a timeout.
    // Also asks provider to apply DB-side statement timeout (e.g., PG: SET LOCAL statement_timeout).
    private void withArchiveTx(IArchiveQueryProvider provider, JdbcTemplate archiveJdbc, Runnable work) {
        var tm = new org.springframework.jdbc.datasource.DataSourceTransactionManager(archiveJdbc.getDataSource());
        var tt = new org.springframework.transaction.support.TransactionTemplate(tm);
        tt.setTimeout(BATCH_TX_TIMEOUT_SECONDS); // Spring-level timeout (transaction scope)

        tt.execute(status -> {
            // Apply DB-side timeout for this transaction (provider-specific, e.g. PG/MySQL).
            try {
                provider.applyPerTransactionTimeout(archiveJdbc, BATCH_TX_TIMEOUT_SECONDS);
            } catch (Exception e) {
                // Non-fatal: DBs without such setting can ignore.
                LOGGER.debug("[ARCHIVER] Could not apply per-tx timeout: " + e.getMessage());
            }

            try {
                work.run();
            } finally {
                // Always clear/restore DB-side timeout when leaving the transaction.
                try {
                    provider.clearPerTransactionTimeout(archiveJdbc);
                } catch (Exception e) {
                    LOGGER.debug("[ARCHIVER] Could not clear per-tx timeout: " + e.getMessage());
                }
            }
            return null;
        });
    }

    // Wrap source DB work (no provider-specific DB timeout here; we only enforce Spring tx timeout).
    private void withSourceTx(JdbcTemplate jdbc, Runnable work) {
        var tm = new org.springframework.jdbc.datasource.DataSourceTransactionManager(jdbc.getDataSource());
        var tt = new org.springframework.transaction.support.TransactionTemplate(tm);
        tt.setTimeout(BATCH_TX_TIMEOUT_SECONDS); // Spring-level timeout (transaction scope)

        tt.execute(status -> { work.run(); return null; });
    }
}
