package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.IUsersProfileDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewProfilePermissionsService implements PermissionsService<ViewAccess, UsersProfileVO> {

    private final IUsersProfileDAO usersProfileDAO;

    public ViewProfilePermissionsService(IUsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<ViewAccess> getPermissions(UsersProfileVO profile) {
        return usersProfileDAO.selectViewPermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO profile, List<ViewAccess> toAddOrUpdate) {
        usersProfileDAO.insertViewUsersProfile(profile.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO profile, List<ViewAccess> toRemove) {
        usersProfileDAO.deleteViewUsersProfile(profile.getId(), toRemove);
    }
}
