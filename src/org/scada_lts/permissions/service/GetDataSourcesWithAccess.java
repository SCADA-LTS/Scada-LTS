package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DataPointDAO;
import org.scada_lts.dao.impl.DataSourceDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.Collections;
import java.util.List;

public class GetDataSourcesWithAccess implements GetObjectsWithAccess<DataSourceVO<?>, User> {

    private static final Log LOG = LogFactory.getLog(GetDataSourcesWithAccess.class);

    private final DataSourceDAO dataSourceDAO;
    private final DataPointDAO dataPointDAO;

    public GetDataSourcesWithAccess(DataSourceDAO dataSourceDAO, DataPointDAO dataPointDAO) {
        this.dataSourceDAO = dataSourceDAO;
        this.dataPointDAO = dataPointDAO;
    }

    @Override
    public List<DataSourceVO<?>> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return dataSourceDAO.getDataSources();
        return dataSourceDAO.selectDataSourcesWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return dataSourceDAO.findIdentifiers();
        return dataSourceDAO.selectDataSourceIdentifiersWithAccess(user.getId(), user.getUserProfile());
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
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(dataSource == null) {
            LOG.warn("dataSource is null");
            return false;
        }
        if(dataPointDAO == null) {
            LOG.warn("dataPointDAO is null");
            return false;
        }
        if(Permissions.hasDataSourcePermission(user, dataSource.getId()))
            return true;
        List<ScadaObjectIdentifier> dataPoints = dataPointDAO.findIdentifiers(dataSource.getId());
        for (ScadaObjectIdentifier dataPoint: dataPoints) {
            if(Permissions.hasDataPointReadPermission(user, dataSource.getId(), dataPoint.getId())) {
                return true;
            }
        }
        return false;
    }
}
