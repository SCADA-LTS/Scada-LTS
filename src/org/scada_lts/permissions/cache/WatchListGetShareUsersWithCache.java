package org.scada_lts.permissions.cache;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.permissions.service.GetShareUsers;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@CacheConfig(cacheNames = "share_user_list_by_watchlist")
public class WatchListGetShareUsersWithCache implements GetShareUsers<WatchList> {

    private final GetShareUsers<WatchList> getShareUsers;

    public WatchListGetShareUsersWithCache(GetShareUsers<WatchList> getShareUsers) {
        this.getShareUsers = getShareUsers;
    }

    @Override
    @Cacheable(key = "'shareUsers' + #object.id", condition = "#object != null")
    public List<ShareUser> getShareUsers(WatchList object) {
        return getShareUsers.getShareUsers(object);
    }

    @Override
    @Cacheable(key = "'shareUsersFromProfile' + #object.id", condition = "#object != null")
    public List<ShareUser> getShareUsersFromProfile(WatchList object) {
        return getShareUsers.getShareUsersFromProfile(object);
    }

    @Override
    @Cacheable(key = "'shareUsersWithProfile' + #object.id", condition = "#object != null")
    public List<ShareUser> getShareUsersWithProfile(WatchList object) {
        return getShareUsers.getShareUsersWithProfile(object);
    }
}
