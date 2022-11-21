package org.scada_lts.permissions.migration.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.UsersProfileDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class OnlyMigrationUsersProfileDAO extends UsersProfileDAO {

    private static final Log LOG = LogFactory.getLog(OnlyMigrationUsersProfileDAO.class);

    private static final String COLUMN_NAME_USER_ID = "userId";
    private static final String COLUMN_NAME_USER_PROFILE_ID = "userProfileId";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";

    private static final String COLUMN_NAME_PERMISSION = "permission";
    private static final String COLUMN_NAME_DATA_POINT_ID = "dataPointId";
    private static final String COLUMN_NAME_DATA_SOURCE_ID = "dataSourceId";
    private static final String COLUMN_NAME_VIEW_ID = "viewId";
    private static final String COLUMN_NAME_WATCH_LIST_ID = "watchListId";

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

    private static final String SHARE_USERS_BY_USERS_PROFILE_AND_WATCHLIST_ID = "" +
            "select " +
                "uup." + COLUMN_NAME_USER_ID + ", " +
                "wlup." + COLUMN_NAME_PERMISSION + " " +
            "from " +
                "usersUsersProfiles uup " +
            "left join " +
                "watchListUsersProfiles wlup " +
            "on " +
                "wlup." + COLUMN_NAME_USER_PROFILE_ID + "=uup." + COLUMN_NAME_USER_PROFILE_ID + " " +
            "where " +
                "wlup." + COLUMN_NAME_WATCH_LIST_ID + "=?;";

    private static final String SHARE_USERS_BY_USERS_PROFILE_AND_VIEW_ID = "" +
            "select " +
                "uup." + COLUMN_NAME_USER_ID + ", " +
                "vup." + COLUMN_NAME_PERMISSION + " " +
            "from " +
                "usersUsersProfiles uup " +
            "left join " +
                "viewUsersProfiles vup " +
            "on " +
                "vup." + COLUMN_NAME_USER_PROFILE_ID + "=uup." + COLUMN_NAME_USER_PROFILE_ID + " " +
            "where " +
                "vup." + COLUMN_NAME_VIEW_ID + "=?;";

    private static final String SHARE_USERS_BY_USERS_PROFILE_AND_DATA_POINT_ID = "" +
            "select " +
                "uup." + COLUMN_NAME_USER_ID + ", " +
                "dpup." + COLUMN_NAME_PERMISSION + " " +
            "from " +
                "usersUsersProfiles uup " +
            "left join " +
                "dataPointUsersProfiles dpup " +
            "on " +
                "dpup." + COLUMN_NAME_USER_PROFILE_ID + "=uup." + COLUMN_NAME_USER_PROFILE_ID + " " +
            "where " +
                "dpup." + COLUMN_NAME_DATA_POINT_ID + "=?;";

    private static final String SHARE_USERS_BY_USERS_PROFILE_AND_DATA_SOURCE_ID = "" +
            "select " +
                "uup." + COLUMN_NAME_USER_ID + " " +
            "from " +
                "dataSourceUsersProfiles dsup " +
            "left join " +
                "usersUsersProfiles uup " +
            "on " +
                "dsup." + COLUMN_NAME_USER_PROFILE_ID + "=uup." + COLUMN_NAME_USER_PROFILE_ID + " " +
            "where " +
                "dsup." + COLUMN_NAME_DATA_SOURCE_ID + "=?;";

    private static final String DATA_POINT_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE=""
            +"insert dataPointUsersProfiles ("
            +COLUMN_NAME_DATA_POINT_ID+","
            +COLUMN_NAME_USER_PROFILE_ID+","
            +COLUMN_NAME_PERMISSION+")"
            + " values (?,?,?) ON DUPLICATE KEY UPDATE " +
            COLUMN_NAME_PERMISSION + "=?";

    private static final String DATA_SOURCE_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE=""
            +"insert dataSourceUsersProfiles ("
            +COLUMN_NAME_DATA_SOURCE_ID+","
            +COLUMN_NAME_USER_PROFILE_ID+""+")"
            + " values (?,?) ON DUPLICATE KEY UPDATE " +
            COLUMN_NAME_USER_PROFILE_ID + "=?";

    private static final String VIEW_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE=""
            +"insert viewUsersProfiles ("
            +COLUMN_NAME_VIEW_ID+","
            +COLUMN_NAME_USER_PROFILE_ID+","
            +COLUMN_NAME_PERMISSION+")"
            + " values (?,?,?) ON DUPLICATE KEY UPDATE " +
            COLUMN_NAME_PERMISSION + "=?";

    private static final String WATCH_LIST_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE=""
            +"insert watchListUsersProfiles ("
            +COLUMN_NAME_WATCH_LIST_ID+","
            +COLUMN_NAME_USER_PROFILE_ID+","
            +COLUMN_NAME_PERMISSION+")"
            + " values (?,?,?) ON DUPLICATE KEY UPDATE " +
            COLUMN_NAME_PERMISSION + "=?";

    private static final String DATA_POINT_USERS_PROFILE_DELETE_DATA_POINT_ID_AND_USER_PROFILE_ID = ""
            +"delete "
            + "from "
            + "dataPointUsersProfiles "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=? "
            + "and "
            + COLUMN_NAME_USER_PROFILE_ID+"=?";

    private static final String DATA_SOURCE_USERS_PROFILE_DELETE_DATA_SOURCE_ID_AND_USER_PROFILE_ID = ""
            +"delete "
            + "from "
            + "dataSourceUsersProfiles "
            + "where "
            + COLUMN_NAME_DATA_SOURCE_ID+"=? "
            + "and "
            + COLUMN_NAME_USER_PROFILE_ID+"=?";

    private static final String VIEW_USERS_PROFILE_DELETE_VIEW_ID_AND_USER_PROFILE_ID = ""
            +"delete "
            + "from "
            + "viewUsersProfiles "
            + "where "
            + COLUMN_NAME_VIEW_ID+"=? "
            + "and "
            + COLUMN_NAME_USER_PROFILE_ID+"=?";

    private static final String WATCH_LIST_USERS_PROFILE_DELETE_WATCH_LIST_ID_AND_USER_PROFILE_ID = ""
            +"delete "
            + "from "
            + "watchListUsersProfiles "
            + "where "
            + COLUMN_NAME_WATCH_LIST_ID+"=? "
            + "and "
            + COLUMN_NAME_USER_PROFILE_ID+"=?";

    private JdbcTemplate jdbcTemplate;

    public OnlyMigrationUsersProfileDAO() {
        this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
    }

    public OnlyMigrationUsersProfileDAO(JdbcTemplate jdbcTemplate) {
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

    public int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert) profileId:" + profileId + "");
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER  };

        List<Object[]> batchArgs = toInsert.stream()
                .map(a -> new Object[] {a.getDataPointId(), profileId, a.getPermission(), a.getPermission()})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(DATA_POINT_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE, batchArgs, argTypes);
    }

    public int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete) profileId:" + profileId);
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER};

        List<Object[]> batchArgs = toDelete.stream()
                .map(a -> new Object[] {a.getDataPointId(), profileId})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(DATA_POINT_USERS_PROFILE_DELETE_DATA_POINT_ID_AND_USER_PROFILE_ID, batchArgs, argTypes);
    }

    public int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("insertDataSourceUsersProfile(int profileId, List<Integer> toInsert) profileId:" + profileId + "");
        }

        int[] argTypes = { Types.INTEGER, Types.INTEGER, Types.INTEGER };

        List<Object[]> batchArgs = toInsert.stream()
                .map(a -> new Object[] {a, profileId, a})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(DATA_SOURCE_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE, batchArgs, argTypes);
    }

    public int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete) profileId:" + profileId);
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER };

        List<Object[]> batchArgs = toDelete.stream()
                .map(a -> new Object[] {a, profileId})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(DATA_SOURCE_USERS_PROFILE_DELETE_DATA_SOURCE_ID_AND_USER_PROFILE_ID, batchArgs, argTypes);
    }

    public int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("insertViewUsersProfile(int profileId, List<DataPointAccess> toInsert) profileId:" + profileId + "");
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER  };

        List<Object[]> batchArgs = toInsert.stream()
                .map(a -> new Object[] {a.getId(), profileId, a.getPermission(), a.getPermission()})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(VIEW_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE, batchArgs, argTypes);
    }

    public int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("deleteViewUsersProfile(int profileId, List<Integer> toDelete) profileId:" + profileId);
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER };

        List<Object[]> batchArgs = toDelete.stream()
                .map(a -> new Object[] {a.getId(), profileId})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(VIEW_USERS_PROFILE_DELETE_VIEW_ID_AND_USER_PROFILE_ID, batchArgs, argTypes);
    }

    public int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("insertViewUsersProfile(int profileId, List<DataPointAccess> toInsert) profileId:" + profileId + "");
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER  };

        List<Object[]> batchArgs = toInsert.stream()
                .map(a -> new Object[] {a.getId(), profileId, a.getPermission(), a.getPermission()})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(WATCH_LIST_USERS_PROFILE_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE, batchArgs, argTypes);
    }

    public int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("deleteWatchListUsersProfile(int profileId, List<Integer> toDelete) profileId:" + profileId);
        }

        int[] argTypes = {Types.INTEGER, Types.INTEGER };

        List<Object[]> batchArgs = toDelete.stream()
                .map(a -> new Object[] {a.getId(), profileId})
                .collect(Collectors.toList());

        return DAO.getInstance().getJdbcTemp()
                .batchUpdate(WATCH_LIST_USERS_PROFILE_DELETE_WATCH_LIST_ID_AND_USER_PROFILE_ID, batchArgs, argTypes);
    }

    public List<ShareUser> selectDataSourceShareUsers(int dataSourceId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectDataSourceShareUsers(int dataSourceId) dataSourceId:" + dataSourceId);
        try {
            return jdbcTemplate.query(SHARE_USERS_BY_USERS_PROFILE_AND_DATA_SOURCE_ID,
                    new Object[]{dataSourceId},
                    new ShareUserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public List<ShareUser> selectViewShareUsers(int viewId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectViewShareUsers(int viewId) viewId:" + viewId);
        try {
            return jdbcTemplate.query(SHARE_USERS_BY_USERS_PROFILE_AND_VIEW_ID,
                    new Object[]{viewId},
                    new ShareUserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public List<ShareUser> selectWatchListShareUsers(int watchListId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectViewShareUsers(int watchListId) watchListId:" + watchListId);
        try {
            return jdbcTemplate.query(SHARE_USERS_BY_USERS_PROFILE_AND_WATCHLIST_ID,
                    new Object[]{watchListId},
                    new ShareUserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public List<ShareUser> selectDataPointShareUsers(int dataPointId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectDataPointShareUsers(int dataPointId) dataPointId:" + dataPointId);
        try {
            return jdbcTemplate.query(SHARE_USERS_BY_USERS_PROFILE_AND_DATA_POINT_ID,
                    new Object[]{dataPointId},
                    new ShareUserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
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

    private class ShareUserRowMapper implements RowMapper<ShareUser> {

        @Override
        public ShareUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShareUser profile = new ShareUser();
            profile.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
            profile.setAccessType(rs.getInt(COLUMN_NAME_PERMISSION));
            return profile;
        }
    }
}
