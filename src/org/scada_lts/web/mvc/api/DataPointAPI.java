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

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.EmportDwr;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourcePointJsonFactory;
import org.scada_lts.web.mvc.api.json.JsonDataPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Stream;

import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByAccess;
import static org.scada_lts.permissions.service.GetDataPointsWithAccess.hasDataPointReadPermission;

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
                    return getDataPoint(id, user, dataPointService::getDataPoint);
                } else if (xid != null){
                    return getDataPoint(xid, user, dataPointService::getDataPoint);
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
                    List<DataPointJson> result = filteringByAccess(user, dataPointService.getDataPoints(id, null))
                            .stream()
                            .map(DataSourcePointJsonFactory::getDataPointJson)
                            .collect(Collectors.toList());
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

    @GetMapping(value = "/api/datapoint/validate")
    public ResponseEntity<Map<String, Object>> isDataPointXidUnique(
            @RequestParam String xid,
            @RequestParam Integer id,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
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
            if(user != null && user.isAdmin()) {
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
            if(user != null && user.isAdmin()) {
                DataPointVO dataPointVO = datapoint.createDataPointVO();
                DwrResponseI18n responseI18n = new DwrResponseI18n();
                dataPointVO.validate(responseI18n);
                if(responseI18n.getHasMessages()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                DataPointVO result = dataPointService.createDataPoint(dataPointVO);
                return new ResponseEntity<>(
                        DataSourcePointJsonFactory.getDataPointJson(result),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> updateDataPoint(
            @RequestBody DataPointJson datapoint,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                DataPointVO dataPointVO = datapoint.createDataPointVO();
                DwrResponseI18n responseI18n = new DwrResponseI18n();
                dataPointVO.validate(responseI18n);
                if(responseI18n.getHasMessages()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                dataPointService.updateDataPointConfiguration(dataPointVO);
                return new ResponseEntity<>(datapoint, HttpStatus.OK);
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
            if(user != null && user.isAdmin()) {
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
                    List<JsonDataPoint> result = filteringByAccess(user, dataPointService.getDataPoints(id, null))
                            .stream()
                            .map(JsonDataPoint::newInstance)
                            .collect(Collectors.toList());
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
                List<JsonDataPoint> result = filteringByAccess(user, dataPointService.searchDataPointsBy(searchText))
                        .stream()
                        .map(JsonDataPoint::newInstance)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/datapoints/identifiers")
    public ResponseEntity<List<ScadaObjectIdentifier>> getDataPointUserIdentifiers(
            @RequestParam() Integer dataSourceId,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<ScadaObjectIdentifier> result = filteringByAccess(user, dataPointService.getDataPoints(dataSourceId, null))
                        .stream()
                        .map(DataPointVO::toIdentifier)
                        .collect(Collectors.toList());
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

        if(xid != null && !xid.isEmpty()) {
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
    public ResponseEntity<List<DatapointJSON>> getAll(@RequestParam(value = "types", required = false) Integer[] types, HttpServletRequest request) {
        LOG.info("/api/datapoint/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                List<DatapointJSON> result = dataPointService.getDataPointsWithAccess(user)
                        .stream()
                        .filter(a -> filteringByTypes(types, a))
                        .map(DatapointJSON::new)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/api/datapoint/{datasourceId}/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DatapointJSON>> getAllPlcDataPoints(@PathVariable("datasourceId") int datasourceId, HttpServletRequest request) {
        LOG.info("/api/datapoint/datasourceId/getAllPlc");

        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<DatapointJSON> result = filteringByAccess(user, dataPointService.getPlcDataPoints(datasourceId))
                        .stream()
                        .map(DatapointJSON::new)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public static class DatapointJSON implements Serializable {
        private long id;
        private String name;
        private String xid;
        private String description;
        private int type;

        public DatapointJSON(long id, String name, String xid, String description, int type) {
            this.id = id;
            this.name = name;
            this.xid = xid;
            this.description = description;
            this.type = type;
        }

        DatapointJSON(DataPointVO dp) {
            this.setId(dp.getId());
            this.setName(dp.getName());
            this.setXid(dp.getXid());
            this.setDescription(dp.getDescription());
            if(dp.getPointLocator() != null)
            this.setType(dp.getPointLocator().getDataTypeId());
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    private static <T> ResponseEntity<DataPointVO> getDataPoint(T id, User user, Function<T, DataPointVO> get) {
        DataPointVO dataPoint = get.apply(id);
        if(dataPoint == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(!hasDataPointReadPermission(user, dataPoint))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(dataPoint, HttpStatus.OK);
    }

    private static boolean filteringByTypes(Integer[] types, DataPointVO point) {
        if(types == null)
            return true;
        if(Objects.isNull(point.getPointLocator()))
            return false;
        return Stream.of(types).anyMatch(type -> point.getPointLocator().getDataTypeId() == type);
    }
}

