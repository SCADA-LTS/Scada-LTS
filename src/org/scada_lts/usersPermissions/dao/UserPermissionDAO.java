package org.scada_lts.usersPermissions.dao;


import com.mysql.jdbc.Statement;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.scada_lts.usersPermissions.model.UserPermission;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private static final String USER_PERMISSION_INSERT = ""
            + "insert into " + TABLE_NAME + " ("
            + COLUMN_NAME_ENTITY_XID + ", "
            + COLUMN_NAME_PERMISSION + ") "
            + "values (?,?)";

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

        UserPermission userPermission;

        try {
            userPermission = (UserPermission) DAO.getInstance().getJdbcTemp().queryForObject(USER_PERMISSION_SELECT_WHERE_ENTITY_XID, new Object[]{entityXid}, new UserPermissionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return userPermission;
    }

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    public int insert(final UserPermission userPermission) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("insert(final UserPermission userPermission) userPermission:" + userPermission.toString());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(USER_PERMISSION_INSERT, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        userPermission.getEntityXid(),
                        userPermission.getPermission()
                }).setValues(ps);
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().intValue();
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
