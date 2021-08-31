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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class V2_7_1_0__CreatePointValuesDenormalized extends BaseJavaMigration {


    private static final Log LOG = LogFactory.getLog(V2_7_1_0__CreatePointValuesDenormalized.class);

    @Override
    public void migrate(Context context) throws Exception {

        File schema = new File("questdb-schema.json");
        int limit = Common.getEnvironmentProfile().getInt("db.values.export.limit", 1001);

        int i;
        for (i = 0 ; migrationNext(schema, i, limit); i = i + limit) {}


        final JdbcTemplate jdbcTemplate = DAO.query().getJdbcTemp();

        final String createEmptyQuery = "CREATE TABLE IF NOT EXISTS pointValuesDenormalized (" +
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

        jdbcTemplate.execute(createEmptyQuery);

    }

    private boolean migrationNext(File schema, int offset, int limit) {

        AtomicBoolean hasNext = new AtomicBoolean(true);
        createCsvTemp().ifPresent(csv -> {
                    try {
                        exportFromMysqlToCsv(csv, offset, limit);
                        if(csv.length() > 0)
                            importToQuestDb(schema, csv);
                        else
                            hasNext.set(false);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    } finally {
                        LOG.info(offset);
                        delete(csv);
                    }
                });
        return hasNext.get();
    }

    public static int exportFromMysqlToCsv(File csv, int offset, int limit) {
        String query = "select * from pointValuesDenormalized OFFSET " + offset + " LIMIT " + limit;
        String [] exportFromMysqlCommand = {"mysql", "-u", "root", "-proot", "scadalts", "-e", query, "-B"};

        try {
           int result = new ProcessBuilder(exportFromMysqlCommand)
                    .redirectOutput(csv)
                    .start()
                    .waitFor();
            LOG.info("MySql export finished: " + result);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return 0;
    }

    public static void importToQuestDb(File schema, File csv) throws IOException {
        /*String schema = "schema=[{\"name\":\"timestamp\", \"type\": \"TIMESTAMP\", \"pattern\": \"yyyy-MM-ddTHH:mm:ss.SSSZ\"},{\"name\":\"textPointValueShort\", \"type\": \"SYMBOL\"},{\"name\":\"textPointValueLong\", \"type\": \"SYMBOL\"},{\"name\":\"sourceType\", \"type\": \"INT\"},{\"name\":\"sourceId\", \"type\": \"INT\"},{\"name\":\"username\", \"type\": \"SYMBOL\"},{\"name\":\"ts\", \"type\": \"LONG\"}]";
        String [] importToQuestDbCommand = {"curl", "-F", schema, "-F", "data=@" + csv.getAbsolutePath(), "http://localhost:9000/imp?overwrite=true&name=pointValuesDenormalized&timestamp=timestamp&partitionBy=DAY"};

        int result = new ProcessBuilder(importToQuestDbCommand)
                .start()
                .waitFor();
        LOG.info("QuestDb import finished: " + result);*/

        String code = sendFiles("http://localhost:9000/imp?overwrite=true&name=pointValuesDenormalized&timestamp=timestamp&partitionBy=DAY",
                "UTF-8", new FileWithInfo(schema, "Content-Type: application/octet-stream", "schema"),
                new FileWithInfo(csv, "Content-Type: application/octet-stream", "data"));

        LOG.info("QuestDb import finished: " + code);
    }

    public static void delete(File csv) {
        try {
            Files.delete(csv.toPath());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static Optional<File> createCsvTemp() {
        try {
            return Optional.of(File.createTempFile("pointValues",".csv"));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private static String sendFiles(String url, String charsetName, FileWithInfo... files) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        // connection.setConnectTimeout(10000);
        // connection.setReadTimeout(10000);
        connection.setDoOutput(true);
        createRequest(connection, charsetName, files);
        return responseToString(connection);
    }

    private static void createRequest(HttpURLConnection connection, String charsetName, FileWithInfo[] files) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Expect", "100-continue");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), charsetName))) {
            for (FileWithInfo file : files) {
                if (file.file.isDirectory()) {
                    continue;
                }
                writer.println("--" + boundary);
                writer.println("Content-Disposition: form-data; name=\"" + file.name + "\"; filename=\"" + file.file.getName() + "\"");
                writer.println(file.contentType);
                writer.println();
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.file), charsetName))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
            }
            writer.println();
            writer.println("--" + boundary + "--");
            writer.println();
        }
    }

    private static String responseToString(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        InputStream inputStream = getInputStream(responseCode, connection);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            return response.toString();
        }
    }

    private static InputStream getInputStream(int responseCode, HttpURLConnection connection) throws IOException {
        return responseCode >= 200 && responseCode <= 299 ?  connection.getInputStream() : connection.getErrorStream();
    }

    static class FileWithInfo {
        private final File file;
        private final String contentType;
        private final String name;

        public FileWithInfo(File file, String contentType, String name) {
            this.file = file;
            this.contentType = contentType;
            this.name = name;
        }
    }
}
