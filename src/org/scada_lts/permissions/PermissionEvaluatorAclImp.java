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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.scada_lts.permissions.model.EntryDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class PermissionEvaluatorAclImp implements PermissionEvaluatorAcl {

    private static final Log LOG = LogFactory.getLog(PermissionEvaluatorAclImp.class);

    private HttpResponse response;

    private static PermissionEvaluatorAclImp instance = null;

    private PermissionEvaluatorAclImp() {
    }

    private boolean hasPermisionTo(String to, int userId, int classId, int entityIdentityId) {
        String charset = StandardCharsets.UTF_8.name();

        String url = ACLConfig.getInstance().getUrlACL() + "/"+to+"/"+userId+"/"+classId+"/"+entityIdentityId;

        HttpClient client = HttpClientBuilder.create().build();
        boolean result = false;
        try {
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(request);
            String strResult = EntityUtils.toString(response.getEntity());
            try {
                result = Boolean.parseBoolean(strResult);
            } catch (Exception e) {
                result = false;
            }
        } catch (ClientProtocolException e) {
            LOG.error(e);
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            LOG.info(" check url:"+url);
        }
        return result;
    }

    public static PermissionEvaluatorAclImp getInstance() {
        if (instance == null) {
            instance = new PermissionEvaluatorAclImp();
        }
        return instance;
    }

    @Override
    public boolean hasPermissionToWrite(int userId, int classId, int entityIdentityId) {
        return hasPermisionTo("hasPermissionToWrite", userId, classId, entityIdentityId );
    }

    @Override
    public boolean hasPermissionToRead(int userId, int classId, int entityIdentityId) {
        return hasPermisionTo("hasPermissionToRead", userId, classId, entityIdentityId );
    }

    @Override
    public boolean hasPermissionToExecute(int userId, int classId, int entityIdentityId) {
        return hasPermisionTo("hasPermissionToExecute", userId, classId, entityIdentityId );
    }

    //filter is always check isRead
    @Override
    public List<EntryDto> filter(long clazzId, long userId ) {

        List<EntryDto> result = null;
        try {
            /*if (ACLConfig.getInstance().isPermissionFromServerAcl()) {
                throw new Exception("Note the filter method is only provided for authentication from the ACL server");
            }*/

            //TODO optimalization
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(ACLConfig.getInstance().getUrlACL()+"/filter");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            String inputFilter = "{" +
            /*        "\"entityClass\": { " +
                    "\"className\": \"" + clazz + "\"," +
                    "\"id\":" + clazzId +
                    "}," +
                    "\"permision\": {" +
                    "\"mask\": \"1\"" +
                    "}," +
                    "\"sid\": {" +
                    "\"id\":" + sidId + "," +
                    "\"principal\":" + principalId + ", " +
                    "\"sid\": \"" + sid + "\"" +
                    "}" +*/
                    "}";

            StringEntity entity = new StringEntity(inputFilter);
            httpPost.setEntity(entity);

            CloseableHttpResponse response2 = httpclient.execute(httpPost);


            try {

                ObjectMapper mapper = new ObjectMapper();
                result = mapper.readValue(response2.getEntity().getContent(), new TypeReference<List<EntryDto>>() {
                });

                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                response2.close();
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        return result;

    }

}
