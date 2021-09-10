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

    @Cacheable(cacheNames = "users_list", key="'userIds'", unless = "#result.isEmpty()")
    List<Integer> getAll();
    @Cacheable(cacheNames = "users_list", key="'users'", unless = "#result.isEmpty()")
    List<User> getUsers();
    @Cacheable(cacheNames = "users_list", key="'activeUsers'", unless = "#result.isEmpty()")
    List<User> getActiveUsers();

    List<ShareUser> selectDataSourceShareUsers(int dataSourceId);
    List<ShareUser> selectViewShareUsers(int viewId);
    List<ShareUser> selectWatchListShareUsers(int watchListId);
    List<ShareUser> selectDataPointShareUsers(int dataPointId);

    @Cacheable(cacheNames = "users_by_username", unless = "#result == null")
    User getUser(String username);

    @Cacheable(cacheNames = "users", unless = "#result == null")
    User getUser(int id);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", key = "#user.id"),
                    @CacheEvict(cacheNames = "users_by_username", key = "#user.username")
            }
    )
    void update(User user);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", key = "#user.id"),
                    @CacheEvict(cacheNames = "users_by_username", key = "#user.username")
            }
    )
    void updateHideMenu(User user);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", key = "#user.id"),
                    @CacheEvict(cacheNames = "users_by_username", key = "#user.username")
            }
    )
    void updateScadaTheme(User user);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", key = "#userId"),
                    @CacheEvict(cacheNames = "users_by_username", allEntries = true)
            }
    )
    void updateHomeUrl(int userId, String homeUrl);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", key = "#userId"),
                    @CacheEvict(cacheNames = "users_by_username", allEntries = true)
            }
    )
    void updateLogin(int userId);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", key = "#userId"),
                    @CacheEvict(cacheNames = "users_by_username", allEntries = true)
            }
    )
    void delete(int userId);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "users_list", allEntries = true),
                    @CacheEvict(cacheNames = "users", allEntries = true),
                    @CacheEvict(cacheNames = "users_by_username", allEntries = true)
            }
    )
    int insert(User user);
}
