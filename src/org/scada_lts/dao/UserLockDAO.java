package org.scada_lts.dao;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.userLock.UserLock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Repository
public class UserLockDAO {

    private Log LOG = LogFactory.getLog(UserLockDAO.class);

    private static final String USERS_LOCKS_TABLE = "userLocks";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_LOCK_TYPE = "lockType";
    private static final String COLUMN_TYPE_KEY = "typeKey";
    private static final String COLUMN_TIMESTAMP = "ts";

    private static final String INSERT_USER_LOCK = "INSERT INTO "
            + USERS_LOCKS_TABLE + " ("
            + COLUMN_USER_ID + ", "
            + COLUMN_LOCK_TYPE + ", "
            + COLUMN_TYPE_KEY + ", "
            + COLUMN_TIMESTAMP + ") "
            + "VALUES (?, ?, ?, ?);";

    private static final String SELECT_USER_LOCK = "SELECT * FROM "
            + USERS_LOCKS_TABLE + " "
            + "WHERE "
            + COLUMN_LOCK_TYPE + " = ? AND "
            + COLUMN_TYPE_KEY + " = ?;";

    private static final String DELETE_USER_LOCK = "DELETE FROM "
            + USERS_LOCKS_TABLE
            + " WHERE "
            + COLUMN_LOCK_TYPE + " = ? AND "
            + COLUMN_TYPE_KEY + " = ?;";

    private class UserLockRowMapper implements RowMapper<UserLock> {

        @Override
        public UserLock mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserLock userLock = new UserLock();
            userLock.setUserId(rs.getInt(COLUMN_USER_ID));
            userLock.setLockType(rs.getShort(COLUMN_LOCK_TYPE));
            userLock.setTypeKey(rs.getLong(COLUMN_TYPE_KEY));
            userLock.setTimestamp(rs.getLong(COLUMN_TIMESTAMP));
            return userLock;
        }
    }

    public void insertUserLock(UserLock userLock) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("insert('..')");
        }

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_USER_LOCK, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        userLock.getUserId(),
                        userLock.getLockType(),
                        userLock.getTypeKey(),
                        userLock.getTimestamp()
                }).setValues(ps);
                return ps;
            }
        });
    }

    public UserLock selectUserLock(short lockType, long lockKey) throws EmptyResultDataAccessException {
        UserLock userLock;
        try {
            userLock = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_USER_LOCK, new Object[]{lockType, lockKey}, new UserLockRowMapper());
        } catch (EmptyResultDataAccessException e) {
            userLock = null;
        }
        return userLock;
    }

    public void deleteUserLock(short lockType, long lockKey) {
        DAO.getInstance().getJdbcTemp().update(DELETE_USER_LOCK, new Object[]{lockType, lockKey});
    }

}
