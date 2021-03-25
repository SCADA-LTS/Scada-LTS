package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.DataPointUserDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public class DataPointPermissionsService implements PermissionsService<DataPointAccess, DataPointVO> {

    private final DataPointUserDAO dataPointUserDAO;
    private final DataPointDAO dataPointDAO;

    public DataPointPermissionsService() {
        this.dataPointUserDAO = new DataPointUserDAO();
        this.dataPointDAO = new DataPointDAO();
    }

    public DataPointPermissionsService(DataPointUserDAO dataPointUserDAO, DataPointDAO dataPointDAO) {
        this.dataPointUserDAO = dataPointUserDAO;
        this.dataPointDAO = dataPointDAO;
    }

    @Override
    public List<DataPointAccess> getPermissions(User user) {
        return dataPointUserDAO.selectDataPointPermissions(user.getId());
    }

    @Override
    public List<DataPointAccess> getPermissionsByProfile(UsersProfileVO profile) {
        return dataPointUserDAO.selectDataPointPermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<DataPointAccess> toAddOrUpdate) {
        dataPointUserDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<DataPointAccess> toRemove) {
        dataPointUserDAO.deletePermissions(user.getId(), toRemove);
    }

    @Override
    public List<DataPointVO> getObjectsWithAccess(User user) {
        return dataPointDAO.selectDataPointsWithAccess(user.getId());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        return dataPointDAO.selectDataPointIdentifiersWithAccess(user.getId());
    }
}
