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
            @CacheEvict(cacheNames = "view_by_id", key = "#view.id"),
            @CacheEvict(cacheNames = "view_by_xid", key = "#view.xid"),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    View save(View view);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", key = "#p0"),
            @CacheEvict(cacheNames = "view_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0")
    })
    void delete(int viewId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", key = "#view.id"),
            @CacheEvict(cacheNames = "view_by_xid", key = "#view.xid"),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #view.id"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #view.id")
    })
    void update(View view);

    @Cacheable(cacheNames = "view_list", key = "'views'")
    List<View> findAll();

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0")
    })
    @Deprecated
    void deleteViewForUser(int viewId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#p1"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0")
    })
    void deleteViewForUser(int viewId, int userId);

    @Cacheable(cacheNames = "permission_view_list_by_user", key = "#p0", condition = "#userId != 0")
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


    @Cacheable(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0", condition = "#viewId != 0")
    List<ShareUser> selectShareUsers(int viewId);

    @Cacheable(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0", condition = "#viewId != 0")
    List<ShareUser> selectShareUsersFromProfile(int viewId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", key = "#p0"),
            @CacheEvict(cacheNames = "view_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsersFromProfile' + #p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", key = "'shareUsers' + #p0")
    })
    default void resetViewById(int viewId){}

    @Caching(evict = {
            @CacheEvict(cacheNames = "view_list", allEntries = true),
            @CacheEvict(cacheNames = "view_by_id", allEntries = true),
            @CacheEvict(cacheNames = "view_by_xid", key = "#viewXid"),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    default void resetViewByXid(String viewXid){}

    @Cacheable(cacheNames = "view_by_id", key = "#p0", condition = "#viewId != 0")
    View findById(int viewId);

    @Cacheable(cacheNames = "view_by_xid", key = "#viewXid", condition = "#viewXid != null")
    View findByXid(String viewXid);
}
