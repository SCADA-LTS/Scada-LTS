package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.dao.ViewDAO;

import java.util.List;

public class ViewProfilePermissionsService implements PermissionsService<ViewAccess, UsersProfileVO> {

    private final ViewDAO viewDAO;
    private final UsersProfileDAO usersProfileDAO;

    public ViewProfilePermissionsService() {
        this.viewDAO = new ViewDAO();
        this.usersProfileDAO = new UsersProfileDAO();
    }

    public ViewProfilePermissionsService(ViewDAO viewDAO, UsersProfileDAO usersProfileDAO) {
        this.viewDAO = viewDAO;
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<ViewAccess> getPermissions(UsersProfileVO user) {
        return viewDAO.selectViewPermissionsByProfileId(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO user, List<ViewAccess> toAddOrUpdate) {
        usersProfileDAO.insertViewUsersProfile(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO user, List<ViewAccess> toRemove) {
        usersProfileDAO.deleteViewUsersProfile(user.getId(), toRemove);
    }
}
