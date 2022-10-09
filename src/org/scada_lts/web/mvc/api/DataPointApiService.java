package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.EmportDwr;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.scada_lts.dao.model.DataPointIdentifier;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourcePointJsonFactory;
import org.scada_lts.web.mvc.api.exceptions.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByAccess;
import static org.scada_lts.utils.DataSourcePointApiUtils.toMapMessages;
import static org.scada_lts.utils.DataSourcePointApiUtils.toObject;
import static org.scada_lts.utils.ValidationUtils.*;

public class DataPointApiService implements ObjectApiService<DataPointJson, DataPointIdentifier> {

    private final DataPointService dataPointService;

    public DataPointApiService(DataPointService dataPointService) {
        this.dataPointService = dataPointService;
    }

    @Override
    public Map<String, Object> isUniqueXid(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id and xid cannot be null.", id, xid);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUnique = dataPointService.isXidUnique(xid, id);
            response.put("unique", isUnique);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    @Override
    public String generateUniqueXid(HttpServletRequest request) {
        checkIfNonAdminThenUnauthorized(request);
        String response;
        try {
            response = dataPointService.generateUniqueXid();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    @Override
    public DataPointJson create(HttpServletRequest request, DataPointJson datapoint) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Point cannot be null.", datapoint);
        DataPointVO dataPointVO = toDataPointVO(request, datapoint);
        DataPointJson response;
        try {
            DataPointVO result = dataPointService.createDataPoint(dataPointVO);
            response = DataSourcePointJsonFactory.getDataPointJson(result);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    @Override
    public DataPointJson update(HttpServletRequest request, DataPointJson datapoint) {
        checkArgsIfEmptyThenBadRequest(request, "Data Point cannot be null.", datapoint);

        User user = Common.getUser(request);
        DataPointVO vo = toDataPointVO(request, datapoint);
        if(!GetDataPointsWithAccess.hasDataPointSetPermission(user, vo)) {
            throw new UnauthorizedException(request.getRequestURI());
        }
        try {
            dataPointService.updateDataPointConfiguration(vo);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return datapoint;
    }

    @Override
    public void delete(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", id, xid);
        try {
            if(id != null) {
                dataPointService.deleteDataPoint(id);
            } else {
                dataPointService.deleteDataPoint(xid);
            }
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Data Point not found, id: " + id + ", xid: " + xid, request.getRequestURI());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @Override
    public List<DataPointIdentifier> getIdentifiers(HttpServletRequest request) {
        User user = Common.getUser(request);
        List<DataPointIdentifier> response;
        try {
            response = dataPointService.getDataPointsWithAccess(user)
                    .stream()
                    .map(DataPointVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public String getConfigurationByXid(HttpServletRequest request, String xid) {
        checkIfNonAdminThenUnauthorized(request);
        if(StringUtils.isEmpty(xid)) {
            throw new ScadaApiException(ScadaErrorMessage.builder(HttpStatus.OK)
                    .detail("Given xid is empty.")
                    .instance(request.getRequestURI())
                    .build());
        }

        String response;
        try {
            response = EmportDwr.exportJSON(xid);
        } catch (Exception ex) {
            throw new BadRequestException(ex, request.getRequestURI());
        }
        return response;
    }

    public DataPointVO getDataPoint(HttpServletRequest request, String xid, Integer id) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", id, xid);
        User user = Common.getUser(request);

        DataPointVO response;
        if(id != null) {
            response = toObject(id, user, request, dataPointService::getDataPoint,
                    GetDataPointsWithAccess::hasDataPointReadPermission, a -> a);
        } else {
            response = toObject(xid, user, request, dataPointService::getDataPoint,
                    GetDataPointsWithAccess::hasDataPointReadPermission, a -> a);
        }
        return response;
    }

    public List<DataPointJson> getDataPointsByDataSourceId(HttpServletRequest request, Integer dataSourceId) {
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", dataSourceId);
        User user = Common.getUser(request);
        List<DataPointJson> response;
        try {
            response = filteringByAccess(user, dataPointService.getDataPoints(dataSourceId, null))
                    .stream()
                    .map(DataSourcePointJsonFactory::getDataPointJson)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public List<DataPointIdentifier> getDataPointIdentifiersByDataSourceId(HttpServletRequest request, Integer id) {
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);
        User user = Common.getUser(request);
        List<DataPointIdentifier> response;
        try {
            response = filteringByAccess(user, dataPointService.getDataPoints(id, null))
                    .stream()
                    .map(DataPointVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public List<DataPointIdentifier> getDataPointIdentifiersByTypes(HttpServletRequest request, Integer[] types) {
        User user = Common.getUser(request);
        List<DataPointIdentifier> response;
        try {
            response = dataPointService.getDataPointsWithAccess(user)
                    .stream()
                    .filter(a -> filteringByTypes(types, a))
                    .map(DataPointVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new BadRequestException(ex, request.getRequestURI());
        }
        return response;
    }

    public List<DataPointIdentifier> searchDataPointIdentifiers(HttpServletRequest request, String searchText) {
        User user = Common.getUser(request);
        List<DataPointIdentifier> response;
        try {
            response = filteringByAccess(user, dataPointService.searchDataPointsBy(searchText))
                    .stream()
                    .map(DataPointVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public List<DataPointIdentifier> getDataPointIdentifiersPlcByDataSourceId(HttpServletRequest request, Integer datasourceId) {
        checkArgsIfEmptyThenBadRequest(request, "datasourceId cannot be null.", datasourceId);
        User user = Common.getUser(request);
        List<DataPointIdentifier> response;
        try {
            response = filteringByAccess(user, dataPointService.getPlcDataPoints(datasourceId))
                    .stream()
                    .map(DataPointVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new BadRequestException(ex, request.getRequestURI());
        }
        return response;
    }

    private static boolean filteringByTypes(Integer[] types, DataPointVO point) {
        if(types == null)
            return true;
        if(Objects.isNull(point.getPointLocator()))
            return false;
        return Stream.of(types).anyMatch(type -> point.getPointLocator().getDataTypeId() == type);
    }

    private static DataPointVO toDataPointVO(HttpServletRequest request, DataPointJson datapoint) {
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
