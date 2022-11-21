package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.UserDAO;

import java.util.List;

public class UserCache implements UserCachable {

    private final UserDAO userDAO;

    public UserCache(UserDAO userDAO) {
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
    public void updateHideMenu(User user) {
        userDAO.updateHideMenu(user);
    }

    @Override
    public void updateScadaTheme(User user) {
        userDAO.updateScadaTheme(user);
    }

    @Override
    public void delete(int userId) {
        userDAO.delete(userId);
    }
}
