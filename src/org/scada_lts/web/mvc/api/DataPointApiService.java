package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.EmportDwr;
import com.serotonin.mango.web.dwr.beans.DataPointBean;
import org.scada_lts.dao.model.DataPointIdentifier;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.ApiUtils;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourcePointJsonFactory;
import org.scada_lts.web.mvc.api.datasources.SearchDataPointJson;
import org.scada_lts.web.mvc.api.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByAccess;
import static org.scada_lts.utils.ApiUtils.*;
import static org.scada_lts.utils.DataSourcePointApiUtils.toObject;
import static org.scada_lts.utils.UpdateValueUtils.setIf;
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
        DataPointVO fromRequest = toDataPointVO(request, datapoint, ApiUtils::validateObjectForCreate);
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
        DataPointVO toUpdate = getDataPointFromDatabase(request, datapoint.getXid(), datapoint.getId());
        updateObjectDataPointVO(toUpdate, datapoint);
        validateObject(request, toUpdate);
        DataPointJson response;
        try {
            dataPointService.updateDataPointConfiguration(toUpdate);
            response = DataSourcePointJsonFactory.getDataPointJson(toUpdate);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public DataPointJson enableDataPoint(HttpServletRequest request, String xid, Integer id, Boolean enabled) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Enabled is required.", enabled);
        checkArgsIfTwoEmptyThenBadRequest(request, "Id or xid cannot be null.", id, xid);
        DataPointVO toUpdate = getDataPointFromDatabase(request, xid, id);
        DataPointJson response;
        if(toUpdate.isEnabled() == enabled)
            throw new BadRequestException("Data point already is " + (enabled ? "enabled" : "disabled"), request.getRequestURI());
        try {
            toUpdate.setEnabled(enabled);
            Common.ctx.getRuntimeManager().saveDataPoint(toUpdate);
            response = DataSourcePointJsonFactory.getDataPointJson(toUpdate);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
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

    @Deprecated(since = "2.8.0")
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

    public Map<String, Object> getDataPointByXid(HttpServletRequest request, String xid) {
        checkIfNonAdminThenUnauthorized(request);
        if(StringUtils.isEmpty(xid)) {
            throw new ScadaApiException(ScadaErrorMessage.builder(HttpStatus.OK)
                    .detail("Given xid is empty.")
                    .instance(request.getRequestURI())
                    .build());
        }
        return EmportDwr.exportDataPointBy(xid);
    }

    public DataPointVO getDataPointFromDatabase(HttpServletRequest request, String xid, Integer id) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", id, xid);
        User user = Common.getUser(request);

        DataPointVO response;
        if(idExists(id)) {
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

    @Deprecated(since = "2.8.1")
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

    public List<DataPointBean> searchDataPointBean(HttpServletRequest request,
                                                   SearchDataPointJson searchDataPointJson) {
        return searchDataPoint(request, searchDataPointJson).stream().map(DataPointBean::new).collect(Collectors.toList());
    }

    public List<DataPointIdentifier> searchDataPointIdentifiers(HttpServletRequest request,
                                                                SearchDataPointJson searchDataPointJson) {
        return searchDataPoint(request, searchDataPointJson).stream().map(DataPointIdentifier::new).collect(Collectors.toList());
    }

    public List<DataPointVO> searchDataPoint(HttpServletRequest request,
                                             SearchDataPointJson searchDataPointJson) {

        checkIfLogoutThenUnauthorized(request);
        User user = Common.getUser(request);
        Set<Integer> includeIds = searchDataPointJson.getIncludeIds();

        List<DataPointVO> response = searchDataPoint(request, searchDataPointJson, user);
        if(includeIds != null && !includeIds.isEmpty()) {
            if(response.isEmpty()) {
                return filteringAnaPaginationPoints(searchDataPointJson, getDataPoints(request, includeIds, user));
            } else {
                return response;
            }
        }
        return response;
    }

    private List<DataPointVO> searchDataPoint(HttpServletRequest request,
                                              SearchDataPointJson searchDataPointJson,
                                              User user) {
        try {
            if(user.isAdmin()) {
                int page = searchDataPointJson.getPage();
                List<DataPointVO> points;
                int safe = 5;
                List<DataPointVO> reponse = new ArrayList<>();
                do {
                    points = dataPointService.getDataPoints(searchDataPointJson.getKeywordSearch(),
                                    searchDataPointJson.getExcludeIds(), searchDataPointJson.isStartsWith(),
                                    page++, searchDataPointJson.getLimit())
                            .stream().filter(filterByDataTypes(searchDataPointJson))
                            .collect(Collectors.toList());
                    reponse.addAll(points);
                    safe--;
                } while(reponse.size() < searchDataPointJson.getLimit() && safe > 0);

                if(reponse.size() < searchDataPointJson.getLimit()) {
                    points = dataPointService.getDataPoints(searchDataPointJson.getKeywordSearch(),
                            searchDataPointJson.getExcludeIds(), searchDataPointJson.isStartsWith(),
                            -1, -1);
                    return filteringAnaPaginationPoints(searchDataPointJson, points);
                }

                return reponse.stream()
                        .sorted(DataPointExtendedNameComparator.instance)
                        .limit(searchDataPointJson.getLimit())
                        .collect(Collectors.toList());
            } else {
                List<DataPointVO> points = dataPointService.getDataPointsWithAccess(user);
                return filteringAnaPaginationPoints(searchDataPointJson, points);
            }
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    private List<DataPointVO> filteringAnaPaginationPoints(SearchDataPointJson searchDataPointJson, List<DataPointVO> points) {
        return points.stream().sorted(DataPointExtendedNameComparator.instance)
                .filter(filteringPoints(searchDataPointJson))
                .skip((long) searchDataPointJson.getPage() * searchDataPointJson.getLimit())
                .limit(searchDataPointJson.getLimit())
                .collect(Collectors.toList());
    }

    private Predicate<DataPointVO> filteringPoints(SearchDataPointJson searchDataPointJson) {
        return filterByDataTypes(searchDataPointJson)
                .and(filterByExcludeIds(searchDataPointJson))
                .and(filterByKeywords(searchDataPointJson))
                .and(filterByIncludeIds(searchDataPointJson));
    }

    private List<DataPointVO> getDataPoints(HttpServletRequest request, Set<Integer> includeIds, User user) {
        List<DataPointVO> response;
        try {
            response = filteringByAccess(user, dataPointService.getDataPoints(includeIds));
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
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

    private static DataPointVO toDataPointVO(HttpServletRequest request, DataPointJson datapoint,
                                             BiConsumer<HttpServletRequest, DataPointVO> doValidate) {
        DataPointVO dataPointVO;
        try {
            dataPointVO = datapoint.createDataPointVO();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        doValidate.accept(request, dataPointVO);
        return dataPointVO;
    }

    private List<DataPointVO> getDataPointsBy(HttpServletRequest request, String dataSourceXid, Integer dataSourceId) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Data Point id or xid cannot be null.", dataSourceId, dataSourceXid);
        List<DataPointVO> dataPoints;
        try {
            if (idExists(dataSourceId)) {
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

    private static void updateObjectDataPointVO(DataPointVO toUpdate, DataPointJson fromRequest) {
        setIf(fromRequest.getXid(), toUpdate::setXid, a -> !StringUtils.isEmpty(a));
        setIf(fromRequest.getPointLocator(), a -> toUpdate.setPointLocator(a.parsePointLocatorData()), Objects::nonNull);
        setIf(fromRequest.getDescription(), toUpdate::setDescription, a -> !StringUtils.isEmpty(a));
        setIf(fromRequest.getName(), toUpdate::setName, a -> !StringUtils.isEmpty(a));
        setIf(fromRequest.isEnabled(), toUpdate::setEnabled, Objects::nonNull);
        setIf(fromRequest.isSettable(), toUpdate::setSettable, Objects::nonNull);
    }

    private static Predicate<DataPointVO> filterByKeywords(SearchDataPointJson searchDataPointJson) {
        return dataPoint -> StringUtils.isEmpty(searchDataPointJson.getKeywordSearch()) || (dataPoint.getName() != null && (searchDataPointJson.isStartsWith() ? dataPoint.getName().startsWith(searchDataPointJson.getKeywordSearch()) : dataPoint.getName().contains(searchDataPointJson.getKeywordSearch())));
    }

    private static Predicate<DataPointVO> filterByIncludeIds(SearchDataPointJson searchDataPointJson) {
        Set<Integer> includeIds = searchDataPointJson.getIncludeIds();
        return dataPoint -> includeIds == null || includeIds.contains(dataPoint.getId());
    }

    private static Predicate<DataPointVO> filterByExcludeIds(SearchDataPointJson searchDataPointJson) {
        Set<Integer> excludeIds = searchDataPointJson.getExcludeIds();
        return dataPoint -> excludeIds == null || excludeIds.isEmpty() || !excludeIds.contains(dataPoint.getId());
    }

    private static Predicate<DataPointVO> filterByDataTypes(SearchDataPointJson searchDataPointJson) {
        Set<Integer> dataTypes = searchDataPointJson.getDataTypes();
        return dataPoint -> dataTypes == null || dataTypes.isEmpty() || (dataPoint.getPointLocator() == null || dataTypes.contains(dataPoint.getPointLocator().getDataTypeId()));
    }
}
