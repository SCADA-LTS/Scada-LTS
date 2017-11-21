/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.permissions;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class PermissionEvaluatorAcl {

    private static PermissionEvaluatorAcl instance = null;

    public static PermissionEvaluatorAcl getInstance() {
        if (instance == null) {
            instance = new PermissionEvaluatorAcl();
        }
        return instance;
    }

    private PermissionEvaluatorAcl() {
    }

    public List<IFilter> filter(String clazz, String sid) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8090/api/permission/filter");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        String inputFilter="{" +
                                "\"entityClass\": { " +
                                    "\"className\": \""+clazz+"\","+
                                    "\"id\": 0 "+
                                "},"+
                                "\"permision\": {"+
                                    "\"mask\": \"1\""+
                                "},"+
                                "\"sid\": {"+
                                    "\"id\": 0,"+
                                    "\"principal\": 0,"+
                                    "\"sid\": \""+sid+"\""+
                                "}"+
                            "}";

        StringEntity entity = new StringEntity(inputFilter);
        httpPost.setEntity(entity);

        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }

        return null;


    }

}
