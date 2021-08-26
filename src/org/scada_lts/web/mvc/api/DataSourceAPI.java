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
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.web.mvc.api.datasources.DataSourceFactory;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;
import org.scada_lts.web.mvc.api.datasources.GenericJsonDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataSourceAPI {

    private static final Log LOG = LogFactory.getLog(DataSourceAPI.class);


    DataSourceService dataSourceService = new DataSourceService();

    @GetMapping(value = "/api/datasources")
    public ResponseEntity<List<GenericJsonDataSource>> getAllDataSources(HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {

                //TODO: Ensure DataSource access privileges.
                //@see DataSourceEditDwr.java
                List<GenericJsonDataSource> list = new ArrayList<>();
                List<DataSourceVO<?>> dataSources = dataSourceService.getDataSources();

                for(DataSourceVO<?> ds: dataSources) {
                    list.add(new GenericJsonDataSource(ds));
                }

                return new ResponseEntity<>(list, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/api/datasource")
    public ResponseEntity<GenericJsonDataSource> getDataSource(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                DataSourceVO<?> ds;
                if(id != null) {
                    ds = dataSourceService.getDataSource(id);
                } else if (xid != null) {
                    ds = dataSourceService.getDataSource(xid);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(DataSourceFactory.getDataSource(ds), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/datasource/toggle")
    public ResponseEntity<Map<String, Object>> toggleDataSource(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                Map<String, Object> response = new HashMap<>();
                if(id != null) {
                    response.put("id", id);
                    response.put("state", dataSourceService.toggleDataSource(id));
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/api/datasource")
    public ResponseEntity<String> createDataSource(
            @RequestBody DataSourceJson<?> dataSource,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {

                if(dataSource != null) {
                    DataSourceVO<?> vo = DataSourceFactory.parseDataSource(dataSource);
                    if(vo != null) {
                        dataSourceService.saveDataSource(vo);
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    } else {
                        LOG.error("DataSource JSON Type Not recoginized!");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/api/datasource")
    public ResponseEntity<String> updateDataSource(
            @RequestBody DataSourceJson<?> dataSource,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {

                if(dataSource != null) {
                    DataSourceVO<?> vo = DataSourceFactory.parseDataSource(dataSource);
                    if(vo != null) {
                        dataSourceService.saveDataSource(vo);
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    } else {
                        LOG.error("DataSource JSON Type Not recoginized!");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceVO<?>> deleteDataSource(
            @RequestParam Integer id,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(id != null) {
                    dataSourceService.deleteDataSource(id);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/datasource/getAll", method = RequestMethod.GET)
    public ResponseEntity<String> getAll(HttpServletRequest request) {
        LOG.info("/api/datasource/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {
                class DatasourceJSON implements Serializable {
                    private long id;
                    private String xid;

                    DatasourceJSON(long id,String xid) {
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

                List<DataSourceVO<?>> lstDS;
                if (user.isAdmin()) {
                    lstDS = dataSourceService.getDataSources();
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<DatasourceJSON> lst = new ArrayList<DatasourceJSON>();
                for (DataSourceVO<?> ds:lstDS) {

                    DatasourceJSON dsJ = new DatasourceJSON(ds.getId(), ds.getXid());
                    lst.add(dsJ);
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
                    DataSourceSimpleJSON d = new DataSourceSimpleJSON(ds.getId(), ds.getXid(), ds.getName());
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

    private class DataSourceSimpleJSON {
        private long id;
        private String xid;
        private String name;

        DataSourceSimpleJSON(long id, String xid, String name) {
            this.setId(id);
            this.setXid(xid);
            this.setName(name);
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
    }

}
