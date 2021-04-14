package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.dao.watchlist.WatchListDAO;

import java.util.List;


public class WatchListProfilePermissionsService implements PermissionsService<WatchListAccess, UsersProfileVO>{

    private final WatchListDAO watchListDAO;
    private final UsersProfileDAO usersProfileDAO;

    public WatchListProfilePermissionsService() {
        this.watchListDAO = new WatchListDAO();
        this.usersProfileDAO = new UsersProfileDAO();
    }

    public WatchListProfilePermissionsService(WatchListDAO watchListDAO, UsersProfileDAO usersProfileDAO) {
        this.watchListDAO = watchListDAO;
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<WatchListAccess> getPermissions(UsersProfileVO profile) {
        return watchListDAO.selectWatchListPermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO profile, List<WatchListAccess> toAddOrUpdate) {
        usersProfileDAO.insertWatchListUsersProfile(profile.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO profile, List<WatchListAccess> toRemove) {
        usersProfileDAO.deleteWatchListUsersProfile(profile.getId(), toRemove);
    }
}
