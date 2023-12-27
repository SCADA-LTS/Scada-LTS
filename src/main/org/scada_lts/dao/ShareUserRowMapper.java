package org.scada_lts.dao;

import com.serotonin.mango.view.ShareUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ShareUserRowMapper implements RowMapper<ShareUser> {

    private final String userIdColumnName;
    private final String permissionColumnName;

    private static final String COLUMN_NAME_USER_ID = "userId";
    private static final String COLUMN_NAME_PERMISSION = "permission";

    public ShareUserRowMapper(String userIdColumnName, String permissionColumnName) {
        this.userIdColumnName = userIdColumnName;
        this.permissionColumnName = permissionColumnName;
    }

    public static ShareUserRowMapper defaultName() {
        return new ShareUserRowMapper(COLUMN_NAME_USER_ID, COLUMN_NAME_PERMISSION);
    }

    @Override
    public ShareUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        ShareUser profile = new ShareUser();
        profile.setUserId(rs.getInt(userIdColumnName));
        profile.setAccessType(rs.getInt(permissionColumnName));
        return profile;
    }
}
