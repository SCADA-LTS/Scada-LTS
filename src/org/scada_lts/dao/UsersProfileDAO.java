package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierRowMapper;
import org.scada_lts.utils.XidUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UsersProfileDAO implements IUsersProfileDAO {

    private static final Log LOG = LogFactory.getLog(UsersProfileDAO.class);

    private static final String TABLE_NAME = "usersProfiles";

    private static final String TABLE_NAME_USERS_PROFILES = "usersProfiles";

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
            "from usersUsersProfiles uup left join " + TABLE_NAME_USERS_PROFILES + " up on " +
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
            "from " + TABLE_NAME_USERS_PROFILES + " up " +
            "where up." + COLUMN_NAME_ID + "=? ";

    private static final String USERS_PROFILE_SELECT_BY_XID = "" +
            "select " +
            "up." + COLUMN_NAME_ID + ", " +
            "up." + COLUMN_NAME_XID + ", " +
            "up." + COLUMN_NAME_NAME + " " +
            "from " + TABLE_NAME_USERS_PROFILES + " up " +
            "where up." + COLUMN_NAME_XID + "=? ";

    private static final String USERS_USERS_PROFILE_DELETE_BY_USER_ID = "" +
            "delete " +
            "from usersUsersProfiles " +
            "where " + COLUMN_NAME_USER_ID + "=?";

    private static final String USERS_PROFILE_DELETE_BY_ID = "" +
            "delete " +
            "from " + TABLE_NAME_USERS_PROFILES + " " +
            "where " + COLUMN_NAME_ID + "=?";

    private static final String USERS_PROFILES_UPDATE_NAME_BY_ID = "" +
            "update " +
            TABLE_NAME_USERS_PROFILES + " up " +
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
            "from " + TABLE_NAME_USERS_PROFILES + " u order by " +
            "u." + COLUMN_NAME_NAME + " " +
            "limit ? offset ?";

    private static final String USERS_PROFILE_INSERT = "" +
            "insert into " + TABLE_NAME_USERS_PROFILES + " (" +
            COLUMN_NAME_XID + ", " +
            COLUMN_NAME_NAME + "" +
            ") values (?, ?)";

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

    private static final String WATCHLIST_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID = ""
            + "select "
            + COLUMN_NAME_WATCH_LIST_ID+ ", "
            + COLUMN_NAME_PERMISSION + " "
            + "from "
            + "watchListUsersProfiles "
            + "where "
            + COLUMN_NAME_USER_PROFILE_ID+ "=?";

    private static final String VIEW_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID = ""
            + "select "
            + COLUMN_NAME_VIEW_ID+ ", "
            + COLUMN_NAME_PERMISSION + " "
            + "from "
            + "viewUsersProfiles "
            + "where "
            + COLUMN_NAME_USER_PROFILE_ID+ "=?";

    private static final String DATA_SOURCE_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID = ""
            + "select "
            + COLUMN_NAME_DATA_SOURCE_ID+ ", "
            + COLUMN_NAME_USER_PROFILE_ID + " "
            + "from "
            + "dataSourceUsersProfiles "
            + "where "
            + COLUMN_NAME_USER_PROFILE_ID+ "=?";

    private static final String DATA_POINT_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID = ""
            + "select "
            + COLUMN_NAME_DATA_POINT_ID+ ", "
            + COLUMN_NAME_USER_PROFILE_ID+ ", "
            + COLUMN_NAME_PERMISSION + " "
            + "from "
            + "dataPointUsersProfiles "
            + "where "
            + COLUMN_NAME_USER_PROFILE_ID+ "=?";


    public List<ScadaObjectIdentifier> getUserProfiles() {
        if (LOG.isTraceEnabled()) LOG.trace("getUserProfiles()");
        ScadaObjectIdentifierRowMapper mapper = ScadaObjectIdentifierRowMapper.withDefaultNames();
        return DAO.getInstance().getJdbcTemp()
                .query(mapper.selectScadaObjectIdFrom(TABLE_NAME), mapper);
    }

    @Override
    public List<UsersProfileVO> selectUserProfileByUserId(int userId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfileByUserId(int userId) userId:" + userId);
        try {
            return DAO.getInstance().getJdbcTemp().query(USERS_USERS_PROFILES_SELECT_BY_USER_ID, new Object[]{userId}, new UsersProfileRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<UsersProfileVO> selectProfileById(int usersProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfileById(int usersProfileId) id:" + usersProfileId);
        try {
            UsersProfileVO usersProfile = DAO.getInstance().getJdbcTemp().queryForObject(USERS_PROFILE_SELECT_BY_ID, new Object[]{usersProfileId}, new UsersProfileRowMapper());
            return Optional.ofNullable(usersProfile);
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("usersProfileId: " + usersProfileId + ", msg: " + ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UsersProfileVO> selectProfileByXid(String usersProfileXid) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfileByXid(String usersProfileXid) id:" + usersProfileXid);
        try {
            UsersProfileVO usersProfile = DAO.getInstance().getJdbcTemp().queryForObject(USERS_PROFILE_SELECT_BY_XID, new Object[]{usersProfileXid}, new UsersProfileRowMapper());
            return Optional.ofNullable(usersProfile);
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("usersProfileXid: " + usersProfileXid + ", msg: " + ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public List<UsersProfileVO> selectProfiles(int offset, int limit) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectProfiles(int offset, int limit) limit:" + limit + " offset:" + offset);
        try {
            return DAO.getInstance().getJdbcTemp().query(USERS_PROFILE_SELECT_ORDER_BY_NAME_LIMIT_OFFSET, new Object[]{limit, offset}, new UsersProfileRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Integer> selectUsersByProfileId(int usersProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectUsersByProfileId(int usersProfileId) id:" + usersProfileId);
        try {
            return DAO.getInstance().getJdbcTemp().query(USERS_SELECT_BY_USERS_PROFILE_ID, new Object[]{usersProfileId}, (rs, rowNum) ->
                    rs.getInt(COLUMN_NAME_USER_ID));
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("usersProfileId: " + usersProfileId + ", msg: " + ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    @Override
    public int updateProfileName(String name, int usersProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("updateProfileName(String name, int usersProfileId) id:" + usersProfileId + " name: " +  name);
        try {
            return DAO.getInstance().getJdbcTemp().update(USERS_PROFILES_UPDATE_NAME_BY_ID, name, usersProfileId);
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("name: " + name + ", usersProfileId: " + usersProfileId + ", msg: " + ex.getMessage());
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    @Override
    public int deleteUserProfileByUserId(int userId) {
        if (LOG.isTraceEnabled())
            LOG.trace("deleteUserProfileByUserId(int userId) userId:" + userId);
        try {
            return DAO.getInstance().getJdbcTemp().update(USERS_USERS_PROFILE_DELETE_BY_USER_ID, userId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    @Override
    public int deleteProfile(int profileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("deleteProfile(int profileId) profileId:" + profileId);
        try {
            return DAO.getInstance().getJdbcTemp().update(USERS_PROFILE_DELETE_BY_ID, profileId);
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("profileId: " + profileId + ", msg: " + ex.getMessage());
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    @Override
    public int insertUserProfile(int userId, int userProfileId) {
        if (LOG.isTraceEnabled())
            LOG.trace("insertUserProfile(int userId, int userProfileId) userId:" + userId + ", userProfileId:" + userProfileId);
        try {
            return DAO.getInstance().getJdbcTemp().update(USERS_USERS_PROFILE_INSERT, userProfileId, userId);
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("userId: " + userId + ", userProfileId: " + userProfileId + ", msg: " + ex.getMessage());
            return 0;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    @Override
    public int insertProfile(String usersProfileXid, String usersProfileName) {
        if (LOG.isTraceEnabled())
            LOG.trace("insertProfile(String usersProfileXid, String usersProfileName) usersProfileXid:" + usersProfileXid + ", usersProfileName:" + usersProfileName);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            DAO.getInstance().getJdbcTemp().update((Connection connection) -> {
                PreparedStatement ps = connection
                        .prepareStatement(USERS_PROFILE_INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, usersProfileXid);
                ps.setString(2, usersProfileName);
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("usersProfileXid: " + usersProfileXid + ", usersProfileName: " + usersProfileName + ", msg: " + ex.getMessage());
            return -1;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public List<WatchListAccess> selectWatchListPermissionsByProfileId(int usersProfileId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("selectWatchListPermissionsByProfileId(int usersProfileId) usersProfileId:" + usersProfileId);
        }

        return DAO.getInstance().getJdbcTemp().query(WATCHLIST_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID, new Object[]{usersProfileId}, (rs, rowNum) -> {
            WatchListAccess dataPointAccess = new WatchListAccess();
            dataPointAccess.setId(rs.getInt(COLUMN_NAME_WATCH_LIST_ID));
            dataPointAccess.setPermission(rs.getInt(COLUMN_NAME_PERMISSION));
            return dataPointAccess;
        });

    }

    @Override
    public List<ViewAccess> selectViewPermissionsByProfileId(int usersProfileId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("selectViewPermissionsByUsersProfileId(final int usersProfileId) usersProfileId:" + usersProfileId);
        }

        return DAO.getInstance().getJdbcTemp().query(VIEW_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID, new Object[]{usersProfileId}, (rs, rowNum) -> {
            ViewAccess viewAccess = new ViewAccess();
            viewAccess.setId(rs.getInt(COLUMN_NAME_VIEW_ID));
            viewAccess.setPermission(rs.getInt(COLUMN_NAME_PERMISSION));
            return viewAccess;
        });
    }

    @Override
    public List<Integer> selectDataSourcePermissionsByProfileId(int profileId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("selectDataSourcePermissionsByProfileId(int profileId) profileId:" + profileId);
        }

        return DAO.getInstance().getJdbcTemp().query(DATA_SOURCE_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID,
                        new Object[]{profileId}, (rs, rowNum) -> rs.getInt(COLUMN_NAME_DATA_SOURCE_ID));
    }

    @Override
    public List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("selectWatchListPermissionsByProfileId(int profileId) profileId:" + profileId);
        }

        return DAO.getInstance().getJdbcTemp().query(DATA_POINT_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID, new Object[]{profileId}, (rs, rowNum) -> {
            DataPointAccess dataPointAccess = new DataPointAccess();
            dataPointAccess.setDataPointId(rs.getInt(COLUMN_NAME_DATA_POINT_ID));
            dataPointAccess.setPermission(rs.getInt(COLUMN_NAME_PERMISSION));
            return dataPointAccess;
        });

    }

    @Override
    public String generateUniqueXid(String prefix) {
        String xid = XidUtils.generateXid(prefix);
        while (!isXidUnique(xid, -1)) {
            xid = XidUtils.generateXid(prefix);
        }
        return xid;
    }

    private boolean isXidUnique(String xid, int excludeId) {
        return DAO.getInstance().getJdbcTemp().queryForObject("select count(*) from " + TABLE_NAME_USERS_PROFILES
                + " where xid=? and id<>?", new Object[] { xid, excludeId }, Integer.class) == 0;
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
