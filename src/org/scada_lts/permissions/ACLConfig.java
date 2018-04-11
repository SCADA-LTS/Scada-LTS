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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;

import java.io.IOException;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class ACLConfig {

    private static final Log LOG = LogFactory.getLog(ACLConfig.class);

    private static ACLConfig instance = null;

    private String serverACL;
    private boolean permissionFromServerAcl;
    private String urlACL;

    public static ACLConfig getInstance() {
        if (instance==null) {
            try {
                instance = new ACLConfig();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return instance;
    }

    //for test
    public ACLConfig(ACLConfig config) {
        instance = config;
    }

    private ACLConfig() throws IOException {
        permissionFromServerAcl = ScadaConfig.getInstance().getBoolean(ScadaConfig.USE_ACL, false);
        serverACL = ScadaConfig.getInstance().getProperty(ScadaConfig.ACL_SERVER);
        urlACL = serverACL+"/api/permission";
    }

    public String getServerACL() {
        return serverACL;
    }

    public boolean isPermissionFromServerAcl() {
        return permissionFromServerAcl;
    }

    public String getUrlACL() {
        return urlACL;
    }

}
