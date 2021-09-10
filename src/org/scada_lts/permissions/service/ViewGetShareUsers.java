package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.dao.UserDaoCachable;
import org.scada_lts.dao.ViewDAO;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

public class ViewGetShareUsers implements GetShareUsers<View> {

    private final ViewDAO viewDAO;
    private final UserDaoCachable userDAO;

    public ViewGetShareUsers() {
        this.viewDAO = new ViewDAO();
        this.userDAO = new UserDAO();
    }

    public ViewGetShareUsers(ViewDAO viewDAO, UserDaoCachable userDAO) {
        this.viewDAO = viewDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<ShareUser> getShareUsers(View object) {
        return viewDAO.getShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersFromProfile(View object) {
        return userDAO.selectViewShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(View object) {
        List<ShareUser> shareUsers = viewDAO.getShareUsers(object.getId());
        List<ShareUser> fromProfile = userDAO.selectViewShareUsers(object.getId());
        return merge(shareUsers, fromProfile);
    }
}
