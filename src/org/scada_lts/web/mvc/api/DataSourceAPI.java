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
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.DataSourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataSourceAPI {

    private static final Log LOG = LogFactory.getLog(DataSourceAPI.class);


    DataSourceService dataSourceService = new DataSourceService();

    @GetMapping(value = "/api/datasource/getAll")
    public ResponseEntity<List<ScadaObjectIdentifier>> getAll(HttpServletRequest request) {
        LOG.info("/api/datasource/getAll");

        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(dataSourceService.getAllDataSources(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/datasource/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DataSourceSimpleJSON>> getAllPlcDataSources(HttpServletRequest request) {
        LOG.info("/api/datasource/getAllPlc");
        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<DataSourceVO<?>> list;
                list = dataSourceService.getDataSourcesPlc();
                List<DataSourceSimpleJSON> result = new ArrayList<>();
                for(DataSourceVO<?> ds: list) {
                    DataSourceSimpleJSON d = new DataSourceSimpleJSON(ds.getId(), ds.getXid(), ds.getName(), ds.isEnabled());
                    result.add(d);
                }
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceSimpleJSON> getDataSource(
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if (xid != null){
                    DataSourceVO ds = dataSourceService.getDataSource(xid);
                    DataSourceSimpleJSON json = new DataSourceSimpleJSON(ds.getId(), ds.getXid(), ds.getName(), ds.isEnabled());
                    return new ResponseEntity<>(json,HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private class DataSourceSimpleJSON {
        private long id;
        private String xid;
        private String name;
        private boolean enabled;

        DataSourceSimpleJSON(long id, String xid, String name, boolean enabled) {
            this.setId(id);
            this.setXid(xid);
            this.setName(name);
            this.setEnabled(enabled);
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getXid() {
            return xid;
        }

        public void setXid(String xid) {
            this.xid = xid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getEnabled() {
            return enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

}
