package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Component
public interface UserDaoCachable {

    @Cacheable(cacheNames = "userIds")
    List<Integer> getAll();
    @Cacheable(cacheNames = "usersById")
    User getUser(int id);
    @Cacheable(cacheNames = "usersByUsername")
    User getUser(String username);
    @Cacheable(cacheNames = "usersById")
    List<User> getUsers();
    @Cacheable(cacheNames = "activeUsers")
    List<User> getActiveUsers();

    @CachePut(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"})
    void update(User user);
    @CachePut(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"})
    void updateHideMenu(User user);
    @CachePut(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"})
    void updateScadaTheme(User user);

    @CacheEvict(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"}, allEntries = true)
    void updateHomeUrl(int userId, String homeUrl);
    @CacheEvict(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"}, allEntries = true)
    void updateLogin(int userId);
    @CacheEvict(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"}, allEntries = true)
    int insert(User user);
    @CacheEvict(cacheNames = {"activeUsers", "usersById", "usersByUsername", "userIds"}, allEntries = true)
    void delete(int userId);
}
