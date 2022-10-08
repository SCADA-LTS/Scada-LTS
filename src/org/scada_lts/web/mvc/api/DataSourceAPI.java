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

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.utils.DataSourcePointApiUtils;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;
import org.scada_lts.web.mvc.api.datasources.DataSourcePointJsonFactory;
import org.scada_lts.web.mvc.api.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.utils.ValidationUtils.*;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataSourceAPI {

    private static final Log LOG = LogFactory.getLog(DataSourceAPI.class);
    private final DataSourceService dataSourceService = new DataSourceService();

    @GetMapping(value = "/api/datasources")
    public ResponseEntity<List<DataSourceJson>> getAllDataSources(HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);

        List<DataSourceJson> response;
        try {
            List<DataSourceVO<?>> dataSources = dataSourceService.getDataSources();
            response = dataSources.stream().map(DataSourcePointJsonFactory::getDataSourceJson)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> getDataSource(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {

        checkArgsIfTwoEmptyThenBadRequest(request, "Id or xid cannot be null.", id, xid);

        DataSourceVO<?> ds = null;
        try {
            if(id != null) {
                ds = dataSourceService.getDataSource(id);
            } else {
                ds = dataSourceService.getDataSource(xid);
            }
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }

        if(ds == null)
            throw new NotFoundException("id: " + id + ", xid: " + xid, request.getRequestURI());

        DataSourceJson response;
        try {
            response = DataSourcePointJsonFactory.getDataSourceJson(ds);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/toggle")
    public ResponseEntity<Map<String, Object>> toggleDataSource(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String xid,
            HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean state = dataSourceService.toggleDataSource(id);
            response.put("id", id);
            response.put("state", state);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/validate")
    public ResponseEntity<Map<String, Object>> isDataPointXidUnique(
            @RequestParam String xid,
            @RequestParam Integer id,
            HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id and xid cannot be null.", id, xid);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUnique = dataSourceService.isXidUnique(xid, id);
            response.put("unique", isUnique);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/datapoints/enable")
    public ResponseEntity<List<DataPointJson>> enableAllPointsInDS(
            @RequestParam Integer id,
            HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);

        List<DataPointJson> response;
        try {
            response = dataSourceService.enableAllDataPointsInDS(id)
                    .stream().map(DataPointJson::new)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(HttpServletRequest request) {

        String response;
        try {
            response = dataSourceService.generateUniqueXid();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> createDataSource(@RequestBody DataSourceJson dataSource,
                                                           HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Source cannot be null.", dataSource);

        DataSourceVO<?> vo = toDataSourceVO(dataSource, request);
        DataSourceJson response;
        try {
            DataSourceVO<?> created = dataSourceService.createDataSource(vo);
            response = DataSourcePointJsonFactory.getDataSourceJson(created);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceJson> updateDataSource(@RequestBody DataSourceJson dataSource,
                                                           HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Source cannot be null.", dataSource);

        DataSourceVO<?> vo = toDataSourceVO(dataSource, request);
        try {
            dataSourceService.updateAndInitializeDataSource(vo);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(dataSource, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/api/datasource")
    public ResponseEntity<DataSourceVO<?>> deleteDataSource(@RequestParam Integer id, HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);

        try {
            dataSourceService.deleteDataSource(id);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/getAll")
    public ResponseEntity<List<ScadaObjectIdentifier>> getAll(HttpServletRequest request) {

        LOG.info("/api/datasource/getAll");

        checkIfNonAdminThenUnauthorized(request);
        List<ScadaObjectIdentifier> response;
        try {
            response = dataSourceService.getAllDataSources();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/api/datasource/getAllPlc", produces = "application/json")
    public ResponseEntity<List<DataSourceSimpleJSON>> getAllPlcDataSources(HttpServletRequest request) {

        LOG.info("/api/datasource/getAllPlc");

        List<DataSourceVO<?>> list;
        try {
            list = dataSourceService.getDataSourcesPlc();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        List<DataSourceSimpleJSON> response = new ArrayList<>();
        for(DataSourceVO<?> ds: list) {
            DataSourceSimpleJSON d = new DataSourceSimpleJSON(ds.getId(), ds.getXid(), ds.getName(), ds.isEnabled());
            response.add(d);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    private static DataSourceVO<?> toDataSourceVO(DataSourceJson dataSource, HttpServletRequest request) {
        DataSourceVO<?> vo;
        try {
            vo = dataSource.createDataSourceVO();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }

        DwrResponseI18n responseI18n = new DwrResponseI18n();
        vo.validate(responseI18n);
        if(responseI18n.getHasMessages()) {
            throw new BadRequestException(DataSourcePointApiUtils.toMapMessages(responseI18n),
                    request.getRequestURI());
        }
        return vo;
    }
}
