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
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataSourceAPI {

    private static final Log LOG = LogFactory.getLog(DataSourceAPI.class);
    private final DataSourceApiService dataSourceService;

    public DataSourceAPI() {
        this.dataSourceService = new DataSourceApiService(new DataSourceService());
    }

    public DataSourceAPI(DataSourceApiService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @GetMapping(value = "/api/datasources")
    public ResponseEntity<List<DataSourceJson>> getAllDataSources(HttpServletRequest request) {
        LOG.info(  "/api/datasources");

        List<DataSourceJson> response = dataSourceService.getDataSources(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> getDataSource(@RequestParam(required = false) Integer id,
                                                        @RequestParam(required = false) String xid,
                                                        HttpServletRequest request) {
        LOG.info(  "/api/datasource");

        DataSourceJson response = dataSourceService.getDataSource(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/toggle")
    public ResponseEntity<Map<String, Object>> toggleDataSource(@RequestParam(required = false) Integer id,
                                                                @RequestParam(required = false) String xid,
                                                                HttpServletRequest request) {
        LOG.info( "/api/datasource/toggle");

        Map<String, Object> response = dataSourceService.toggleDataSource(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/validate")
    public ResponseEntity<Map<String, Object>> isUniqueXid(@RequestParam(required = false) Integer id,
                                                           @RequestParam(required = false) String xid,
                                                           HttpServletRequest request) {
        LOG.info("/api/datasource/validate");

        Map<String, Object> response = dataSourceService.isUniqueXid(request, xid, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/datapoints/enable")
    public ResponseEntity<List<DataPointJson>> enableAllPointsInDataSource(@RequestParam(required = false) Integer id,
                                                                           HttpServletRequest request) {
        LOG.info("/api/datasource/datapoints/enable");

        List<DataPointJson> response = dataSourceService.enableAllPointsInDataSource(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(HttpServletRequest request) {
        LOG.info("/api/datasource/generateUniqueXid");

        String response = dataSourceService.generateUniqueXid(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> createDataSource(@RequestBody(required = false) DataSourceJson dataSource,
                                                           HttpServletRequest request) {
        LOG.info("/api/datasource");

        DataSourceJson response = dataSourceService.create(request, dataSource);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> updateDataSource(@RequestBody(required = false) DataSourceJson dataSource,
                                                           HttpServletRequest request) {
        LOG.info("/api/datasource");

        DataSourceJson response = dataSourceService.update(request, dataSource);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> deleteDataSource(@RequestParam(required = false) Integer id,
                                                           HttpServletRequest request) {
        LOG.info("/api/datasource");

        dataSourceService.delete(request, null, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/getAll")
    public ResponseEntity<List<DataSourceIdentifier>> getDataSourceIdentifiers(HttpServletRequest request) {
        LOG.info("/api/datasource/getAll");

        List<DataSourceIdentifier> response = dataSourceService.getIdentifiers(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DataSourceIdentifier>> getDataSourcesPlcIdentifers(HttpServletRequest request) {
        LOG.info("/api/datasource/getAllPlc");

        List<DataSourceIdentifier> response = dataSourceService.getDataSourcesPlcIdentifers(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
