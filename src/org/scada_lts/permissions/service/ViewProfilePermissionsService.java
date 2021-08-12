package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.UsersProfileDaoCachable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewProfilePermissionsService implements PermissionsService<ViewAccess, UsersProfileVO> {

    private final UsersProfileDaoCachable usersProfileDAO;

    public ViewProfilePermissionsService(UsersProfileDaoCachable usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<ViewAccess> getPermissions(UsersProfileVO object) {
        return usersProfileDAO.selectViewPermissionsByProfileId(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO object, List<ViewAccess> toAddOrUpdate) {
        usersProfileDAO.insertViewUsersProfile(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO object, List<ViewAccess> toRemove) {
        usersProfileDAO.deleteViewUsersProfile(object.getId(), toRemove);
    }
}
