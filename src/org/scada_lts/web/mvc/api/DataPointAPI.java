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
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourcePointJsonFactory;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
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
import static org.scada_lts.utils.DataSourcePointApiUtils.toMapMessages;
import static org.scada_lts.utils.ValidationUtils.*;

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

        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", id, xid);
        User user = Common.getUser(request);
        try {
            if(id != null) {
                return getDataPoint(id, user, dataPointService::getDataPoint);
            } else {
                return getDataPoint(xid, user, dataPointService::getDataPoint);
            }
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @GetMapping(value = "/api/datapoint/datasource")
    public ResponseEntity<List<DataPointJson>> getDataPointsFromDataSource(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {

        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);
        User user = Common.getUser(request);
        List<DataPointJson> response;
        try {
            response = filteringByAccess(user, dataPointService.getDataPoints(id, null))
                    .stream()
                    .map(DataSourcePointJsonFactory::getDataPointJson)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/validate")
    public ResponseEntity<Map<String, Object>> isDataPointXidUnique(@RequestParam String xid,
                                                                    @RequestParam Integer id,
                                                                    HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id and xid cannot be null.", id, xid);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUnique = dataPointService.isXidUnique(xid, id);
            response.put("unique", isUnique);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        String response;
        try {
            response = dataPointService.generateUniqueXid();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> createDataPoint(@RequestBody DataPointJson datapoint,
                                                         HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Point cannot be null.", datapoint);
        DataPointVO dataPointVO = toDataPointVO(datapoint, request);
        DataPointJson response;
        try {
            DataPointVO result = dataPointService.createDataPoint(dataPointVO);
            response = DataSourcePointJsonFactory.getDataPointJson(result);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> updateDataPoint(@RequestBody DataPointJson datapoint,
                                                         HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Point cannot be null.", datapoint);
        DataPointVO dataPointVO = toDataPointVO(datapoint, request);
        try {
            dataPointService.updateDataPointConfiguration(dataPointVO);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(datapoint, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> deleteDataPoint(@RequestParam(required = false) Integer id,
                                                       @RequestParam(required = false) String xid,
                                                       HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", id, xid);
        try {
            if(id != null) {
                dataPointService.deleteDataPoint(id);
            } else if(xid != null) {
                dataPointService.deleteDataPoint(xid);
            }
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoints/datasource")
    public ResponseEntity<List<JsonDataPoint>> getDataPointsFromDataSourceId(@RequestParam() Integer id, HttpServletRequest request) {
        checkArgsIfEmptyThenBadRequest(request, "Data Source Id cannot be null.", id);
        User user = Common.getUser(request);
        List<JsonDataPoint> response;
        try {
            response = filteringByAccess(user, dataPointService.getDataPoints(id, null))
                    .stream()
                    .map(JsonDataPoint::newInstance)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoints")
    public ResponseEntity<List<JsonDataPoint>> getDataPoints(@RequestParam(value="keywordSearch", required = false) String searchText,
                                                             HttpServletRequest request
    ) {
        User user = Common.getUser(request);
        List<JsonDataPoint> response;
        try {
            response = filteringByAccess(user, dataPointService.searchDataPointsBy(searchText))
                    .stream()
                    .map(JsonDataPoint::newInstance)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoints/identifiers")
    public ResponseEntity<List<ScadaObjectIdentifier>> getDataPointUserIdentifiers(@RequestParam() Integer dataSourceId,
                                                                                   HttpServletRequest request) {
        User user = Common.getUser(request);
        List<ScadaObjectIdentifier> response;
        try {
            response = filteringByAccess(user, dataPointService.getDataPoints(dataSourceId, null))
                    .stream()
                    .map(DataPointVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/datapoint/getConfigurationByXid/{xid}", method = RequestMethod.GET)
    public ResponseEntity<String> getConfigurationByXid(@PathVariable String xid, HttpServletRequest request) {
        LOG.info("/api/datapoint/getConfigurationByXid/{xid}");

        checkIfNonAdminThenUnauthorized(request);
        if(StringUtils.isEmpty(xid)) {
            return new ResponseEntity<>("Given xid is empty.",HttpStatus.OK);
        }
        String response;
        try {
            response = EmportDwr.exportJSON(xid);
        } catch (Exception ex) {
            throw new BadRequestException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/datapoint/getAll", method = RequestMethod.GET)
    public ResponseEntity<List<DatapointJSON>> getAll(@RequestParam(value = "types", required = false) Integer[] types,
                                                      HttpServletRequest request) {
        LOG.info("/api/datapoint/getAll");

        User user = Common.getUser(request);
        List<DatapointJSON> response;
        try {
            response = dataPointService.getDataPointsWithAccess(user)
                    .stream()
                    .filter(a -> filteringByTypes(types, a))
                    .map(DatapointJSON::new)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new BadRequestException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/{datasourceId}/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DatapointJSON>> getAllPlcDataPoints(@PathVariable("datasourceId") Integer datasourceId,
                                                                   HttpServletRequest request) {
        LOG.info("/api/datapoint/datasourceId/getAllPlc");
        checkArgsIfEmptyThenBadRequest(request, "datasourceId cannot be null.", datasourceId);
        User user = Common.getUser(request);
        List<DatapointJSON> response;
        try {
            response = filteringByAccess(user, dataPointService.getPlcDataPoints(datasourceId))
                .stream()
                .map(DatapointJSON::new)
                .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new BadRequestException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    private static DataPointVO toDataPointVO(DataPointJson datapoint, HttpServletRequest request) {
        DataPointVO dataPointVO;
        try {
            dataPointVO = datapoint.createDataPointVO();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        DwrResponseI18n responseI18n = new DwrResponseI18n();
        dataPointVO.validate(responseI18n);
        if(responseI18n.getHasMessages()) {
            throw new BadRequestException(toMapMessages(responseI18n),
                    request.getRequestURI());
        }
        return dataPointVO;
    }
}

