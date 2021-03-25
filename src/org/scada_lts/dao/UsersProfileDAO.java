package org.scada_lts.dao;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UsersProfileDAO {

    private static final Log LOG = LogFactory.getLog(UsersProfileDAO.class);

    private static final String COLUMN_NAME_USER_ID = "userId";
    private static final String COLUMN_NAME_USER_PROFILE_ID = "userProfileId";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";


    private static final String USERS_USERS_PROFILES_SELECT_BY_USER_ID = "" +
            "select " +
            "up." + COLUMN_NAME_ID + ", " +
            "up." + COLUMN_NAME_XID + ", " +
            "up." + COLUMN_NAME_NAME + " " +
            "from usersUsersProfiles uup left join usersProfiles up on " +
            "up." + COLUMN_NAME_ID + "=" +
            "uup." + COLUMN_NAME_USER_PROFILE_ID + " " +
            "where uup." + COLUMN_NAME_USER_ID + "=? " +
            "order by " +
            "up." + COLUMN_NAME_NAME;

    private static final String USERS_PROFILE_SELECT_BY_ID = "" +
            "select " +
            "up." + COLUMN_NAME_ID + ", " +
            "up." + COLUMN_NAME_XID + ", " +
            "up." + COLUMN_NAME_NAME + " " +
            "from usersProfiles up " +
            "where up." + COLUMN_NAME_USER_PROFILE_ID + "=? ";

    private static final String USERS_USERS_PROFILE_DELETE_BY_USER_ID = "" +
            "delete " +
            "from usersUsersProfiles " +
            "where " + COLUMN_NAME_USER_ID + "=?";

    private static final String USERS_PROFILE_DELETE_BY_ID = "" +
            "delete " +
            "from usersProfiles " +
            "where " + COLUMN_NAME_ID + "=?";

    private static final String USERS_PROFILES_UPDATE_NAME_BY_ID = "" +
            "update " +
            "usersProfiles up " +
            "set " +
            COLUMN_NAME_NAME + "=? " +
            "where " +
            COLUMN_NAME_ID + "=?";

    private static final String USERS_SELECT_BY_USERS_PROFILE_ID = "" +
            "select " +
            COLUMN_NAME_USER_ID + " " +
            "from " +
            "usersUsersProfiles " +
            "where " +
            COLUMN_NAME_USER_PROFILE_ID + "=?";

    private static final String USERS_USERS_PROFILE_INSERT = "" +
            "insert into usersUsersProfiles(" +
            COLUMN_NAME_USER_PROFILE_ID + ", " +
            COLUMN_NAME_USER_ID +") " +
            "values (?,?)";

    private static final String USERS_PROFILE_SELECT_ORDER_BY_NAME_LIMIT_OFFSET = "" +
            "select " +
            "u." + COLUMN_NAME_ID + ", " +
            "u." + COLUMN_NAME_NAME + ", " +
            "u." + COLUMN_NAME_XID + " " +
            "from usersProfiles u order by " +
            "u." + COLUMN_NAME_NAME + " " +
            "limit ? offset ?";

    private static final String USERS_PROFILE_INSERT = "" +
            "insert into usersProfiles (" +
            COLUMN_NAME_XID + ", " +
            COLUMN_NAME_NAME + "" +
            ") values (?, ?)";


    private JdbcTemplate jdbcTemplate;

    public UsersProfileDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UsersProfileVO> selectUserProfileByUserId(int userId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfileByUserId(int userId) userId:" + userId);
        try {
            return jdbcTemplate.query(USERS_USERS_PROFILES_SELECT_BY_USER_ID, new Object[]{userId}, new UsersProfileRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public Optional<UsersProfileVO> selectProfileById(int usersProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfileById(int usersProfileId) id:" + usersProfileId);
        try {
            UsersProfileVO usersProfile = jdbcTemplate.queryForObject(USERS_PROFILE_SELECT_BY_ID, new Object[]{usersProfileId}, new UsersProfileRowMapper());
            return Optional.ofNullable(usersProfile);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public List<UsersProfileVO> selectProfiles(int offset, int limit) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfiles(int offset, int limit) limit:" + limit + " offset:" + offset);
        try {
            return jdbcTemplate.query(USERS_PROFILE_SELECT_ORDER_BY_NAME_LIMIT_OFFSET, new Object[]{limit, offset}, new UsersProfileRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public List<Integer> selectUsersByProfileId(int usersProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectUsersByProfileId(int usersProfileId) id:" + usersProfileId);
        try {
            return jdbcTemplate.query(USERS_SELECT_BY_USERS_PROFILE_ID, new Object[]{usersProfileId}, (rs, rowNum) ->
                    rs.getInt(COLUMN_NAME_USER_ID));
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public int updateProfileName(String name, int usersProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("updateProfileName(String name, int usersProfileId) id:" + usersProfileId + " name: " +  name);
        try {
            return jdbcTemplate.update(USERS_PROFILES_UPDATE_NAME_BY_ID, name, usersProfileId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int deleteUserProfileByUserId(int userId) {
        if (LOG.isTraceEnabled())
            LOG.trace("deleteUserProfileByUserId(int userId) userId:" + userId);
        try {
            return jdbcTemplate.update(USERS_USERS_PROFILE_DELETE_BY_USER_ID, userId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int deleteProfile(int profileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("deleteProfile(int profileId) profileId:" + profileId);
        try {
            return jdbcTemplate.update(USERS_PROFILE_DELETE_BY_ID, profileId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int insertUserProfile(int userId, int userProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("insertUserProfile(int userId, int userProfileId) userId:" + userId + ", userProfileId:" + userProfileId);
        try {
            return jdbcTemplate.update(USERS_USERS_PROFILE_INSERT, userProfileId, userId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public int insertProfile(String usersProfileXid, String usersProfileName) {
        if (LOG.isTraceEnabled())
            LOG.trace("insertProfile(String usersProfileXid, String usersProfileName) usersProfileXid:" + usersProfileXid + ", usersProfileName:" + usersProfileName);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update((Connection connection) -> {
                PreparedStatement ps = connection
                        .prepareStatement(USERS_PROFILE_INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, usersProfileXid);
                ps.setString(2, usersProfileName);
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (EmptyResultDataAccessException ex) {
            return -1;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    public void updateViewUsersProfiles(UsersProfileVO usersProfile) {
        try {
            _updateViewUsersProfiles(usersProfile);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void updateWatchListUsersProfiles(UsersProfileVO usersProfile) {
        try {
            _updateWatchListUsersProfiles(usersProfile);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void updateDataPointUsersProfiles(UsersProfileVO usersProfile) {
        try {
            _updateDataPointUsersProfiles(usersProfile);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void updateDataSourceUsersProfiles(UsersProfileVO usersProfile) {
        try {
            _updateDataSourceUsersProfiles(usersProfile);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void _updateViewUsersProfiles(UsersProfileVO usersProfile) throws SQLException {
        jdbcTemplate.update("delete from viewUsersProfiles where userProfileId=?",
                new Object[] { usersProfile.getId() });
        jdbcTemplate.batchUpdate(
                "insert into viewUsersProfiles (viewId, userProfileId, permission) values (?,?,?)",
                new BatchPreparedStatementSetter() {
                    public int getBatchSize() {
                        return usersProfile.getViewPermissions().size();
                    }

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setInt(1, usersProfile.getViewPermissions().get(i)
                                .getId());
                        ps.setInt(2, usersProfile.getId());
                        ps.setInt(3, usersProfile.getViewPermissions().get(i)
                                .getPermission());
                    }
                });
    }

    private void _updateWatchListUsersProfiles(UsersProfileVO usersProfile) throws SQLException {
        jdbcTemplate.update("delete from watchListUsersProfiles where userProfileId=?",
                new Object[] { usersProfile.getId() });
        jdbcTemplate.batchUpdate(
                "insert into watchListUsersProfiles (watchlistId, userProfileId, permission) values (?,?,?)",
                new BatchPreparedStatementSetter() {
                    public int getBatchSize() {
                        return usersProfile.getWatchlistPermissions().size();
                    }

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setInt(1, usersProfile.getWatchlistPermissions()
                                .get(i).getId());
                        ps.setInt(2, usersProfile.getId());
                        ps.setInt(3, usersProfile.getWatchlistPermissions()
                                .get(i).getPermission());
                    }
                });
    }

    private void _updateDataPointUsersProfiles(UsersProfileVO usersProfile) throws SQLException {
        jdbcTemplate.update("delete from dataPointUsersProfiles where userProfileId=?",
                new Object[] { usersProfile.getId() });
        jdbcTemplate.batchUpdate(
                "insert into dataPointUsersProfiles (dataPointId, userProfileId, permission) values (?,?,?)",
                new BatchPreparedStatementSetter() {
                    public int getBatchSize() {
                        return usersProfile.getDataPointPermissions().size();
                    }

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setInt(1, usersProfile.getDataPointPermissions()
                                .get(i).getDataPointId());
                        ps.setInt(2, usersProfile.getId());
                        ps.setInt(3, usersProfile.getDataPointPermissions()
                                .get(i).getPermission());
                    }
                });
    }

    private void _updateDataSourceUsersProfiles(UsersProfileVO usersProfile) throws SQLException {
        jdbcTemplate.update("delete from dataSourceUsersProfiles where userProfileId=?",
                new Object[] { usersProfile.getId() });
        jdbcTemplate.batchUpdate(
                "insert into dataSourceUsersProfiles (dataSourceId, userProfileId) values (?,?)",
                new BatchPreparedStatementSetter() {
                    public int getBatchSize() {
                        return usersProfile.getDataSourcePermissions().size();
                    }

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setInt(1, usersProfile.getDataSourcePermissions()
                                .get(i));
                        ps.setInt(2, usersProfile.getId());
                    }
                });
    }

    private class UsersProfileRowMapper implements RowMapper<UsersProfileVO> {

        @Override
        public UsersProfileVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            UsersProfileVO profile = new UsersProfileVO();
            profile.setId(rs.getInt(COLUMN_NAME_ID));
            profile.setXid(rs.getString(COLUMN_NAME_XID));
            profile.setName(rs.getString(COLUMN_NAME_NAME));
            return profile;
        }
    }
}
