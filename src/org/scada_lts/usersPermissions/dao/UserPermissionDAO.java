package org.scada_lts.usersPermissions.dao;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.usersPermissions.model.UserPermission;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Repository
public class UserPermissionDAO {

    private static final Log LOG = LogFactory.getLog(UserPermissionDAO.class);

    private static final String TABLE_NAME = "usersPermissions";
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_ENTITY_XID = "entityXid";
    private static final String COLUMN_NAME_PERMISSION = "permission";

    // @formatter:off

    private static final String USER_PERMISSION_SELECT_ALL = ""
            + "select "
            + COLUMN_NAME_ID + ", "
            + COLUMN_NAME_ENTITY_XID + ", "
            + COLUMN_NAME_PERMISSION + " "
            + "from "
            + TABLE_NAME + ";";

    private static final String USER_PERMISSION_SELECT_WHERE_ENTITY_XID = ""
            + "select "
            + COLUMN_NAME_ID + ", "
            + COLUMN_NAME_ENTITY_XID + ", "
            + COLUMN_NAME_PERMISSION + " "
            + "from " + TABLE_NAME + " where "
            + COLUMN_NAME_ENTITY_XID + "=?;";

    // @formatter:on

    public List<UserPermission> getAllUsersPermissions() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getAllUsersPermissions()");
        }

        List<UserPermission> userPermissions = new ArrayList<>();

        userPermissions = (List<UserPermission>) DAO.getInstance().getJdbcTemp().query(USER_PERMISSION_SELECT_ALL, new Object[]{}, new UserPermissionRowMapper() );

        return userPermissions;
    }

    public UserPermission getUserPermission(String entityXid) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getUserPermission()");
        }

        UserPermission userPermission = new UserPermission();
        userPermission = (UserPermission) DAO.getInstance().getJdbcTemp().query(USER_PERMISSION_SELECT_WHERE_ENTITY_XID, new Object[]{entityXid}, new UserPermissionRowMapper());
        return userPermission;
    }

    private class UserPermissionRowMapper implements RowMapper<UserPermission> {

        @Override
        public UserPermission mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            UserPermission userPermission = new UserPermission();

            userPermission.setId(resultSet.getInt(COLUMN_NAME_ID));
            userPermission.setEntityXid(resultSet.getString(COLUMN_NAME_ENTITY_XID));
            userPermission.setPermission(resultSet.getInt(COLUMN_NAME_PERMISSION));

            return userPermission;
        }
    }

}
