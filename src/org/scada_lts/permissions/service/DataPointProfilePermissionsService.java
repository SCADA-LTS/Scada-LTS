package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.IUsersProfileDAO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class DataPointProfilePermissionsService implements PermissionsService<DataPointAccess, UsersProfileVO> {

    private final IUsersProfileDAO usersProfileDAO;

    public DataPointProfilePermissionsService(IUsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<DataPointAccess> getPermissions(UsersProfileVO user) {
        return usersProfileDAO.selectDataPointPermissionsByProfileId(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO object, List<DataPointAccess> toAddOrUpdate) {
        usersProfileDAO.insertDataPointUsersProfile(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO object, List<DataPointAccess> toRemove) {
        usersProfileDAO.deleteDataPointUsersProfile(object.getId(), toRemove);
    }
}
