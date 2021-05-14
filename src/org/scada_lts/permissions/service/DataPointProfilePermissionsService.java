package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.DataPointUserDAO;
import org.scada_lts.dao.UsersProfileDAO;


import java.util.List;

public class DataPointProfilePermissionsService implements PermissionsService<DataPointAccess, UsersProfileVO> {

    private final DataPointUserDAO dataPointUserDAO;
    private final UsersProfileDAO usersProfileDAO;

    public DataPointProfilePermissionsService() {
        this.usersProfileDAO = new UsersProfileDAO();
        this.dataPointUserDAO = new DataPointUserDAO();
    }

    public DataPointProfilePermissionsService(UsersProfileDAO usersProfileDAO, DataPointUserDAO dataPointUserDAO) {
        this.usersProfileDAO = usersProfileDAO;
        this.dataPointUserDAO = dataPointUserDAO;
    }

    @Override
    public List<DataPointAccess> getPermissions(UsersProfileVO user) {
        return dataPointUserDAO.selectDataPointPermissionsByProfileId(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO profile, List<DataPointAccess> toAddOrUpdate) {
        usersProfileDAO.insertDataPointUsersProfile(profile.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO user, List<DataPointAccess> toRemove) {
        usersProfileDAO.deleteDataPointUsersProfile(user.getId(), toRemove);
    }
}
