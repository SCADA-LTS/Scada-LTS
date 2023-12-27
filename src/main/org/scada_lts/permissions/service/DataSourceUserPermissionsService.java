package org.scada_lts.permissions.service;


import com.serotonin.mango.vo.User;
import org.scada_lts.dao.DataSourceDAO;

import java.util.List;

public class DataSourceUserPermissionsService implements PermissionsService<Integer, User> {

    private final DataSourceDAO dataSourceDAO;

    public DataSourceUserPermissionsService(DataSourceDAO dataSourceDAO) {
        this.dataSourceDAO = dataSourceDAO;
    }

    @Override
    public List<Integer> getPermissions(User user) {
        return dataSourceDAO.selectDataSourcePermissions(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<Integer> toAddOrUpdate) {
        dataSourceDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<Integer> toRemove) {
        dataSourceDAO.deletePermissions(user.getId(), toRemove);
    }
}
