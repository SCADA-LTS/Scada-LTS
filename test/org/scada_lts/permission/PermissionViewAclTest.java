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
package org.scada_lts.permission;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.permissions.ACLConfig;
import org.scada_lts.permissions.PermissionViewACL;
import org.scada_lts.permissions.model.EntryDto;

import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
@RunWith(JUnit4.class)
public class PermissionViewAclTest {

    @Test //requires a server running with permissions with well set permission
    public void hasPermissionToRead() throws IOException {
        ACLConfig aclConfig = mock(ACLConfig.class);
        when(aclConfig.getUrlACL()).thenReturn("http://localhost:8090/api/permission");
        new ACLConfig(aclConfig);
        Boolean test = PermissionViewACL.getInstance().hasPermissionToRead(1, 1);
        assertTrue(test==false);
    }

    @Test //requires a server running with permissions with well set permission
    public void filter() throws IOException {
        new TestLoger();
        ACLConfig aclConfig = mock(ACLConfig.class);
        when(aclConfig.getUrlACL()).thenReturn("http://localhost:8090/api/permission");
        new ACLConfig(aclConfig);
        Map<Integer, EntryDto> map = PermissionViewACL.getInstance().filter(1);
        //System.out.printf("size:"+lst.size());
        assertTrue(map.size()==0);
    }

}
