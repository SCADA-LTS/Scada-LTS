package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.utils.ApplicationContextProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

@Service
@CacheConfig(cacheNames = "share_user_list_by_watchlist")
public class WatchListGetShareUsers implements GetShareUsers<WatchList> {

    private final WatchListDAO watchListDAO;

    public WatchListGetShareUsers() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        this.watchListDAO = (WatchListDAO) context.getBean("watchListDAO");
    }

    public WatchListGetShareUsers(WatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    @Cacheable(key = "'shareUsers' + #object.id", unless = "#object == null || #result.isEmpty()")
    public List<ShareUser> getShareUsers(WatchList object) {
        return watchListDAO.getWatchListUsers(object.getId());
    }

    @Override
    @Cacheable(key = "'shareUsersFromProfile' + #object.id", unless = "#object == null || #result.isEmpty()")
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
