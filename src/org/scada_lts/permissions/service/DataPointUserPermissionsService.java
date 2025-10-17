package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.DataPointUserDAO;

import java.util.List;

public class DataPointUserPermissionsService implements PermissionsService<DataPointAccess, User> {

    private final DataPointUserDAO dataPointUserDAO;

    public DataPointUserPermissionsService(DataPointUserDAO dataPointUserDAO) {
        this.dataPointUserDAO = dataPointUserDAO;
    }

    @Override
    public List<DataPointAccess> getPermissions(User user) {
        return dataPointUserDAO.selectDataPointPermissions(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<DataPointAccess> toAddOrUpdate) {
        dataPointUserDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<DataPointAccess> toRemove) {
        dataPointUserDAO.deletePermissions(user.getId(), toRemove);
    }

}
