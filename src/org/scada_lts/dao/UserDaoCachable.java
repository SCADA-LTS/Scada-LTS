package org.scada_lts.dao;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDaoCachable {

    @Cacheable(cacheNames = "user_list", key="'userIds'", unless = "#result.isEmpty()")
    List<Integer> getAll();
    @Cacheable(cacheNames = "user_list", key="'users'", unless = "#result.isEmpty()")
    List<User> getUsers();
    @Cacheable(cacheNames = "user_list", key="'activeUsers'", unless = "#result.isEmpty()")
    List<User> getActiveUsers();

    @Cacheable(cacheNames = "share_user_datasource", key = "#p0", unless = "#result.isEmpty()")
    List<ShareUser> selectDataSourceShareUsers(int dataSourceId);
    @Cacheable(cacheNames = "share_user_datapoint", key = "#p0", unless = "#result.isEmpty()")
    List<ShareUser> selectDataPointShareUsers(int dataPointId);
    @Cacheable(cacheNames = "share_user_view", key = "#p0", unless = "#result.isEmpty()")
    List<ShareUser> selectViewShareUsers(int viewId);
    @Cacheable(cacheNames = "share_user_watchlist", key = "#p0", unless = "#result.isEmpty()")
    List<ShareUser> selectWatchListShareUsers(int watchListId);

    @Cacheable(cacheNames = "user_by_username", key = "#p0", unless = "#result == null")
    User getUser(String username);

    @Cacheable(cacheNames = "user_by_id", key = "#p0", unless = "#result == null")
    User getUser(int id);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#user.id"),
                    @CacheEvict(cacheNames = "user_by_username", key = "#user.username"),
                    @CacheEvict(cacheNames = "share_user_datasource", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_view", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_watchlist", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_datapoint", allEntries = true)
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

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#p0"),
                    @CacheEvict(cacheNames = "user_by_username", allEntries = true)
            }
    )
    void updateLogin(int userId);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", key = "#p0"),
                    @CacheEvict(cacheNames = "user_by_username", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_datasource", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_view", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_watchlist", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_datapoint", allEntries = true)
            }
    )
    void delete(int userId);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_id", allEntries = true),
                    @CacheEvict(cacheNames = "user_by_username", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_datasource", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_view", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_watchlist", allEntries = true),
                    @CacheEvict(cacheNames = "share_user_datapoint", allEntries = true)
            }
    )
    int insert(User user);

    @Caching(evict = {
            @CacheEvict(cacheNames = "share_user_datasource", allEntries = true)
    })
    default void resetDataSourcePermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "share_user_datapoint", allEntries = true)
    })
    default void resetDataPointPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "share_user_view", allEntries = true)
    })
    default void resetViewPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "share_user_watchlist", allEntries = true)
    })
    default void resetWatchListPermissions() {}
}
