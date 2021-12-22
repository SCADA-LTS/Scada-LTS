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
import com.serotonin.mango.web.dwr.EmportDwr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Response;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.json.JsonDataPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arkadiusz Parafiniuk
 * E-mail: arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataPointAPI {

    private static final Log LOG = LogFactory.getLog(DataPointAPI.class);

    DataPointService dataPointService = new DataPointService();

    @GetMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointVO> getDataPoint(@RequestParam(required = false) Integer id,
                                                    @RequestParam(required = false) String xid,
                                                    HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(id != null) {
                    return new ResponseEntity<>(dataPointService.getDataPoint(id), HttpStatus.OK);
                } else if (xid != null){
                    return new ResponseEntity<>(dataPointService.getDataPoint(xid), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/api/datapoint/datasource")
    public ResponseEntity<List<DataPointJson>> getDataPointsFromDataSource(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(id != null) {
                    return new ResponseEntity<>(
                            dataPointService.getDataPoints(id, null)
                                    .stream().map(DataPointJson::new)
                                    .collect(Collectors.toList()),
                            HttpStatus.OK);
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

    @GetMapping(value = "/api/datapoint/validate")
    public ResponseEntity<Map<String, Object>> isDataPointXidUnique(
            @RequestParam String xid,
            @RequestParam Integer id,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("unique", dataPointService.isXidUnique(xid, id));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/datapoint/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                return new ResponseEntity<>(dataPointService.generateUniqueXid(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> createDataPoint(
            @RequestBody DataPointJson datapoint,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                return new ResponseEntity<>(
                        new DataPointJson(dataPointService.createDataPoint(datapoint.createDataPointVO())),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointVO> updateDataPoint(
            @RequestBody DataPointJson datapoint,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                dataPointService.updateDataPointConfiguration(datapoint.createDataPointVO());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointVO> deleteDataPoint(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(id != null) {
                    dataPointService.deleteDataPoint(id);
                } else if(xid != null) {
                    dataPointService.deleteDataPoint(xid);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/datapoints/datasource")
    public ResponseEntity<List<JsonDataPoint>> getDataPointsFromDataSourceId(
            @RequestParam() Integer id,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(id != null) {
                    List<JsonDataPoint> result = new ArrayList<>();
                    List<DataPointVO> list = dataPointService.getDataPoints(id, null);
                    list.forEach(point -> result.add(new JsonDataPoint(
                            point.getId(),
                            point.getName(),
                            point.getXid(),
                            point.isEnabled(),
                            point.getDescription(),
                            point.getDataSourceName(),
                            point.getPointLocator().getDataTypeId(),
                            point.getPointLocator().isSettable()
                    )));
                    return new ResponseEntity<>(result, HttpStatus.OK);
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

    @GetMapping(value = "/api/datapoints")
    public ResponseEntity<List<JsonDataPoint>> getDataPoints(
            @RequestParam(value="keywordSearch", required = false) String searchText,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<DataPointVO> lstDP;

                if (searchText != null) {
                    String[] keywords = searchText.split("\\s+");
                    List<JsonDataPoint> result = new ArrayList<>();
                    for (DataPointVO dp: dataPointService.searchDataPoints(keywords)){
                        JsonDataPoint jdp = new JsonDataPoint(
                                dp.getId(),
                                dp.getName(),
                                dp.getXid(),
                                dp.isEnabled(),
                                dp.getDescription(),
                                dp.getDataSourceName(),
                                dp.getPointLocator().getDataTypeId(),
                                dp.getPointLocator().isSettable()
                        );

                        result.add(jdp);
                    }
                    return new ResponseEntity<List<JsonDataPoint>>(result, HttpStatus.OK);
                }

                Comparator<DataPointVO> comparator = new Comparator<DataPointVO>() {
                    @Override
                    public int compare(DataPointVO o1, DataPointVO o2) {
                        return 0;
                    }
                };

                lstDP = dataPointService.getDataPoints(comparator, false);

                List<JsonDataPoint> result = new ArrayList<>();
                for (DataPointVO dp:lstDP){
                    JsonDataPoint jdp = new JsonDataPoint(
                            dp.getId(),
                            dp.getName(),
                            dp.getXid(),
                            dp.isEnabled(),
                            dp.getDescription(),
                            dp.getDataSourceName(),
                            dp.getPointLocator().getDataTypeId(),
                            dp.getPointLocator().isSettable()
                    );
                    result.add(jdp);
                }
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/api/datapoint/getConfigurationByXid/{xid}", method = RequestMethod.GET)
    public ResponseEntity<String> getConfigurationByXid(
            @PathVariable String xid,
            HttpServletRequest request) {
        LOG.info("/api/datapoint/getAllByXid/{xid}");

        if( !xid.isEmpty() || xid != null ) {
            try {
                User user = Common.getUser(request);
                if (user != null) {

                    String json = null;
                    if (user.isAdmin()) {
                        json = EmportDwr.exportJSON(xid);
                    } else {
                        return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                    }

                    return new ResponseEntity<String>(json, HttpStatus.OK);
                }

                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

            } catch (Exception e) {
                LOG.error(e);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
        }
        else
            {
                return new ResponseEntity<String>("Given xid is empty.",HttpStatus.OK);
            }
    }

    @RequestMapping(value = "/api/datapoint/getAll", method = RequestMethod.GET)
    public ResponseEntity<String> getAll(HttpServletRequest request) {
        LOG.info("/api/datapoint/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {


                List<DataPointVO> lstDP;

                Comparator<DataPointVO> comparator = new Comparator<DataPointVO>() {
                    @Override
                    public int compare(DataPointVO o1, DataPointVO o2) {
                        return 0;
                    }
                };

                if (user.isAdmin()) {
                    lstDP = dataPointService.getDataPoints(comparator, false);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<DatapointJSON> lst = new ArrayList<>();
                for (DataPointVO dp:lstDP){
                    DatapointJSON dpJ = new DatapointJSON(dp.getId(), dp.getName(), dp.getXid(), dp.getDescription());
                    lst.add(dpJ);
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

    @GetMapping(value = "/api/datapoint/{datasourceId}/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DatapointJSON>> getAllPlcDataPoints(@PathVariable("datasourceId") int datasourceId, HttpServletRequest request) {
        LOG.info("/api/datapoint/datasourceId/getAllPlc");

        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<DatapointJSON> resultList = new ArrayList<>();
                List<DataPointVO> datapointList = dataPointService.getPlcDataPoints(datasourceId);
                for(DataPointVO datapoint: datapointList) {
                    DatapointJSON dp = new DatapointJSON(datapoint.getId(), datapoint.getName(), datapoint.getXid(), datapoint.getDescription());
                    resultList.add(dp);
                }
                return new ResponseEntity<>(resultList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public class DatapointJSON implements Serializable {
        private long id;
        private String name;
        private String xid;
        private String description;

        DatapointJSON(long id, String name, String xid, String description) {
            this.setId(id);
            this.setName(name);
            this.setXid(xid);
            this.setDescription(description);
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getName() {
            return name;
        }
        public String getDescription() { return description; }

        public void setName(String name) {
            this.name = name;
        }
        public String getXid() {
            return xid;
        }
        public void setXid(String xid) {
            this.xid = xid;
        }
        public void setDescription(String description) { this.description = description; }
    }
}

