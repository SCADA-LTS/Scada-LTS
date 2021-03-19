package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.ViewDAO;

import java.util.List;

public class ViewPermissionsService implements PermissionsService<ViewAccess, View>, GetShareUsers<View> {

    private final ViewDAO viewDAO;

    public ViewPermissionsService() {
        viewDAO = new ViewDAO();
    }

    public ViewPermissionsService(ViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<ViewAccess> getPermissions(User user) {
        return viewDAO.selectViewPermissions(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<ViewAccess> toAddOrUpdate) {
        viewDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<ViewAccess> toRemove) {
        viewDAO.deletePermissions(user.getId(), toRemove);
    }

    @Override
    public List<ViewAccess> getPermissionsByProfile(UsersProfileVO profile) {
        return viewDAO.selectViewPermissionsByProfileId(profile.getId());
    }

    @Override
    public List<View> getObjectsWithAccess(User user) {
        return viewDAO.selectViewWithAccess(user.getId());
    }

    @Override
    public List<ShareUser> getShareUsers(View object) {
        return viewDAO.getShareUsers(object.getId());
    }


}
