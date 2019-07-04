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

import com.mysql.jdbc.Statement;
import org.apache.commons.logging.Log;
import com.serotonin.mango.util.Timezone;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @autor Khelifi Hassene, grzegorz.bylica@gmail.com (SoftQ) on 03.07.19
 */
public class UserTzDAO extends UserDAO implements IUserDAO  {

    private static final Log LOG = LogFactory.getLog(UserTzDAO.class);

    private final static String COLUMN_NAME_TIMEZONE="timezone";
    private final static String COLUMN_NAME_ZONE="zone";

    // @formatter:off

    private static final String USER_SELECT = ""
            + "select "
                + COLUMN_NAME_ID + ", "
                + COLUMN_NAME_USERNAME + ", "
                + COLUMN_NAME_PASSWORD + ", "
                + COLUMN_NAME_EMAIL + ", "
                + COLUMN_NAME_PHONE + ", "
                + COLUMN_NAME_ADMIN + ", "
                + COLUMN_NAME_DISABLED + ", "
                + COLUMN_NAME_SELECTED_WATCH_LIST + ", "
                + COLUMN_NAME_HOME_URL + ", "
                + COLUMN_NAME_LAST_LOGIN + ", "
                + COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", "
                + COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS +", "
                + COLUMN_NAME_TIMEZONE +", " // Offset
                + COLUMN_NAME_ZONE +" "// ZONE
            + "from users ";


    private static final String USER_INSERT = ""
            + "insert into users ("
                + COLUMN_NAME_USERNAME + ", "
                + COLUMN_NAME_PASSWORD + ", "
                + COLUMN_NAME_EMAIL + ", "
                + COLUMN_NAME_PHONE + ", "
                + COLUMN_NAME_ADMIN + ", "
                + COLUMN_NAME_DISABLED + ", "
                + COLUMN_NAME_HOME_URL + ", "
                + COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", "
                + COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + ", "
                + COLUMN_NAME_TIMEZONE +", "
                + COLUMN_NAME_ZONE + " )"
            + "values (?,?,?,?,?,?,?,?,?,?,?) ";

    private static final String USER_UPDATE = ""
            + "update users set "
                + COLUMN_NAME_USERNAME + "=?, "
                + COLUMN_NAME_PASSWORD + "=?, "
                + COLUMN_NAME_EMAIL + "=?, "
                + COLUMN_NAME_PHONE + "=?, "
                + COLUMN_NAME_ADMIN + "=?, "
                + COLUMN_NAME_DISABLED + "=?, "
                + COLUMN_NAME_HOME_URL + "=?, "
                + COLUMN_NAME_RECEIVE_ALARM_EMAILS + "=?, "
                + COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS +"=?, "
                + COLUMN_NAME_TIMEZONE + "=?, "
                + COLUMN_NAME_ZONE + "=? "
            + "where "
                + COLUMN_NAME_ID + "=? ";

    private static final String USER_SELECT_TIMEZONE = ""
            + "select "											// Timezone
                + COLUMN_NAME_TIMEZONE + " "
            + "from users "
            + "where "
                + COLUMN_NAME_ID + "=? ";

    private static final String USER_UPDATE_TIMEZONE = ""
            + "update users set "								// Timezone
                + COLUMN_NAME_TIMEZONE + "=? "
            + "where "
                + COLUMN_NAME_ID + "=? ";


    private static final String USER_SELECT_ZONE = ""
            + "select "											// Zone
                + COLUMN_NAME_ZONE + " "
            + "from users "
            + "where "
                + COLUMN_NAME_ID + "=? ";

    private static final String USER_UPDATE_ZONE=""
            + "update users set "								// Zone
                + COLUMN_NAME_ZONE + "=? "
            + "where "
                + COLUMN_NAME_ID + "=? ";

    private static final String USER_SELECT_WHERE_EMAIL = ""
            + USER_SELECT
            + "where lower("
                + COLUMN_NAME_EMAIL + ")=?";

    // @formatter:on

