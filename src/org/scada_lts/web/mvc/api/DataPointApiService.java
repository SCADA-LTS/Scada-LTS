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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByAccess;
import static org.scada_lts.utils.ApiUtils.toMapMessages;
import static org.scada_lts.utils.DataSourcePointApiUtils.toObject;
import static org.scada_lts.utils.ValidationUtils.*;

@Service
public class DataPointApiService implements CrudService<DataPointJson>, GeneratorXid, GetIdentifiers<DataPointIdentifier> {

    private final DataPointService dataPointService;
    private final DataSourceApiService dataSourceApiService;

    public DataPointApiService(DataPointService dataPointService, DataSourceApiService dataSourceApiService) {
        this.dataPointService = dataPointService;
        this.dataSourceApiService = dataSourceApiService;
    }

    @Override
    public boolean isUniqueXid(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id and xid cannot be null.", id, xid);
        try {
            return dataPointService.isXidUnique(xid, id);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
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
        DataPointVO fromRequest = toDataPointVO(request, datapoint);
        dataSourceApiService.read(request, null, fromRequest.getDataSourceId());
        DataPointJson response;
        try {
            DataPointVO result = dataPointService.createDataPoint(fromRequest);
            response = DataSourcePointJsonFactory.getDataPointJson(result);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    @Override
    public DataPointJson update(HttpServletRequest request, DataPointJson datapoint) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Point cannot be null.", datapoint);
        getDataPointFromDatabase(request, datapoint.getXid(), datapoint.getId());
        DataPointVO fromRequest = toDataPointVO(request, datapoint);
        dataSourceApiService.read(request, fromRequest.getDataSourceXid(), fromRequest.getDataSourceId());
        try {
            dataPointService.updateDataPointConfiguration(fromRequest);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return datapoint;
    }

    @Override
    public DataPointJson delete(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", id, xid);
        DataPointVO toDelete = getDataPointFromDatabase(request, xid, id);
        try {
            dataPointService.deleteDataPoint(toDelete.getId());
            return DataSourcePointJsonFactory.getDataPointJson(toDelete);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @Override
    public DataPointJson read(HttpServletRequest request, String xid, Integer id) {
        DataPointVO dataPoint = getDataPointFromDatabase(request, xid, id);
        return new DataPointJson(dataPoint);
    }

    @Override
    public List<DataPointJson> readAll(HttpServletRequest request) {
        User user = Common.getUser(request);
        return dataPointService.getDataPointsWithAccess(user).stream()
                .map(DataPointJson::new)
                .collect(Collectors.toList());
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

    public DataPointVO getDataPointFromDatabase(HttpServletRequest request, String xid, Integer id) {
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

    public List<DataPointJson> getDataPointsByDataSource(HttpServletRequest request,
                                                         String dataSourceXid,
                                                         Integer dataSourceId) {
        return getDataPointsBy(request, dataSourceXid, dataSourceId).stream()
                .map(DataPointJson::new)
                .collect(Collectors.toList());
    }

    public List<DataPointIdentifier> getDataPointIdentifiersByDataSource(HttpServletRequest request,
                                                                         String dataSourceXid,
                                                                         Integer dataSourceId) {
        return getDataPointsBy(request, dataSourceXid, dataSourceId).stream()
                .map(DataPointVO::toIdentifier)
                .collect(Collectors.toList());
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

    private List<DataPointVO> getDataPointsBy(HttpServletRequest request, String dataSourceXid, Integer dataSourceId) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", dataSourceId, dataSourceXid);
        List<DataPointVO> dataPoints;
        try {
            if (dataSourceId != null) {
                dataPoints = dataPointService.getDataPoints(dataSourceId,
                        Comparator.comparing(DataPointVO::getName));
            } else {
                dataPoints = dataPointService.getDataPoints(dataSourceXid,
                        Comparator.comparing(DataPointVO::getName));
            }
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        User user = Common.getUser(request);
        List<DataPointVO> response;
        try {
            response = filteringByAccess(user, dataPoints);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }
}
