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
    public List<DataPointAccess> getPermissions(UsersProfileVO profile) {
        return usersProfileDAO.selectDataPointPermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO profile, List<DataPointAccess> toAddOrUpdate) {
        usersProfileDAO.insertDataPointUsersProfile(profile.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO profile, List<DataPointAccess> toRemove) {
        usersProfileDAO.deleteDataPointUsersProfile(profile.getId(), toRemove);
    }
}
