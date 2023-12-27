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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.scada_lts.permissions.model.EntryDto;

import java.io.IOException;
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

    @Override
    public List<EntryDto> filter(long userId, long clazzId ) {

        List<EntryDto> result = null;
        try {

            //TODO to optimalization
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet request = new HttpGet(ACLConfig.getInstance().getUrlACL()+"/filter/"+userId+"/"+clazzId);
            request.addHeader("User-Agent", USER_AGENT);
            request.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<EntryDto>>() {});

            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

        } catch (JsonParseException e1) {
            LOG.error(e1);
        } catch (JsonMappingException e1) {
            LOG.error(e1);
        } catch (ClientProtocolException e1) {
            LOG.error(e1);
        } catch (IOException e1) {
            LOG.error(e1);
        }

        return result;

    }

}
