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
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ViewService;
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
public class ViewAPI {

    private static final Log LOG = LogFactory.getLog(WatchListAPI.class);
    private static final int ID_USER_AMIN = 1;

    ViewService viewService = new ViewService();

    @RequestMapping(value = "/api/view/getAll", method = RequestMethod.GET)
    public ResponseEntity<String> getAll(HttpServletRequest request) {
        LOG.info("/api/view/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {
                class ViewJSON implements Serializable {
                    private long id;
                    private String xid;

                    ViewJSON(long id,String xid) {
                        this.setId(id);
                        this.setXid(xid);
                    }

                    public long getId() { return id; }
                    public void setId(long id) { this.id = id; }
                    public String getXid() {
                        return xid;
                    }
                    public void setXid(String xid) {
                        this.xid = xid;
                    }
                }

                int userId = user.getId();
                List<View> lstV;
                if(userId == ID_USER_AMIN) {
                  lstV = viewService.getViews();
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<ViewJSON> lst = new ArrayList<>();
                for(View view:lstV){
                    ViewJSON vJ = new ViewJSON(view.getId(), view.getXid());
                    lst.add(vJ);
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
