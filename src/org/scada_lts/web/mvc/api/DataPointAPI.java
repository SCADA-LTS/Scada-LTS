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

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.DataPointIdentifier;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Arkadiusz Parafiniuk
 * E-mail: arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataPointAPI {

    private static final Log LOG = LogFactory.getLog(DataPointAPI.class);

    private final DataPointApiService dataPointApiService;

    public DataPointAPI(DataPointApiService dataPointApiService) {
        this.dataPointApiService = dataPointApiService;
    }

    @GetMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointVO> getDataPoint(@RequestParam(required = false) String xid,
                                                    @RequestParam(required = false) Integer id,
                                                    HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        DataPointVO response = dataPointApiService.getDataPointFromDatabase(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/datasource")
    public ResponseEntity<List<DataPointJson>> getDataPointsByDataSource(@RequestParam(required = false) String xid,
                                                                         @RequestParam(required = false) Integer id,
                                                                         HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        List<DataPointJson> response = dataPointApiService.getDataPointsByDataSource(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/validate")
    public ResponseEntity<Map<String, Object>> isDataPointXidUnique(@RequestParam(required = false) String xid,
                                                                    @RequestParam(required = false) Integer id,
                                                                    HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        Map<String, Object> response = new HashMap<>();
        boolean isUnique = dataPointApiService.isUniqueXid(request, xid, id);
        response.put("unique", isUnique);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        String response = dataPointApiService.generateUniqueXid(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> createDataPoint(@RequestBody(required = false) DataPointJson datapoint,
                                                         HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        DataPointJson response = dataPointApiService.create(request, datapoint);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> updateDataPoint(@RequestBody(required = false) DataPointJson datapoint,
                                                         HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        DataPointJson response = dataPointApiService.update(request, datapoint);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/datapoint")
    public ResponseEntity<DataPointJson> deleteDataPoint(@RequestParam(required = false) String xid,
                                                         @RequestParam(required = false) Integer id,
                                                         HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        DataPointJson response = dataPointApiService.delete(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoints/datasource")
    public ResponseEntity<List<DataPointIdentifier>> getDataPointIdentifiersByDataSourceId(@RequestParam(required = false) String xid,
                                                                                           @RequestParam(required = false) Integer id,
                                                                                           HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        List<DataPointIdentifier> response = dataPointApiService.getDataPointIdentifiersByDataSource(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoints")
    public ResponseEntity<List<DataPointIdentifier>> searchDataPointIdentifiers(@RequestParam(value="keywordSearch", required = false) String searchText,
                                                                                HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        List<DataPointIdentifier> response = dataPointApiService.searchDataPointIdentifiers(request, searchText);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/getConfigurationByXid/{xid}")
    public ResponseEntity<String> getConfigurationByXid(@PathVariable(required = false) String xid,
                                                        HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        String response = dataPointApiService.getConfigurationByXid(request, xid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/getAll")
    public ResponseEntity<List<DataPointIdentifier>> getDataPointIdentifiersByTypes(@RequestParam(value = "types", required = false) Integer[] types,
                                                                                    HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        List<DataPointIdentifier> response = dataPointApiService.getDataPointIdentifiersByTypes(request, types);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datapoint/{datasourceId}/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DataPointIdentifier>> getDataPointIdentifiersPlcByDataSourceId(@PathVariable(value = "datasourceId", required = false) Integer datasourceId,
                                                                                              HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        List<DataPointIdentifier> response = dataPointApiService.getDataPointIdentifiersPlcByDataSourceId(request, datasourceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/api/datapoint/enabled")
    public ResponseEntity<DataPointJson> enableDataPoint(@RequestParam(required = false) String xid,
                                                         @RequestParam(required = false) Integer id,
                                                         HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        DataPointJson response = dataPointApiService.enableDataPoint(request, xid, id, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/api/datapoint/disabled")
    public ResponseEntity<DataPointJson> disableDataPoint(@RequestParam(required = false) String xid,
                                                          @RequestParam(required = false) Integer id,
                                                          HttpServletRequest request) {
        LOG.debug(request.getRequestURI());

        DataPointJson response = dataPointApiService.enableDataPoint(request, xid, id, false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

