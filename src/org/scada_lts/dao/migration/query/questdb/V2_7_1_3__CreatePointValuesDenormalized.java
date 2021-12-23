/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.dao.migration.query.questdb;

import com.serotonin.mango.Common;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcOperations;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class V2_7_1_3__CreatePointValuesDenormalized extends BaseJavaMigration {


    private static final Log LOG = LogFactory.getLog(V2_7_1_3__CreatePointValuesDenormalized.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcOperations questdb = DAO.query().getJdbcTemp();
        final JdbcOperations mysql = DAO.getInstance().getJdbcTemp();

        URL resource = V2_7_1_3__CreatePointValuesDenormalized.class.getClassLoader().getResource("questdb-schema.json");
        File schema = Paths.get(Objects.requireNonNull(resource).toURI()).toFile();
        int limit = Common.getEnvironmentProfile().getInt("db.values.export.limit", 1001);
        boolean overwrite = Common.getEnvironmentProfile().getBoolean("dbquery.import.overwrite", false);
        boolean valuesImportEnabled = Common.getEnvironmentProfile().getBoolean("dbquery.values.import.enabled", true);

        List<Integer> dataPoints = mysql.queryForList("select dataPointId from pointValues group by dataPointId order by dataPointId", Integer.class);

        if(overwrite) {
            try {
                List<String> tables = questdb.queryForList("SHOW TABLES;", String.class);

                for(String table: tables) {
                    if(!table.equalsIgnoreCase("telemetry")
                            && !table.equalsIgnoreCase("telemetry_config")
                            && table.contains("pointValues")) {
                        String dropQuery = "DROP TABLE " + table + ";";
                        questdb.execute(dropQuery);
                    }
                }

            } catch (Exception ex) {
                LOG.warn(ex.getMessage());
            }
        }

        if(valuesImportEnabled) {

            MigrationSettings migrationSettings = MigrationSettings.builder()
                    .toRead(mysql)
                    .overwrite(false)
                    .schema(schema)
                    .build();

            for (int dataPoint : dataPoints) {
                LOG.info("Datapoint: " + dataPoint);
                for (int i = 0; migrationNext(PaginationParams.params(i, limit, dataPoint), migrationSettings); i += limit) {
                }
            }
        }
/*
        final String createEmptyTableQuery = "CREATE TABLE IF NOT EXISTS pointValuesDenormalized (" +
                "dataPointId INT, " +
                "dataType INT, " +
                "pointValue DOUBLE, " +
                "ts LONG, " +
                "timestamp TIMESTAMP, " +
                "textPointValueShort SYMBOL, " +
                "textPointValueLong SYMBOL, " +
                "sourceType INT, " +
                "sourceId INT, " +
                "username SYMBOL) timestamp(timestamp) PARTITION BY DAY;";

        questdb.execute(createEmptyTableQuery);*/

    }

    private static boolean migrationNext(PaginationParams paginationParams,
                                         MigrationSettings migrationSettings) {
        if(migrationSettings.getSchema() == null
                || !migrationSettings.getSchema().exists()
                || paginationParams.getOffset() < 0
                || paginationParams.getLimit() <= 0
                || migrationSettings.getToRead() == null) {
            LOG.warn(migrationSettings);
            return false;
        }
        AtomicBoolean exportedToCsv = new AtomicBoolean(false);
        AtomicInteger rowsImported = new AtomicInteger(0);
        createAccessDir(migrationSettings.getToRead())
                .flatMap(dir -> exportToCsv(dir, paginationParams, migrationSettings.getToRead()))
                .ifPresent(csv -> {
                    exportedToCsv.set(true);
                    try {
                        if (csv.length() > 0) {
                            int imported = importToQuestDb(csv, migrationSettings, paginationParams.getDataPointId());
                            rowsImported.set(imported);
                        }
                    } finally {
                        delete(csv);
                    }
                });
        LOG.info("last offset: " + (paginationParams.getOffset() + rowsImported.get()));
        return exportedToCsv.get() && rowsImported.get() == paginationParams.getLimit();
    }


    private static Optional<String> createAccessDir(JdbcOperations jdbcOperations) {
        try {
            String secureFilePrivQuery = "SHOW VARIABLES LIKE \"secure_file_priv\"";
            String secureFilePriv = jdbcOperations.query(secureFilePrivQuery, (resultSet, i) -> resultSet.getString("Value")).get(0);
            if(secureFilePriv == null) {
                LOG.warn("The secure_file_priv constant in the mysql database has an empty null value set, this blocked the database from being exported to a file, set a value for this constant other than null:\n" +
                        "1) Setting the value to empty will make it possible to export to any location;\n" +
                        "2) Setting this parameter to a specific path will force the use of that particular path;\n" +
                        "\n" +
                        "The change requires a mysql server restart. Applies to mysql version 5.7.");
                return Optional.empty();
            }

            if(!secureFilePriv.isEmpty()) {
                File file = new File(secureFilePriv + File.separator + "pointValues" + System.currentTimeMillis() + ".csv");
                return Optional.of(changePath(file));
            }
            File file = File.createTempFile("pointValues",".csv");
            delete(file);
            return Optional.of(changePath(file));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private static String changePath(File file) {
        if(file.getAbsolutePath().contains("\\")) {
            return file.getAbsolutePath().replace("\\", "\\\\");
        }
        return file.getAbsolutePath();
    }

    public static Optional<File> exportToCsv(String csv,
                                             PaginationParams paginationParams,
                                             JdbcOperations jdbcOperations) {
        String query = "SELECT * FROM (" +
                "    SELECT 'timestamp', 'ts', 'pointValue', 'metaData', 'dataType' UNION ALL" +
                "    (" +
                "       SELECT timestamp, ts, pointValue, metaData, dataType " +
                " FROM pointValuesDenormalized WHERE dataPointId = " + paginationParams.getDataPointId() + " LIMIT " + paginationParams.getLimit() + " OFFSET " + paginationParams.getOffset() + " " +
                "    )" +
                ") result INTO OUTFILE '" + csv + "' FIELDS TERMINATED BY '\t' ENCLOSED BY '' LINES TERMINATED BY '\r\n';";

        try {
            jdbcOperations.execute(query);
            LOG.info("MySql export finished: OK");
            return Optional.of(new File(csv));
        } catch (Exception ex) {
            LOG.info("MySql export finished: ERROR");
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static int importToQuestDb(File csv, MigrationSettings migrationSettings, int dataPointId) {
        try {
            PostMethod postMethod = new PostMethod("http://localhost:9000/imp?name=pointValues" + dataPointId + "&timestamp=timestamp&partitionBy=MONTH&overwrite=" + migrationSettings.isOverwrite());
            MultipartRequestEntity entity = new MultipartRequestEntity(new Part[]{new FilePart("schema", migrationSettings.getSchema()), new FilePart("data", csv)}, postMethod.getParams());
            postMethod.setRequestEntity(entity);
            MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
            HttpClient httpClient = new HttpClient(manager);
            httpClient.executeMethod(postMethod);
            String response = postMethod.getResponseBodyAsString();
            LOG.info("QuestDb import finished: \r\n" + response);
            return parseRowsImported(response);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -3;
        }
    }

    private static int parseRowsImported(String response) {
        String[] lines = response.split("\r\n");
        for(String line: lines) {
            if(line.contains("Rows imported")) {
                LOG.info(line);
                String[] cols = line.split("([|])");
                try {
                    return Integer.parseInt(cols[2].trim());
                } catch (Exception ex) {
                    return -1;
                }
            }
        }
        return -2;
    }

    public static void delete(File csv) {
        try {
            Files.delete(csv.toPath());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static class PaginationParams {
        int offset;
        int limit;
        int dataPointId;

        private PaginationParams(int offset, int limit, int dataPointId) {
            this.offset = offset;
            this.limit = limit;
            this.dataPointId = dataPointId;
        }

        public static PaginationParams params(int offset, int limit, int dataPointId) {
            return new PaginationParams(offset, limit, dataPointId);
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }

        public int getDataPointId() {
            return dataPointId;
        }

        @Override
        public String toString() {
            return "PaginationParams{" +
                    "offset=" + offset +
                    ", limit=" + limit +
                    '}';
        }
    }

    private static class MigrationSettings {
        File schema;
        JdbcOperations toRead;
        boolean overwrite;

        private MigrationSettings(File schema, JdbcOperations toRead, boolean overwrite) {
            this.schema = schema;
            this.toRead = toRead;
            this.overwrite = overwrite;
        }

        public static MigrationSettings.Builder builder() {
            return new MigrationSettings.Builder();
        }

        private static class Builder {
            File schema;
            JdbcOperations toRead;
            boolean overwrite;

            public Builder schema(File schema) {
                this.schema = schema;
                return this;
            }

            public Builder toRead(JdbcOperations toRead) {
                this.toRead = toRead;
                return this;
            }

            public Builder overwrite(boolean overwrite) {
                this.overwrite = overwrite;
                return this;
            }

            public MigrationSettings build() {
                return new MigrationSettings(schema, toRead, overwrite);
            }
        }

        public File getSchema() {
            return schema;
        }

        public JdbcOperations getToRead() {
            return toRead;
        }

        public boolean isOverwrite() {
            return overwrite;
        }

        @Override
        public String toString() {
            return "MigrationSettings{" +
                    "schema=" + schema +
                    ", jdbcOperations=" + toRead.getClass().getSimpleName() +
                    ", overwrite=" + overwrite +
                    '}';
        }
    }
}
