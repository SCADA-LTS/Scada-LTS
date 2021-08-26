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

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Optional;

public class V2_7_1_0__CreatePointValuesDenormalized extends BaseJavaMigration {


    private static final Log LOG = LogFactory.getLog(V2_7_1_0__CreatePointValuesDenormalized.class);

    @Override
    public void migrate(Context context) throws Exception {

        createCsvTemp().ifPresent(csv -> {
            try {
                exportFromMysqlToCsv("", csv);
                importToQuestDb(csv);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            } finally {
                delete(csv);
            }
        });
    }

    public static void exportFromMysqlToCsv(String condition, File csv) throws IOException, InterruptedException {
        String query = "select * from pointValuesDenormalized " + condition;
        String [] exportFromMysqlCommand = {"mysql", "-u", "root", "-proot", "scadalts", "-e", query, "-B"};

        int result = new ProcessBuilder(exportFromMysqlCommand)
                .redirectOutput(csv)
                .start()
                .waitFor();
        LOG.info("MySql export finished: " + result);
    }

    public static void importToQuestDb(File csv) throws IOException, InterruptedException {
        /*String schema = "schema=[{\"name\":\"ts\", \"type\": \"TIMESTAMP\", \"pattern\": \"yyyy-MM-ddTHH:mm:ss.SSSZ\"},{\"name\":\"textPointValueShort\", \"type\": \"SYMBOL\"},{\"name\":\"textPointValueLong\", \"type\": \"SYMBOL\"},{\"name\":\"sourceType\", \"type\": \"INT\"},{\"name\":\"sourceId\", \"type\": \"INT\"},{\"name\":\"username\", \"type\": \"SYMBOL\"}]";
        String [] importToQuestDbCommand = {"curl", "-F", schema, "-F", "data=@" + csv.getAbsolutePath(), "http://localhost:9000/imp?overwrite=true&name=pointValuesDenormalized&timestamp=ts&partitionBy=DAY"};

        int result = new ProcessBuilder(importToQuestDbCommand)
                .start()
                .waitFor();
        LOG.info("QuestDb import finished: " + result);*/


        NameValuePair schema = new NameValuePair("schema", "[{\"name\":\"ts\", \"type\": \"TIMESTAMP\", \"pattern\": \"yyyy-MM-ddTHH:mm:ss.SSSZ\"},{\"name\":\"textPointValueShort\", \"type\": \"SYMBOL\"},{\"name\":\"textPointValueLong\", \"type\": \"SYMBOL\"},{\"name\":\"sourceType\", \"type\": \"INT\"},{\"name\":\"sourceId\", \"type\": \"INT\"},{\"name\":\"username\", \"type\": \"SYMBOL\"}]");
        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        PostMethod postMethod = new PostMethod("http://localhost:9000/imp?overwrite=true&name=pointValuesDenormalized&timestamp=ts&partitionBy=DAY");
        postMethod.addParameter(schema);
        MultipartRequestEntity entity = new MultipartRequestEntity(new Part[] {new FilePart("data", csv)}, postMethod.getParams());
        postMethod.setRequestEntity(entity);
        int result = client.executeMethod(postMethod);
        LOG.info("QuestDb import finished: " + result);
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
}
