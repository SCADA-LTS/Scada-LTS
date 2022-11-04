package org.scada_lts.dao.mappers;

import org.scada_lts.web.mvc.api.user.UserInfoSimple;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoRowMapper implements RowMapper<UserInfoSimple> {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_USERNAME = "username";
    private static final String COLUMN_NAME_FIRST_NAME = "firstName";
    private static final String COLUMN_NAME_LAST_NAME = "lastName";
    private static final String COLUMN_NAME_EMAIL = "email";
    private static final String COLUMN_NAME_PHONE = "phone";
    private static final String COLUMN_NAME_ADMIN = "admin";
    private static final String COLUMN_NAME_DISABLED = "disabled";

    private static final String TABLE_NAME = "users";


    @Override
    public UserInfoSimple mapRow(ResultSet resultSet, int i) throws SQLException {
        return new UserInfoSimple(
                resultSet.getInt(COLUMN_NAME_ID),
                resultSet.getString(COLUMN_NAME_USERNAME),
                resultSet.getString(COLUMN_NAME_FIRST_NAME),
                resultSet.getString(COLUMN_NAME_LAST_NAME),
                resultSet.getString(COLUMN_NAME_PHONE),
                resultSet.getString(COLUMN_NAME_EMAIL),
                resultSet.getBoolean(COLUMN_NAME_ADMIN),
                resultSet.getBoolean(COLUMN_NAME_DISABLED)
        );
    }

    public String selectUserInfoFromDatabase() {
        return "SELECT " +
                COLUMN_NAME_ID + ", " +
                COLUMN_NAME_USERNAME + ", " +
                COLUMN_NAME_FIRST_NAME + ", " +
                COLUMN_NAME_LAST_NAME + ", " +
                COLUMN_NAME_PHONE + ", " +
                COLUMN_NAME_EMAIL + ", " +
                COLUMN_NAME_ADMIN + ", " +
                COLUMN_NAME_DISABLED + " " +
                "FROM " + TABLE_NAME;
    }
}
