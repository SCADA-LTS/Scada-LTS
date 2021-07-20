package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.dao.ViewDAO;

import java.util.List;

public class ViewGetShareUsers implements GetShareUsers<View> {

    private final ViewDAO viewDAO;
    private final UsersProfileDAO usersProfileDAO;

    public ViewGetShareUsers() {
        this.viewDAO = new ViewDAO();
        this.usersProfileDAO = new UsersProfileDAO();
    }

    public ViewGetShareUsers(ViewDAO viewDAO, UsersProfileDAO usersProfileDAO) {
        this.viewDAO = viewDAO;
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<ShareUser> getShareUsers(View object) {
        return viewDAO.getShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersFromProfile(View object) {
        return usersProfileDAO.selectViewShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(View object) {
        List<ShareUser> shareUsers = viewDAO.getShareUsers(object.getId());
        shareUsers.addAll(usersProfileDAO.selectViewShareUsers(object.getId()));
        return shareUsers;
    }
}
