package org.scada_lts.dao;

import com.serotonin.util.Tuple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

public class UserRoleDAO {

    private static final Log LOG = LogFactory.getLog(UserRoleDAO.class);

    private static final String COLUMN_NAME_USER_ID = "userId";
    private static final String COLUMN_NAME_ROLE = "role";

    private static final String USER_ROLE_SELECT_WHERE_USERID = ""
            + "select "
            + COLUMN_NAME_USER_ID + ", "
            + COLUMN_NAME_ROLE + " "
            + "from userRoles where "
            + COLUMN_NAME_USER_ID + "=? ";

    private static final String USER_ROLE_INSERT = ""
            + "insert into userRoles ("
            + COLUMN_NAME_USER_ID + ", "
            + COLUMN_NAME_ROLE + ") "
            + "values (?,?) ";

    private static final String USER_ROLE_UPDATE = ""
            + "update userRoles set "
            + COLUMN_NAME_ROLE + "=? "
            + "where "
            + COLUMN_NAME_USER_ID + "=?";

    private static final String USER_ROLE_DELETE = ""
            + "delete from userRoles where "
            + COLUMN_NAME_USER_ID + "=? AND "
            + COLUMN_NAME_ROLE + "=?";

    private static final String USER_ROLE_USER_DELETE = ""
            + "delete from userRoles where "
            + COLUMN_NAME_USER_ID + "=? ";

    public List<Tuple<Integer, String>> getUserRoles(final int userId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("getUserRoles(final int userId) userId:" + userId);
        }

        return DAO.getInstance().getJdbcTemp().query(USER_ROLE_SELECT_WHERE_USERID, new Object[]{userId}, new RowMapper<Tuple<Integer, String>>() {
            @Override
            public Tuple<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Tuple<Integer, String>(rs.getInt(COLUMN_NAME_USER_ID), rs.getString(COLUMN_NAME_ROLE));
            }
        });
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public void insert(final int userId, final String role) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("insert(final int userId, final String role) userId:" + userId + ", role:" + role);
        }

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(USER_ROLE_INSERT);
                new ArgumentPreparedStatementSetter(new Object[]{
                        userId,
                        role
                }).setValues(preparedStatement);
                return preparedStatement;
            }
        });
    }

    @Transactional(readOnly = false, propagation= Propagation.REQUIRES_NEW, isolation= Isolation.READ_COMMITTED, rollbackFor=SQLException.class)
    public void update(final String role, int userId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("update(final String role, int userId) role:" + role + ", userId:" + userId);
        }

        DAO.getInstance().getJdbcTemp().update(USER_ROLE_UPDATE, new Object[] {role, userId});
    }

    @Transactional(readOnly = false, propagation= Propagation.REQUIRES_NEW, isolation= Isolation.READ_COMMITTED, rollbackFor=SQLException.class)
    public int deleteUserRole(int userId, String role) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("delete(int userId) userId:" + userId + ", role:" + role);
        }

        return DAO.getInstance().getJdbcTemp().update(USER_ROLE_DELETE, new Object[] {userId, role});
    }

    @Transactional(readOnly = false, propagation= Propagation.REQUIRES_NEW, isolation= Isolation.READ_COMMITTED, rollbackFor=SQLException.class)
    public int deleteUserRoleUser(int userId) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("delete(int userId) userId:" + userId);
        }

        return DAO.getInstance().getJdbcTemp().update(USER_ROLE_USER_DELETE, new Object[] {userId});
    }
}
