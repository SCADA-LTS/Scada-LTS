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
import org.scada_lts.web.mvc.api.json.JsonUser;
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

    UserService userService = new UserService();

    @RequestMapping(value = "/api/user/getAll", method = RequestMethod.GET)
    public ResponseEntity<List<JsonUser>> getAll(HttpServletRequest request) {
        LOG.info("/api/user/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {
                List<User> lstUsers = userService.getUsers();
                List<JsonUser> response = new ArrayList<>();
                for(User u:lstUsers) {
                    response.add(new JsonUser(
                            u.getId(),
                            u.getUsername(),
                            u.getEmail(),
                            u.getPhone(),
                            u.isAdmin(),
                            u.isDisabled(),
                            u.getHomeUrl(),
                            u.getLastLogin()
                    ));
                }
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
