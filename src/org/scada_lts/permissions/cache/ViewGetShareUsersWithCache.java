package org.scada_lts.permissions.cache;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.permissions.service.GetShareUsers;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "share_user_list_by_view")
public class ViewGetShareUsersWithCache implements GetShareUsers<View> {

    private final GetShareUsers<View> getShareUsers;

    public ViewGetShareUsersWithCache(GetShareUsers<View> getShareUsers) {
        this.getShareUsers = getShareUsers;
    }

    @Override
    @Cacheable(key = "'shareUsers' + #object.id", condition = "#object != null")
    public List<ShareUser> getShareUsers(View object) {
        return getShareUsers.getShareUsers(object);
    }

    @Override
    @Cacheable(key = "'shareUsersFromProfile' + #object.id", condition = "#object != null")
    public List<ShareUser> getShareUsersFromProfile(View object) {
        return getShareUsers.getShareUsersFromProfile(object);
    }

    @Override
    @Cacheable(key = "'shareUsersWithProfile' + #object.id", condition = "#object != null")
    public List<ShareUser> getShareUsersWithProfile(View object) {
        return getShareUsers.getShareUsersWithProfile(object);
    }
}
