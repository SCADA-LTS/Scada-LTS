package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IUserDAO;

import java.util.List;
import java.util.stream.Collectors;

public class UserDaoWithCache implements IUserDAO {

    private final UserCachable userCache;

    public UserDaoWithCache(UserCachable userCache) {
        this.userCache = userCache;
    }

    @Override
    public List<Integer> getAll() {
        return getUsers().stream().map(a -> a.getId()).collect(Collectors.toList());
    }

    @Override
    public List<User> getUsers() {
        return userCache.getUsers();
    }

    @Override
    public List<User> getActiveUsers() {
        return getUsers().stream().filter(a -> !a.isDisabled()).collect(Collectors.toList());
    }

    @Override
    public User getUser(String username) {
        return getUsers().stream().filter(a -> a.getUsername().equals(username)).findAny().map(User::new).orElse(null);
    }

    @Override
    public User getUser(int id) {
        return getUsers().stream().filter(a -> a.getId() == id).findAny().map(User::new).orElse(null);
    }

    @Override
    public void update(User user) {
        userCache.update(user);
    }

    @Override
    public void updateHomeUrl(int userId, String homeUrl) {
        userCache.updateHomeUrl(userId, homeUrl);
    }

    @Override
    public void updateLogin(int userId) {
        userCache.updateLogin(userId);
    }

    @Override
    public void delete(int userId) {
        userCache.delete(userId);
    }

    @Override
    public int insert(User user) {
        return userCache.insert(user);
    }

    @Override
    public void updateUserPassword(int userId, String newPassword) {
        userCache.updateUserPassword(userId, newPassword);
    }

    @Override
    public void updateUserLang(int userId, String lang) {
        userCache.updateLang(userId, lang);
    }
}
