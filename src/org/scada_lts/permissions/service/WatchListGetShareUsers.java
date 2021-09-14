package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.UserDaoCachable;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

public class WatchListGetShareUsers implements GetShareUsers<WatchList> {

    private final WatchListDAO watchListDAO;
    private final UserDaoCachable userDAO;

    public WatchListGetShareUsers() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        this.watchListDAO = (WatchListDAO) context.getBean("watchListDAO");
        this.userDAO = (UserDaoCachable) context.getBean("userDAO");
    }

    public WatchListGetShareUsers(WatchListDAO watchListDAO, UserDaoCachable userDAO) {
        this.watchListDAO = watchListDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<ShareUser> getShareUsers(WatchList object) {
        return watchListDAO.getWatchListUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersFromProfile(WatchList object) {
        return userDAO.selectWatchListShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(WatchList object) {
        List<ShareUser> shareUsers = watchListDAO.getWatchListUsers(object.getId());
        List<ShareUser> fromProfile = userDAO.selectWatchListShareUsers(object.getId());
        return merge(shareUsers, fromProfile);
    }
}
