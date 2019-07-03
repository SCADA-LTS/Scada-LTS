package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

/**
 * @autor grzegorz.bylica@gmail.com on 03.07.19
 */
public interface IUserDAO {
    List<Integer> getAll();

    User getUser(int id);

    User getUser(String username);

    List<User> getUsers();

    List<User> getActiveUsers();

    void updateHomeUrl(int userId, String homeUrl);

    void updateLogin(int userId);

    String getUserTimezone(int id);

    String getUserZone(int id)                                                                                    ///smart e-tech //time_zone
    ;

    User getUserByMail(String email);

    void updateUserTimezone(int userId, String timezone);

    void updateUserZone(int userId, String zone);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(User user);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(User user);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int userId);
}
