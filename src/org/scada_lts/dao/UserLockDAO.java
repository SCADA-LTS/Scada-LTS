package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.userLock.UserLock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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

    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_LOCK_TYPE = "lockType";
    private static final String COLUMN_TYPE_KEY = "typeKey";
    private static final String COLUMN_TIMESTAMP = "ts";

    private static final String INSERT_USER_LOCK = "";

    private static final String SELECT_USER_LOCK = "";

    private static final String DELETE_USER_LOCK = "";

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
        DAO.getInstance().getJdbcTemp().update(INSERT_USER_LOCK, new UserLockRowMapper());
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

    public void deleteUserLock(UserLock userLock) {
        DAO.getInstance().getJdbcTemp().query(DELETE_USER_LOCK, new UserLockRowMapper());
    }

}