    private class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt(COLUMN_NAME_ID));
            user.setUsername(rs.getString(COLUMN_NAME_USERNAME));
            user.setPassword(rs.getString(COLUMN_NAME_PASSWORD));
            user.setEmail(rs.getString(COLUMN_NAME_EMAIL));
            user.setPhone(rs.getString(COLUMN_NAME_PHONE));
            user.setAdmin(DAO.charToBool(rs.getString(COLUMN_NAME_ADMIN)));
            user.setDisabled(DAO.charToBool(rs.getString(COLUMN_NAME_DISABLED)));
            user.setSelectedWatchList(rs.getInt(COLUMN_NAME_SELECTED_WATCH_LIST));
            user.setHomeUrl(rs.getString(COLUMN_NAME_HOME_URL));
            user.setLastLogin(rs.getLong(COLUMN_NAME_LAST_LOGIN));
            user.setReceiveAlarmEmails(rs.getInt(COLUMN_NAME_RECEIVE_ALARM_EMAILS));
            user.setReceiveOwnAuditEvents(DAO.charToBool(rs.getString(COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS)));
            user.setTimezone(Timezone.createTimezone(rs.getString(COLUMN_NAME_TIMEZONE))); //Timezone
            user.setZone(rs.getString(COLUMN_NAME_ZONE));
            return user;
        }
    }

    @Override
    public String getUserTimezone(int id) {

        String timeZone;

        if (LOG.isTraceEnabled()) {
            LOG.trace("getUser_timezone(int id) id:" + id);
        }
        try {
            timeZone = DAO.getInstance().getJdbcTemp().queryForObject(USER_SELECT_TIMEZONE, new Object[]{id}, String.class);

        } catch (EmptyResultDataAccessException e) {
            timeZone = null;
        }
        return timeZone;

    }

    @Override
    public String getUserZone(int id)                                                                                    ///smart e-tech //time_zone
    {
        if (LOG.isTraceEnabled())
            LOG.trace("getUser_timezone(int id) id:" + id);

        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(USER_SELECT_ZONE, new Object[]{id}, String.class);

        }catch (EmptyResultDataAccessException e) {
            return "";
        }

    }

    @Override
    public User getUserByMail(String email) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("getUserMail(String email) email:" + email);
        }

        User user;
        try {
            user = DAO.getInstance().getJdbcTemp().queryForObject(USER_SELECT_WHERE_EMAIL, new Object[]{email}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            user = null;
        }
        return user;
    }
    @Override
    public void updateUserTimezone(int userId, String timezone) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("updateUser_timezone(TimeZone timezone) userId:" + userId + ", timezone:" + timezone);
        }
        DAO.getInstance().getJdbcTemp().update(USER_UPDATE_TIMEZONE, new Object[]{timezone, userId});

    }

    @Override
    public void updateUserZone(int userId, String zone) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("updateUser_zone(TimeZone timezone) userId:" + userId + ", zone:" + zone);
        }
        DAO.getInstance().getJdbcTemp().update(USER_UPDATE_ZONE, new Object[]{zone, userId});

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public int insert(final User user) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("insert(User user) user:" + user.toString());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(USER_INSERT, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[]{
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getPhone(),
                        DAO.boolToChar(user.isAdmin()),
                        DAO.boolToChar(user.isDisabled()),
                        user.getHomeUrl(),
                        user.getReceiveAlarmEmails(),
                        DAO.boolToChar(user.isReceiveOwnAuditEvents()),
                        user.getTimezoneId(),
                        user.getZone()
                }).setValues(preparedStatement);
                return preparedStatement;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public void update(final User user) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("update(User user) user:" + user);
        }

        DAO.getInstance().getJdbcTemp().update(USER_UPDATE, new Object[]{
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                DAO.boolToChar(user.isAdmin()),
                DAO.boolToChar(user.isDisabled()),
                user.getHomeUrl(),
                user.getReceiveAlarmEmails(),
                DAO.boolToChar(user.isReceiveOwnAuditEvents()),
                user.getTimezoneId(),
                user.getZone(),
                user.getId()
        });
    }

}
