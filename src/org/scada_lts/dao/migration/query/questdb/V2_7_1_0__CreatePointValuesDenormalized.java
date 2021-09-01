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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class V2_7_1_0__CreatePointValuesDenormalized extends BaseJavaMigration {


    private static final Log LOG = LogFactory.getLog(V2_7_1_0__CreatePointValuesDenormalized.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcOperations questdb = DAO.query().getJdbcTemp();
        final JdbcOperations mysql = DAO.getInstance().getJdbcTemp();

        URL resource = V2_7_1_0__CreatePointValuesDenormalized.class.getClassLoader().getResource("questdb-schema.json");
        File schema = Paths.get(resource.toURI()).toFile();
        int limit = Common.getEnvironmentProfile().getInt("db.values.export.limit", 1001);

        for (int i = 0 ; migrationNext(schema, i, limit, mysql); i += limit) {}

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

        questdb.execute(createEmptyTableQuery);

    }

    private static boolean migrationNext(File schema, int offset, int limit, JdbcOperations mysql) {
        if(schema == null || !schema.exists() || offset < 0  || limit <= 0 || mysql == null) {
            return false;
        }
        AtomicBoolean exportedToCsv = new AtomicBoolean(false);
        AtomicInteger rowsImported = new AtomicInteger(0);
        createAccessDir(mysql)
                .flatMap(path -> exportToCsv(path, offset, limit, mysql))
                .ifPresent(csv -> {
                    exportedToCsv.set(true);
                    try {
                        if (csv.length() > 0) {
                            int imported = importToQuestDb(schema, csv, false);
                            rowsImported.set(imported);
                        }
                    } finally {
                        delete(csv);
                    }
                });
        LOG.info("last offset: " + (offset + rowsImported.get()));
        return exportedToCsv.get() && rowsImported.get() == limit;
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

    public static Optional<File> exportToCsv(String csv, int offset, int limit, JdbcOperations jdbcOperations) {
       /* String query = "select * from pointValuesDenormalized LIMIT " + limit + " OFFSET " + offset;
        String [] exportFromMysqlCommand = {"mysql", "-u", "root", "-proot", "scadalts_12", "-e", query, "-B"};
        try {
           int result = new ProcessBuilder(exportFromMysqlCommand)
                    .redirectOutput(csv)
                    .start()
                    .waitFor();
            LOG.info("MySql export finished: " + result);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;*/

        String query = "SELECT * FROM (" +
                "    SELECT 'dataPointId', 'dataType', 'pointValue', 'ts', 'timestamp', 'textPointValueShort', 'textPointValueLong', 'sourceType', 'sourceId', 'username' UNION ALL" +
                "    (" +
                "       SELECT * FROM pointValuesDenormalized LIMIT " + limit + " OFFSET " + offset + " " +
                "    )" +
                ") result INTO OUTFILE '" + csv + "' FIELDS TERMINATED BY '\t' ENCLOSED BY '' LINES TERMINATED BY '\r\n';";

        try {
            jdbcOperations.execute(query);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
        return Optional.of(new File(csv));
    }

    public static int importToQuestDb(File schema, File csv, boolean overwrite) {
        /*String schema = "schema=[{\"name\":\"timestamp\", \"type\": \"TIMESTAMP\", \"pattern\": \"yyyy-MM-ddTHH:mm:ss.SSSZ\"},{\"name\":\"textPointValueShort\", \"type\": \"SYMBOL\"},{\"name\":\"textPointValueLong\", \"type\": \"SYMBOL\"},{\"name\":\"sourceType\", \"type\": \"INT\"},{\"name\":\"sourceId\", \"type\": \"INT\"},{\"name\":\"username\", \"type\": \"SYMBOL\"},{\"name\":\"ts\", \"type\": \"LONG\"}]";
        String [] importToQuestDbCommand = {"curl", "-F", schema, "-F", "data=@" + csv.getAbsolutePath(), "http://localhost:9000/imp?overwrite=true&name=pointValuesDenormalized&timestamp=timestamp&partitionBy=DAY"};

        int result = new ProcessBuilder(importToQuestDbCommand)
                .start()
                .waitFor();
        LOG.info("QuestDb import finished: " + result);*/
        try {
            PostMethod postMethod = new PostMethod("http://localhost:9000/imp?name=pointValuesDenormalized&timestamp=timestamp&partitionBy=DAY&overwrite=" + overwrite);
            MultipartRequestEntity entity = new MultipartRequestEntity(new Part[]{new FilePart("schema", schema), new FilePart("data", csv)}, postMethod.getParams());
            postMethod.setRequestEntity(entity);
            MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
            HttpClient httpClient = new HttpClient(manager);
            httpClient.executeMethod(postMethod);
            String response = postMethod.getResponseBodyAsString();
            LOG.info(response);
            return parseRowsImported(response);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
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
        return -1;
    }

    public static void delete(File csv) {
        try {
            Files.delete(csv.toPath());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
