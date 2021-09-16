package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDaoCachable {

    @Cacheable(cacheNames = "user_list", key="'userIds'")
    List<Integer> getAll();
    @Cacheable(cacheNames = "user_list", key="'users'")
    List<User> getUsers();
    @Cacheable(cacheNames = "user_list", key="'activeUsers'")
    List<User> getActiveUsers();

    @Cacheable(cacheNames = "user_by_username", key = "#p0", unless = "#result == null")
    User getUser(String username);

    @Cacheable(cacheNames = "user_by_id", key = "#p0", unless = "#result == null")
    User getUser(int id);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#user.id"),
                    @CacheEvict(cacheNames = "user_by_username", key = "#user.username")
            }
    )
    void update(User user);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#user.id"),
                    @CacheEvict(cacheNames = "user_by_username", key = "#user.username")
            }
    )
    void updateHideMenu(User user);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#user.id"),
                    @CacheEvict(cacheNames = "user_by_username", key = "#user.username")
            }
    )
    void updateScadaTheme(User user);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#p0"),
                    @CacheEvict(cacheNames = "user_by_username", allEntries = true)
            }
    )
    void updateHomeUrl(int userId, String homeUrl);
    void updateLogin(int userId);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#p0"),
                    @CacheEvict(cacheNames = "user_by_username", allEntries = true),
                    @CacheEvict(cacheNames = "permission_datasource_list_by_user", key = "#p0"),
                    @CacheEvict(cacheNames = "permission_datapoint_list_by_user", key = "#p0"),
                    @CacheEvict(cacheNames = "permission_watchlist_list_by_user", key = "#p0"),
                    @CacheEvict(cacheNames = "permission_view_list_by_user", key = "#p0"),
                    @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true)
            }
    )
    void delete(int userId);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true)
            }
    )
    int insert(User user);
}
