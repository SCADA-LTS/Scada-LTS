package org.scada_lts.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.service.model.UserRole;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Collections;
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

    private JdbcTemplate jdbcTemplate;

    public UserRoleDAO() {
        this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
    }

    public UserRoleDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserRole> getUserRoles(final int userId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("getUserRoles(final int userId) userId:" + userId);
        }
        try {
            return jdbcTemplate.query(USER_ROLE_SELECT_WHERE_USERID, new Object[]{userId}, new UserRoleDAO.UserRoleRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public int insert(final int userId, final String role) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("insert(final int userId, final String role) userId:" + userId + ", role:" + role);
        }
        try {
            return jdbcTemplate.update(USER_ROLE_INSERT, userId, role);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int update(final String role, int userId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("update(final String role, int userId) role:" + role + ", userId:" + userId);
        }
        try {
            return jdbcTemplate.update(USER_ROLE_UPDATE, role, userId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int deleteUserRole(int userId, String role) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("delete(int userId) userId:" + userId + ", role:" + role);
        }
        try {
            return jdbcTemplate.update(USER_ROLE_DELETE, userId, role);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int deleteUserRoleUser(int userId) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("delete(int userId) userId:" + userId);
        }
        try {
            return jdbcTemplate.update(USER_ROLE_USER_DELETE, userId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    private class UserRoleRowMapper implements RowMapper<UserRole> {

        @Override
        public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserRole userRole = new UserRole();
            userRole.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
            userRole.setRole(rs.getString(COLUMN_NAME_ROLE));
            return userRole;
        }
    }
}
