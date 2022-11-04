package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;
import java.util.stream.Collectors;

public class GetDataSourcesWithAccess implements GetObjectsWithAccess<DataSourceVO<?>, User> {

    private final DataSourceDAO dataSourceDAO;
    private final DataPointDAO dataPointDAO;

    public GetDataSourcesWithAccess(DataSourceDAO dataSourceDAO, DataPointDAO dataPointDAO) {
        this.dataSourceDAO = dataSourceDAO;
        this.dataPointDAO = dataPointDAO;
    }

    @Override
    public List<DataSourceVO<?>> getObjectsWithAccess(User object) {
        if(object.isAdmin())
            return dataSourceDAO.getDataSources();
        return filteringByAccess(object, dataSourceDAO.getDataSources(), dataPointDAO);
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User object) {
        return getObjectsWithAccess(object).stream()
                .map(a -> new ScadaObjectIdentifier(a.getId(), a.getXid(), a.getName()))
                .collect(Collectors.toList());
    }

    public static List<DataSourceVO<?>> filteringByAccess(User user, List<DataSourceVO<?>> dataSources, DataPointDAO dataPointDAO) {
        return dataSources.stream()
                .filter(source -> GetDataSourcesWithAccess.hasDataSourceReadPermission(user, source, dataPointDAO))
                .collect(Collectors.toList());
    }

    public static List<DataSourceVO<?>> filteringByNoAccess(User user, List<DataSourceVO<?>> dataSources, DataPointDAO dataPointDAO) {
        return dataSources.stream()
                .filter(source -> !GetDataSourcesWithAccess.hasDataSourceReadPermission(user, source, dataPointDAO))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasReadPermission(User user, DataSourceVO<?> object) {
        return GetDataSourcesWithAccess.hasDataSourceReadPermission(user, object, dataPointDAO);
    }

    @Override
    public boolean hasSetPermission(User user, DataSourceVO<?> object) {
        return hasReadPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, DataSourceVO<?> object) {
        return user.isAdmin();
    }

    public static boolean hasDataSourceReadPermission(User user, DataSourceVO<?> dataSource, DataPointDAO dataPointDAO) {
        List<DataPointVO> dataPoints = dataPointDAO.getDataPoints(dataSource.getId());
        for (DataPointVO dataPoint: dataPoints) {
            if(Permissions.hasDataPointReadPermission(user, dataPoint)) {
                return true;
            }
        }
        return false;
    }
}
