package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface UserCachable {

    String CACHE_ENABLED_KEY = "user.cache.enabled";

    @Cacheable(cacheNames = "user_list", key="'users'")
    List<User> getUsers();

    @Caching(evict = {@CacheEvict(cacheNames = "user_list", allEntries = true)})
    void update(User user);

    @Caching(evict = {@CacheEvict(cacheNames = "user_list", allEntries = true)})
    void updateHomeUrl(int userId, String homeUrl);

    @Caching(evict = {@CacheEvict(cacheNames = "user_list", allEntries = true)})
    void updateUserPassword(int userId, String newPassword);
    void updateLogin(int userId);
    @Caching(evict = {@CacheEvict(cacheNames = "user_list", allEntries = true)})
    void updateLang(int userId, String lang);

    @Caching (
            evict = {
                    @CacheEvict(cacheNames = "user_list", allEntries = true),
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
