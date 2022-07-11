package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.springframework.cache.annotation.*;

import java.util.List;

public interface ViewCachable {

    String CACHE_ENABLED_KEY = "view.cache.enabled";

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    int insertView(View view);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    void deleteView(View view);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    void updateView(View view);

    @Cacheable(cacheNames = "view_list", key = "'views'")
    List<View> selectViews();

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #viewId"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #viewId")
    })
    void deleteViewForUser(int viewId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#userId"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #viewId"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #viewId")
    })
    void deleteViewForUser(int viewId, int userId);

    @Cacheable(cacheNames = "permission_view_list_by_user", key = "#userId", condition = "#userId != 0")
    List<ViewAccess> selectViewPermissions(int userId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#userId"),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int[] insertPermissions(int userId, List<ViewAccess> toAddOrUpdate);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#userId"),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int[] deletePermissions(int userId, List<ViewAccess> toRemove);


    @Cacheable(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #viewId", condition = "#viewId != 0")
    List<ShareUser> selectShareUsers(int viewId);

    @Cacheable(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #viewId", condition = "#viewId != 0")
    List<ShareUser> selectShareUsersFromProfile(int viewId);
}
