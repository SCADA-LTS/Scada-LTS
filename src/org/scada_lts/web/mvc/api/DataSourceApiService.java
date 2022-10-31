package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.scada_lts.dao.model.DataSourceIdentifier;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.permissions.service.GetDataSourcesWithAccess;
import org.scada_lts.utils.DataSourcePointApiUtils;
import org.scada_lts.web.mvc.api.datasources.DataPointJson;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;
import org.scada_lts.web.mvc.api.datasources.DataSourcePointJsonFactory;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scada_lts.permissions.service.GetDataSourcesWithAccess.filteringByAccess;
import static org.scada_lts.utils.DataSourcePointApiUtils.toObject;
import static org.scada_lts.utils.ValidationUtils.*;

public class DataSourceApiService implements ObjectApiService<DataSourceJson, DataSourceIdentifier> {

    private final DataSourceService dataSourceService;

    public DataSourceApiService(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    public Map<String, Object> isUniqueXid(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id and xid cannot be null.", id, xid);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUnique = dataSourceService.isXidUnique(xid, id);
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
            response = dataSourceService.generateUniqueXid();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    @Override
    public DataSourceJson create(HttpServletRequest request, DataSourceJson dataSource) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Data Source cannot be null.", dataSource);

        DataSourceVO<?> vo = toDataSourceVO(request, dataSource);
        DataSourceJson response;
        try {
            DataSourceVO<?> created = dataSourceService.createDataSource(vo);
            response = DataSourcePointJsonFactory.getDataSourceJson(created);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    @Override
    public DataSourceJson update(HttpServletRequest request, DataSourceJson dataSource) {
        checkArgsIfEmptyThenBadRequest(request, "Data Source cannot be null.", dataSource);
        getDataSourceFromDatabase(request, dataSource.getXid(), dataSource.getId());
        DataSourceVO<?> fromRequest = toDataSourceVO(request, dataSource);
        try {
            dataSourceService.updateAndInitializeDataSource(fromRequest);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return dataSource;
    }

    @Override
    public DataSourceJson delete(HttpServletRequest request, String xid, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);

        DataSourceVO<?> toDelete = getDataSourceFromDatabase(request, xid, id);
        try {
            dataSourceService.deleteDataSource(toDelete.getId());
            return DataSourcePointJsonFactory.getDataSourceJson(toDelete);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @Override
    public List<DataSourceIdentifier> getIdentifiers(HttpServletRequest request) {
        User user = Common.getUser(request);

        List<DataSourceIdentifier> response;
        try {

            response = dataSourceService.getDataSourcesWithAccess(user).stream()
                    .map(DataSourceVO::toIdentifier)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public List<DataSourceJson> getDataSources(HttpServletRequest request) {
        User user = Common.getUser(request);
        List<DataSourceJson> response;
        try {
            List<DataSourceVO<?>> dataSources = dataSourceService.getDataSourcesWithAccess(user);
            response = dataSources.stream()
                    .map(DataSourcePointJsonFactory::getDataSourceJson)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public DataSourceJson getDataSource(HttpServletRequest request, String xid, Integer id) {
        DataSourceVO<?> fromDatabase = getDataSourceFromDatabase(request, xid, id);
        try {
            return DataSourcePointJsonFactory.getDataSourceJson(fromDatabase);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    private DataSourceVO<?> getDataSourceFromDatabase(HttpServletRequest request, String xid, Integer id) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Id or xid cannot be null.", id, xid);
        User user = Common.getUser(request);
        DataSourceVO<?> response;
        if(id != null) {
            response = toObject(id, user, request, dataSourceService::getDataSource,
                    GetDataSourcesWithAccess::hasDataSourceReadPermission,
                    a -> a);
        } else {
            response = toObject(xid, user, request, dataSourceService::getDataSource,
                    GetDataSourcesWithAccess::hasDataSourceReadPermission,
                    a -> a);
        }
        return response;
    }

    public Map<String, Object> toggleDataSource(HttpServletRequest request, Integer id) {
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
        return response;
    }

    public List<DataPointJson> enableAllPointsInDataSource(HttpServletRequest request, Integer id) {
        checkIfNonAdminThenUnauthorized(request);
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);
        User user = Common.getUser(request);

        List<DataPointJson> response;
        try {
            response = dataSourceService.enableAllDataPointsInDS(id, user)
                    .stream().map(DataPointJson::new)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return response;
    }

    public List<DataSourceIdentifier> getDataSourcesPlcIdentifers(HttpServletRequest request) {
        User user = Common.getUser(request);
        List<DataSourceVO<?>> list;
        try {
            list = filteringByAccess(user, dataSourceService.getDataSourcesPlc());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return list.stream()
                .map(DataSourceVO::toIdentifier)
                .collect(Collectors.toList());
    }

    private static DataSourceVO<?> toDataSourceVO(HttpServletRequest request, DataSourceJson dataSource) {
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
