package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = "permission_watchlist_list_by_user")
@Service
public class WatchListUserPermissionsService implements PermissionsService<WatchListAccess, User> {

    private final WatchListDAO watchListDAO;

    public WatchListUserPermissionsService(WatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    @Cacheable(key = "#object.id", unless = "#object == null || #result.isEmpty()")
    public List<WatchListAccess> getPermissions(User object) {
        return watchListDAO.selectWatchListPermissions(object.getId());
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void addOrUpdatePermissions(User object, List<WatchListAccess> toAddOrUpdate) {
        watchListDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    @CacheEvict(key = "#object.id", condition = "#object != null")
    public void removePermissions(User object, List<WatchListAccess> toRemove) {
        watchListDAO.deletePermissions(object.getId(), toRemove);
    }

}
