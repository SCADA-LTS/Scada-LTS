package org.scada_lts.permissions.cache;

import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.User;
import org.scada_lts.permissions.service.PermissionsService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "permission_watchlist_list_by_user")
public class WatchListUserPermissionsServiceWithCache implements PermissionsService<WatchListAccess, User> {

    private final PermissionsService<WatchListAccess, User> service;

    public WatchListUserPermissionsServiceWithCache(PermissionsService<WatchListAccess, User> service) {
        this.service = service;
    }

    @Override
    @Cacheable(key = "#object.id", condition = "#object != null")
    public List<WatchListAccess> getPermissions(User object) {
        return service.getPermissions(object);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<WatchListAccess> toAddOrUpdate) {
        service.addOrUpdatePermissions(object, toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<WatchListAccess> toRemove) {
        service.removePermissions(object, toRemove);
    }

}
