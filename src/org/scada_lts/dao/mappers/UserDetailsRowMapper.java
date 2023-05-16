package org.scada_lts.dao.mappers;

import org.scada_lts.web.mvc.api.user.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDetailsRowMapper implements RowMapper<UserInfo> {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_USERNAME = "username";
    private static final String COLUMN_NAME_FIRST_NAME = "firstName";
    private static final String COLUMN_NAME_LAST_NAME = "lastName";
    private static final String COLUMN_NAME_EMAIL = "email";
    private static final String COLUMN_NAME_PHONE = "phone";
    private static final String COLUMN_NAME_ADMIN = "admin";
    private static final String COLUMN_NAME_DISABLED = "disabled";
    private static final String COLUMN_NAME_SELECTED_WATCH_LIST = "selectedWatchList";
    private static final String COLUMN_NAME_HOME_URL = "homeUrl";
    private static final String COLUMN_NAME_LAST_LOGIN = "lastLogin";
    private static final String COLUMN_NAME_RECEIVE_ALARM_EMAILS = "receiveAlarmEmails";
    private static final String COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS = "receiveOwnAuditEvents";
    private static final String COLUMN_NAME_HIDE_MENU = "hideMenu";
    private static final String COLUMN_NAME_THEME = "theme";
    private static final String COLUMN_NAME_USERPROFILE_ID = "userProfileId";
    private static final String COLUMN_NAME_USER_ID = "userId";
    private static final String COLUMN_NAME_ENABLE_FULL_SCREEN = "enableFullScreen";
    private static final String COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN = "hideShortcutDisableFullScreen";

    private static final String TABLE_NAME = "users";
    private static final String TABLE_NAME_PROFILE = "usersUsersProfiles";


    @Override
    public UserInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        return new UserInfo(
                resultSet.getInt(COLUMN_NAME_ID),
                resultSet.getString(COLUMN_NAME_USERNAME),
                resultSet.getString(COLUMN_NAME_FIRST_NAME),
                resultSet.getString(COLUMN_NAME_LAST_NAME),
                resultSet.getString(COLUMN_NAME_EMAIL),
                resultSet.getString(COLUMN_NAME_PHONE),
                resultSet.getBoolean(COLUMN_NAME_ADMIN),
                resultSet.getBoolean(COLUMN_NAME_DISABLED),
                resultSet.getString(COLUMN_NAME_HOME_URL),
                resultSet.getLong(COLUMN_NAME_LAST_LOGIN),
                resultSet.getInt(COLUMN_NAME_RECEIVE_ALARM_EMAILS),
                resultSet.getBoolean(COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS),
                resultSet.getString(COLUMN_NAME_THEME),
                resultSet.getBoolean(COLUMN_NAME_HIDE_MENU),
                resultSet.getInt(COLUMN_NAME_USERPROFILE_ID),
                resultSet.getBoolean(COLUMN_NAME_ENABLE_FULL_SCREEN),
                resultSet.getBoolean(COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN)
        );
    }

    public String selectUserDetailsFromDatabase(int userId) {
        return "SELECT " +
                COLUMN_NAME_ID + ", " +
                COLUMN_NAME_USERNAME + ", " +
                COLUMN_NAME_PHONE + ", " +
                COLUMN_NAME_FIRST_NAME + ", " +
                COLUMN_NAME_LAST_NAME + ", " +
                COLUMN_NAME_EMAIL + ", " +
                COLUMN_NAME_ADMIN + ", " +
                COLUMN_NAME_DISABLED + ", " +
                COLUMN_NAME_SELECTED_WATCH_LIST + ", " +
                COLUMN_NAME_HOME_URL + ", " +
                COLUMN_NAME_LAST_LOGIN + ", " +
                COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", " +
                COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + ", " +
                COLUMN_NAME_HIDE_MENU + ", " +
                COLUMN_NAME_THEME + ", " +
                COLUMN_NAME_USERPROFILE_ID + " " +
                "FROM " + TABLE_NAME + " LEFT JOIN " + TABLE_NAME_PROFILE +
                " ON " + TABLE_NAME + "." + COLUMN_NAME_ID +
                "=" + TABLE_NAME_PROFILE + "." + COLUMN_NAME_USER_ID +
                " WHERE " + COLUMN_NAME_ID + "=" + userId;
    }
}
