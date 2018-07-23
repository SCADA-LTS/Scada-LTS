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
package org.scada_lts.web.mvc.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class UserAPI {

    private static final Log LOG = LogFactory.getLog(UserAPI.class);
    private static final int ID_USER_ADMIN = 1;

    UserService userService = new UserService();

    @RequestMapping(value = "/api/user/getAll", method = RequestMethod.GET)
    public ResponseEntity<String> getAll(HttpServletRequest request) {
        LOG.info("/api/user/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {
                class UserJSON implements Serializable {
                    private long id;
                    private String name;
                    private String password;
                    private String email;
                    private Boolean admin;

                    UserJSON(long id, String name, String password, String email, Boolean admin) {
                        this.setId(id);
                        this.setName(name);
                        this.setPassword(password);
                        this.setEmail(email);
                        this.setAdmin(admin);
                    }

                    public long getId() { return id; }
                    public void setId(long id) { this.id = id; }

                    public String getName() {
                        return name;
                    }
                    public void setName(String xid) {
                        this.name = xid;
                    }

                    public String getPassword() {
                        return password;
                    }
                    public void setPassword(String password) {
                        this.password = password;
                    }

                    public String getEmail() {
                        return email;
                    }
                    public void setEmail(String email) {
                        this.email = email;
                    }

                    public Boolean isAdmin() { return admin; }
                    public void setAdmin(Boolean admin) { this.admin = admin; }
                }

                int userId = user.getId();
                List<User> lstUsers;
                if (userId == ID_USER_ADMIN) {
                    lstUsers = userService.getUsers();
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<UserJSON> lst = new ArrayList<UserJSON>();
                for (User u:lstUsers) {
                    UserJSON dsU = new UserJSON(u.getId(), u.getUsername(), u.getPassword(), u.getEmail(), u.isAdmin());
                    lst.add(dsU);
                }

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(lst);

                return new ResponseEntity<String>(json,HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
}
