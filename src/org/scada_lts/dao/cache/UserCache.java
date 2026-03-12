package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IUserDAO;

import java.util.List;

public class UserCache implements UserCacheable {

    private final IUserDAO userDAO;

    public UserCache(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<User> getUsers() {
        return userDAO.getUsers();
    }

    @Override
    public void updateHomeUrl(int userId, String homeUrl) {
        userDAO.updateHomeUrl(userId, homeUrl);
    }

    @Override
    public void updateLogin(int userId) {
        userDAO.updateLogin(userId);
    }

    @Override
    public int insert(User user) {
        return userDAO.insert(user);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }

    @Override
    public void delete(int userId) {
        userDAO.delete(userId);
    }

    @Override
    public void updateUserPassword(int userId, String newPassword) {
        userDAO.updateUserPassword(userId, newPassword);
    }

    @Override
    public void updateLang(int userId, String lang) {
        userDAO.updateUserLang(userId, lang);
    }
}
