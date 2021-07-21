package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.dao.watchlist.WatchListDAO;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

public class WatchListGetShareUsers implements GetShareUsers<WatchList> {

    private final WatchListDAO watchListDAO;
    private final UsersProfileDAO usersProfileDAO;

    public WatchListGetShareUsers() {
        this.watchListDAO = new WatchListDAO();
        this.usersProfileDAO = new UsersProfileDAO();
    }

    public WatchListGetShareUsers(WatchListDAO watchListDAO, UsersProfileDAO usersProfileDAO) {
        this.watchListDAO = watchListDAO;
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<ShareUser> getShareUsers(WatchList object) {
        return watchListDAO.getWatchListUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersFromProfile(WatchList object) {
        return usersProfileDAO.selectWatchListShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(WatchList object) {
        List<ShareUser> shareUsers = watchListDAO.getWatchListUsers(object.getId());
        List<ShareUser> fromProfile = usersProfileDAO.selectWatchListShareUsers(object.getId());
        return merge(shareUsers, fromProfile);
    }
}
