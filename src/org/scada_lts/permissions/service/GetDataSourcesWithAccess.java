package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;
import java.util.stream.Collectors;

public class GetDataSourcesWithAccess implements GetObjectsWithAccess<DataSourceVO<?>, User> {

    private final DataSourceDAO dataSourceDAO;

    public GetDataSourcesWithAccess(DataSourceDAO dataSourceDAO) {
        this.dataSourceDAO = dataSourceDAO;
    }

    @Override
    public List<DataSourceVO<?>> getObjectsWithAccess(User object) {
        if(object.isAdmin())
            return dataSourceDAO.getDataSources();
        return dataSourceDAO.selectDataSourcesWithAccess(object.getId(), object.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User object) {
        if(object.isAdmin())
            return dataSourceDAO.findIdentifiers();
        return dataSourceDAO.selectDataSourceIdentifiersWithAccess(object.getId(), object.getUserProfile());
    }

    public static List<DataSourceVO<?>> filteringByAccess(User user, List<DataSourceVO<?>> dataSources) {
        return dataSources.stream()
                .filter(source -> Permissions.hasDataSourcePermission(user, source.getId()))
                .collect(Collectors.toList());
    }

    public static List<DataSourceVO<?>> filteringByNoAccess(User user, List<DataSourceVO<?>> dataSources) {
        return dataSources.stream()
                .filter(source -> !Permissions.hasDataSourcePermission(user, source.getId()))
                .collect(Collectors.toList());
    }

    public static boolean hasDataSourceReadPermission(User user, DataSourceVO<?> dataSource) {
        return Permissions.hasDataSourcePermission(user, dataSource.getId());
    }
}
