package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.watchlist.WatchListDAO;

import java.util.List;

public class WatchListPermissionsService implements PermissionsService<WatchListAccess, WatchList>,
        GetShareUsers<WatchList> {

    private final WatchListDAO watchListDAO;

    public WatchListPermissionsService() {
        this.watchListDAO = new WatchListDAO();
    }

    public WatchListPermissionsService(WatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    public List<WatchListAccess> getPermissions(User user) {
        return watchListDAO.selectWatchListPermissions(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<WatchListAccess> toAddOrUpdate) {
        watchListDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<WatchListAccess> toRemove) {
        watchListDAO.deletePermissions(user.getId(), toRemove);
    }

    @Override
    public List<WatchListAccess> getPermissionsByProfile(UsersProfileVO profile) {
        return watchListDAO.selectWatchListPermissionsByProfileId(profile.getId());
    }

    @Override
    public List<WatchList> getObjectsWithAccess(User user) {
        return watchListDAO.selectWatchListsWithAccess(user.getId());
    }

    @Override
    public List<ShareUser> getShareUsers(WatchList object) {
        return watchListDAO.getWatchListUsers(object.getId());
    }
}
