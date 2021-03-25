package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourcePermissionsService implements PermissionsService<Integer, DataSourceVO<?>> {

    private final DataSourceDAO dataSourceDAO;
    private static Map<Integer, List<ScadaObjectIdentifier>> objectWithAccesses = new ConcurrentHashMap<>();

    public DataSourcePermissionsService() {
        this.dataSourceDAO = new DataSourceDAO();
    }

    public DataSourcePermissionsService(DataSourceDAO dataSourceDAO) {
        this.dataSourceDAO = dataSourceDAO;
    }

    @Override
    public List<Integer> getPermissions(User user) {
        return dataSourceDAO.selectDataSourcePermissions(user.getId());
    }

    @Override
    public List<Integer> getPermissionsByProfile(UsersProfileVO profile) {
        return dataSourceDAO.selectDataSourcePermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<Integer> toAddOrUpdate) {
        dataSourceDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<Integer> toRemove) {
        dataSourceDAO.deletePermissions(user.getId(), toRemove);
    }

    @Override
    public List<DataSourceVO<?>> getObjectsWithAccess(User user) {
        return dataSourceDAO.selectDataSourcesWithAccess(user.getId());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        return dataSourceDAO.selectDataSourceIdentifiersWithAccess(user.getId());
    }
}
