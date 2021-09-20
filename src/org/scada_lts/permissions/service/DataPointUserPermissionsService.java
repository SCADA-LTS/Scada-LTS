package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.DataPointUserDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataPointUserPermissionsService implements PermissionsService<DataPointAccess, User> {

    private final DataPointUserDAO dataPointUserDAO;

    public DataPointUserPermissionsService(DataPointUserDAO dataPointUserDAO) {
        this.dataPointUserDAO = dataPointUserDAO;
    }

    @Override
    public List<DataPointAccess> getPermissions(User object) {
        return dataPointUserDAO.selectDataPointPermissions(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(User object, List<DataPointAccess> toAddOrUpdate) {
        dataPointUserDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User object, List<DataPointAccess> toRemove) {
        dataPointUserDAO.deletePermissions(object.getId(), toRemove);
    }

}
