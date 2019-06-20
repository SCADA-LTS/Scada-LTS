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

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class PermisionFilterDataSource {

    private static final Log LOG = LogFactory.getLog(PermisionFilterDataSource.class);

    private static String CLAZZ = "org.scada_lts.domain.DataSource";
    private static Long CLAZZ_ID = 1L;

    private static PermisionFilterDataSource instance = null;

    public static PermisionFilterDataSource getInstance() {
        if (instance == null) {
            instance = new PermisionFilterDataSource();
        }
        return instance;
    }

    private PermisionFilterDataSource() {
    }

    //TODO set permision
    public List<DataSourceVO<?>> filter(List<DataSourceVO<?>> tofilter, User user) {
        List<DataSourceVO<?>> result = null;
        /*Set<Long> ds =
                PermissionEvaluatorAclImp.getInstance().filter(CLAZZ, CLAZZ_ID, user.getUsername(), user.getId(),user.getId())
                        .stream()
                        .map(EntryDto::getId)
                        .collect(Collectors.toSet());*/


        /*result = tofilter.stream()
                        .filter(e ->  ds.contains(e.getId()))
                        .collect(Collectors.toList());*/

        return result;
    }

}
