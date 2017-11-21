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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.permissions.PermissionEvaluatorAcl;

import java.io.IOException;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
@RunWith(JUnit4.class)
public class PermissionEvaluatorAclTest {


    @Before
    public void init() {
        PermissionEvaluatorAcl.getInstance();
    }

    @After
    public void finalized() {
        //PermissionEvaluatorAcl.getInstance().finalized();
    }

    @Test
    public void filter() throws IOException {
        PermissionEvaluatorAcl.getInstance().filter("DataSource", "admin");
    }

}
