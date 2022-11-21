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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.DataSourceIdentifier;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataSourceAPI {

    private static final Log LOG = LogFactory.getLog(DataSourceAPI.class);
    private final DataSourceApiService dataSourceApiService;

    public DataSourceAPI(DataSourceApiService dataSourceApiService) {
        this.dataSourceApiService = dataSourceApiService;
    }

    @GetMapping(value = "/api/datasources")
    public ResponseEntity<List<DataSourceJson>> getAllDataSources(HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        List<DataSourceJson> response = dataSourceApiService.readAll(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> getDataSource(@RequestParam(required = false) Integer id,
                                                        @RequestParam(required = false) String xid,
                                                        HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        DataSourceJson response = dataSourceApiService.read(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/toggle")
    public ResponseEntity<Map<String, Object>> toggleDataSource(@RequestParam(required = false) Integer id,
                                                                @RequestParam(required = false) String xid,
                                                                HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        Map<String, Object> response = dataSourceApiService.toggleDataSource(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/validate")
    public ResponseEntity<Map<String, Object>> isUniqueXid(@RequestParam(required = false) Integer id,
                                                           @RequestParam(required = false) String xid,
                                                           HttpServletRequest request) {
        LOG.debug( request.getRequestURI());
        Map<String, Object> response = new HashMap<>();
        boolean isUnique = dataSourceApiService.isUniqueXid(request, xid, id);
        response.put("unique", isUnique);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/datapoints/enable")
    public ResponseEntity<List<DataPointJson>> enableAllPointsInDataSource(@RequestParam(required = false) Integer id,
                                                                           @RequestParam(required = false) String xid,
                                                                           HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        List<DataPointJson> response = dataSourceApiService.enableAllPointsInDataSource(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        String response = dataSourceApiService.generateUniqueXid(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> createDataSource(@RequestBody(required = false) DataSourceJson dataSource,
                                                           HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        DataSourceJson response = dataSourceApiService.create(request, dataSource);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> updateDataSource(@RequestBody(required = false) DataSourceJson dataSource,
                                                           HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        DataSourceJson response = dataSourceApiService.update(request, dataSource);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> deleteDataSource(@RequestParam(required = false) Integer id,
                                                           @RequestParam(required = false) String xid,
                                                           HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        DataSourceJson response = dataSourceApiService.delete(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/getAll")
    public ResponseEntity<List<DataSourceIdentifier>> getDataSourceIdentifiers(HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        List<DataSourceIdentifier> response = dataSourceApiService.getIdentifiers(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DataSourceIdentifier>> getDataSourcesPlcIdentifers(HttpServletRequest request) {
        LOG.debug( request.getRequestURI());

        List<DataSourceIdentifier> response = dataSourceApiService.getDataSourcesPlcIdentifers(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
