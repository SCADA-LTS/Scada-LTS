/*
 * (c) 2019 VROC.ai https://vroc.ai/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

/**
 * @autor Khelifi Hassene, grzegorz.bylica@gmail.com (SoftQ) on 03.07.19
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
