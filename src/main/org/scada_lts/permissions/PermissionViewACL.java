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
import org.scada_lts.permissions.model.EntryDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class PermissionViewACL {

    private static final Log LOG = LogFactory.getLog(PermissionViewACL.class);

    private static int CLAZZ_ID = 3;

    private static PermissionViewACL instance = null;

    private PermissionViewACL() {
        //
    }

    public static PermissionViewACL getInstance() {
        if (instance == null) {
            instance = new PermissionViewACL();
        }

        return instance;
    }

    public  boolean hasPermissionToRead(int userId, int entityIdentityId) {
        return PermissionEvaluatorAclImp.getInstance().hasPermissionToRead(userId, CLAZZ_ID, entityIdentityId);
    }

    public  boolean hasPermissionToWrite(int userId, int entityIdentityId) {
        return PermissionEvaluatorAclImp.getInstance().hasPermissionToRead(userId, CLAZZ_ID, entityIdentityId);
    }

    public boolean hasPermissionToExecute(int userId, int entityIdentityId) {
        return PermissionEvaluatorAclImp.getInstance().hasPermissionToRead(userId, CLAZZ_ID, entityIdentityId);
    }


    public Map<Integer, EntryDto> filter(int userId) {

        List<EntryDto> lst = PermissionEvaluatorAclImp.getInstance().filter(userId, CLAZZ_ID);

        LOG.info("Filter PermissionViewACL lst:" + lst.toString());
        Map<Integer, EntryDto> map = lst.stream().collect(
                Collectors.toMap(EntryDto::getId, EntryDto::getSelf));

        return map;
    }

}
