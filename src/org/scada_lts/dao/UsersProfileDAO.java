package org.scada_lts.dao;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsersProfileDAO {

    private static final Log LOG = LogFactory.getLog(UsersProfileDAO.class);

    private static final String COLUMN_NAME_USER_ID = "userId";
    private static final String COLUMN_NAME_USER_PROFILE_ID = "userProfileId";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";


    private static final String USERS_PROFILES_SELECT_BY_USER_ID = ""
            + "select "
            + "up." + COLUMN_NAME_ID + ", "
            + "up." + COLUMN_NAME_XID + ", "
            + "up." + COLUMN_NAME_NAME + " "
            + "from usersUsersProfiles uup left join usersProfiles up on "
            + "up." + COLUMN_NAME_ID + "="
            + "uup." + COLUMN_NAME_USER_PROFILE_ID + " "
            + "where uup." + COLUMN_NAME_USER_ID + "=?";

    private JdbcTemplate jdbcTemplate;

    public UsersProfileDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UsersProfileVO> selectUsersProfileByUserId(int userId) {
        if (LOG.isTraceEnabled())
            LOG.trace("selectUsersProfileByUserId(int userId) userId:" + userId);
        try {
            UsersProfileVO usersProfile = jdbcTemplate.queryForObject(USERS_PROFILES_SELECT_BY_USER_ID, new Object[]{userId}, new UsersProfileRowMapper());
            return Optional.ofNullable(usersProfile);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private class UsersProfileRowMapper implements RowMapper<UsersProfileVO> {

        @Override
        public UsersProfileVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            UsersProfileVO pointLink = new UsersProfileVO();
            pointLink.setId(rs.getInt(COLUMN_NAME_ID));
            pointLink.setXid(rs.getString(COLUMN_NAME_XID));
            pointLink.setName(rs.getString(COLUMN_NAME_NAME));
            return pointLink;
        }
    }
}
