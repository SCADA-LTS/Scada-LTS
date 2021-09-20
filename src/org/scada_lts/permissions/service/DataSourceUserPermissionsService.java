package org.scada_lts.permissions.service;


import com.serotonin.mango.vo.User;
import org.scada_lts.dao.DataSourceDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSourceUserPermissionsService implements PermissionsService<Integer, User> {

    private final DataSourceDAO dataSourceDAO;

    public DataSourceUserPermissionsService(DataSourceDAO dataSourceDAO) {
        this.dataSourceDAO = dataSourceDAO;
    }

    @Override
    public List<Integer> getPermissions(User object) {
        return dataSourceDAO.selectDataSourcePermissions(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(User object, List<Integer> toAddOrUpdate) {
        dataSourceDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User object, List<Integer> toRemove) {
        dataSourceDAO.deletePermissions(object.getId(), toRemove);
    }
}
