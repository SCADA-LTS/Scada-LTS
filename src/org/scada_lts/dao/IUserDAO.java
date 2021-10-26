package org.scada_lts.dao;

import com.serotonin.mango.vo.User;

import java.util.List;

public interface IUserDAO {

    List<Integer> getAll();
    List<User> getUsers();
    List<User> getActiveUsers();
    User getUser(String username);
    User getUser(int id);
    void update(User user);
    @Deprecated
    void updateHideMenu(User user);
    @Deprecated
    void updateScadaTheme(User user);
    void updateHomeUrl(int userId, String homeUrl);
    void updateUserPassword(int userId, String newPassword);
    void updateLogin(int userId);
    void delete(int userId);
    int insert(User user);
}
