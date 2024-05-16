package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.model.BaseObjectIdentifier;
import org.springframework.cache.annotation.*;

import java.util.List;

public interface ViewCacheable {

    String CACHE_ENABLED_KEY = "view.cache.enabled";

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", key = "#view.id"),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    View save(View view);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", key = "#p0"),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0")
    })
    void delete(int viewId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_by_id", key = "#view.id"),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    void update(View view);

    @Cacheable(cacheNames = "view_list", key = "'views'")
    List<BaseObjectIdentifier> findIdentifiers();

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#p1"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0")
    })
    void deleteViewForUser(int viewId, int userId);

    @Cacheable(cacheNames = "permission_view_list_by_user", key = "#p0", condition = "#p0 > -1")
    List<ViewAccess> selectViewPermissions(int userId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int[] insertPermissions(int userId, List<ViewAccess> toAddOrUpdate);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int[] deletePermissions(int userId, List<ViewAccess> toRemove);


    @Cacheable(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0", condition = "#p0 > 0")
    List<ShareUser> selectShareUsers(int viewId);

    @Cacheable(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0", condition = "#p0 > 0")
    List<ShareUser> selectShareUsersFromProfile(int viewId);

    @Cacheable(cacheNames = "view_by_id", key = "#p0", condition = "#p0 > 0", unless="#result == null")
    View findById(int viewId);

    @CachePut(cacheNames = "view_by_id", key = "#view.id", condition = "#view != null && #view.id > 0")
    default View put(View view) {
        return view;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    default void resetCache() {}
}
