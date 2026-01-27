package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.GetExtendedNameComparator;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.Collections;
import java.util.List;

import static org.scada_lts.permissions.service.util.GetSortUtils.getAndSort;

public class GetDataSourcesWithAccess implements GetObjectsWithAccess<DataSourceVO<?>, User> {

    private static final Log LOG = LogFactory.getLog(GetDataSourcesWithAccess.class);

    private final DataSourceDAO dataSourceDAO;

    public GetDataSourcesWithAccess(DataSourceDAO dataSourceDAO) {
        this.dataSourceDAO = dataSourceDAO;
    }

    @Override
    public List<DataSourceVO<?>> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        return getAndSort(user, dataSourceDAO::getDataSources,
                dataSourceDAO::selectDataSourcesWithAccess,
                GetExtendedNameComparator.instance);
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        return getAndSort(user, dataSourceDAO::findIdentifiers,
                dataSourceDAO::selectDataSourceIdentifiersWithAccess,
                GetExtendedNameComparator.instance);
    }

    @Override
    public boolean hasReadPermission(User user, DataSourceVO<?> object) {
        return GetDataSourcesWithAccess.hasDataSourceReadPermission(user, object);
    }

    @Override
    public boolean hasSetPermission(User user, DataSourceVO<?> object) {
        return hasReadPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, DataSourceVO<?> object) {
        return user.isAdmin();
    }

    public static boolean hasDataSourceReadPermission(User user, DataSourceVO<?> dataSource) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(dataSource == null) {
            LOG.warn("dataSource is null");
            return false;
        }
        return Permissions.hasDataSourcePermission(user, dataSource.getId());
    }

    @Deprecated(since = "2.8.1")
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
