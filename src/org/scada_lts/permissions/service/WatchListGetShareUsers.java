package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.watchlist.WatchListDAO;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

public class WatchListGetShareUsers implements GetShareUsers<WatchList> {

    private final WatchListDAO watchListDAO;

    public WatchListGetShareUsers(WatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    public List<ShareUser> getShareUsers(WatchList object) {
        return watchListDAO.getWatchListUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersFromProfile(WatchList object) {
        return watchListDAO.selectWatchListShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(WatchList object) {
        List<ShareUser> shareUsers = getShareUsers(object);
        List<ShareUser> fromProfile = getShareUsersFromProfile(object);
        return merge(shareUsers, fromProfile);
    }
}
