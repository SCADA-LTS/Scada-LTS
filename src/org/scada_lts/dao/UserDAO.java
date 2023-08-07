package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

/**
 * DAO for User.
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 *         person supporting and coreecting translation Jerzy Piejko
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class UserDAO implements IUserDAO {

	private static final Log LOG = LogFactory.getLog(UserDAO.class);

	private final static String COLUMN_NAME_ID = "id";
	private final static String COLUMN_NAME_USERNAME = "username";
	private final static String COLUMN_NAME_PASSWORD = "password";
	private final static String COLUMN_NAME_EMAIL = "email";
	private final static String COLUMN_NAME_PHONE = "phone";
	private final static String COLUMN_NAME_ADMIN = "admin";
	private final static String COLUMN_NAME_DISABLED = "disabled";
	private final static String COLUMN_NAME_SELECTED_WATCH_LIST = "selectedWatchList";
	private final static String COLUMN_NAME_HOME_URL = "homeUrl";
	private final static String COLUMN_NAME_LAST_LOGIN = "lastLogin";
	private final static String COLUMN_NAME_RECEIVE_ALARM_EMAILS = "receiveAlarmEmails";
	private final static String COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS = "receiveOwnAuditEvents";
	private final static String COLUMN_NAME_HIDE_MENU = "hideMenu";
	private final static String COLUMN_NAME_THEME = "theme";
	private static final String COLUMN_NAME_FIRST_NAME = "firstName";
	private static final String COLUMN_NAME_LAST_NAME = "lastName";
	private static final String COLUMN_NAME_LANG = "lang";
	private static final String COLUMN_NAME_ENABLE_FULL_SCREEN = "enableFullScreen";
	private static final String COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN = "hideShortcutDisableFullScreen";
	private static final String TABLE_NAME = "users";

	private static final int DAO_EMPTY_RESULT = 0;
	private static final int DAO_EXCEPTION = -1;

	// @formatter:off
	private static final String USER_SELECT_ID = ""
			+ "select "
				+ COLUMN_NAME_ID + " "
			+ "from users ";

	private static final String USER_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_USERNAME + ", "
				+ COLUMN_NAME_FIRST_NAME + ", "
				+ COLUMN_NAME_LAST_NAME + ", "
				+ COLUMN_NAME_PASSWORD + ", "
				+ COLUMN_NAME_EMAIL + ", "
				+ COLUMN_NAME_PHONE + ", "
				+ COLUMN_NAME_ADMIN + ", "
				+ COLUMN_NAME_DISABLED + ", "
				+ COLUMN_NAME_SELECTED_WATCH_LIST + ", "
				+ COLUMN_NAME_HOME_URL + ", "
				+ COLUMN_NAME_LAST_LOGIN + ", "
				+ COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", "
				+ COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + ", "
				+ COLUMN_NAME_HIDE_MENU + ", "
				+ COLUMN_NAME_LANG + ", "
				+ COLUMN_NAME_ENABLE_FULL_SCREEN + ", "
				+ COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN + ", "
				+ COLUMN_NAME_THEME + " "
			+ "from users ";

	private static final String USER_SELECT_ORDER = ""
				+ USER_SELECT
			+ "order by username ";

	private static final String USER_SELECT_WHERE_ID = ""
				+ USER_SELECT
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String USER_SELECT_WHERE_USERNAME = ""
			+ USER_SELECT
			+ "where lower("
			+ COLUMN_NAME_USERNAME + ")=?";

	private static final String USER_SELECT_ACTIVE = ""
				+ USER_SELECT
			+ "where "
				+ COLUMN_NAME_DISABLED + "=? ";

	private static final String USER_INSERT = ""
			+ "insert into users ("
			+ COLUMN_NAME_USERNAME + ", "
			+ COLUMN_NAME_FIRST_NAME + ", "
			+ COLUMN_NAME_LAST_NAME + ", "
			+ COLUMN_NAME_PASSWORD + ", "
			+ COLUMN_NAME_EMAIL + ", "
			+ COLUMN_NAME_PHONE + ", "
			+ COLUMN_NAME_ADMIN + ", "
			+ COLUMN_NAME_DISABLED + ", "
			+ COLUMN_NAME_HOME_URL + ", "
			+ COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", "
			+ COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + ", "
			+ COLUMN_NAME_HIDE_MENU + ", "
			+ COLUMN_NAME_LANG + ", "
			+ COLUMN_NAME_ENABLE_FULL_SCREEN + ", "
			+ COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN + ", "
			+ COLUMN_NAME_THEME + ") "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

	private static final String USER_UPDATE = ""
			+ "update users set "
			+ COLUMN_NAME_USERNAME + "=?, "
			+ COLUMN_NAME_FIRST_NAME + "=?, "
			+ COLUMN_NAME_LAST_NAME + "=?, "
			+ COLUMN_NAME_PASSWORD + "=?, "
			+ COLUMN_NAME_EMAIL + "=?, "
			+ COLUMN_NAME_PHONE + "=?, "
			+ COLUMN_NAME_ADMIN + "=?, "
			+ COLUMN_NAME_DISABLED + "=?, "
			+ COLUMN_NAME_HOME_URL + "=?, "
			+ COLUMN_NAME_RECEIVE_ALARM_EMAILS + "=?, "
			+ COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + "=?, "
			+ COLUMN_NAME_HIDE_MENU + "=?, "
			+ COLUMN_NAME_LANG + "=?, "
			+ COLUMN_NAME_ENABLE_FULL_SCREEN + "=?, "
			+ COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN + "=?, "
			+ COLUMN_NAME_THEME + "=? "
			+ "where "
			+ COLUMN_NAME_ID + "=? ";

	private static final String USER_UPDATE_LOGIN = ""
			+ "update users set "
				+ COLUMN_NAME_LAST_LOGIN + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String USER_UPDATE_HOME_URL = ""
			+ "update users set "
				+ COLUMN_NAME_HOME_URL + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String USER_DELETE = ""
			+ "delete from users where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String USER_UPDATE_PASSWORD = "" +
			"UPDATE " + TABLE_NAME + " SET " +
			COLUMN_NAME_PASSWORD + "=? " +
			" WHERE " + COLUMN_NAME_ID + "=?";

	private static final String USER_UPDATE_LANG = ""
			+ "update users set "
			+ COLUMN_NAME_LANG + "=? "
			+ "where "
			+ COLUMN_NAME_ID + "=? ";

	// @formatter:on

	private class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt(COLUMN_NAME_ID));
			user.setUsername(rs.getString(COLUMN_NAME_USERNAME));
			user.setPassword(rs.getString(COLUMN_NAME_PASSWORD));
			user.setEmail(rs.getString(COLUMN_NAME_EMAIL));
			user.setPhone(rs.getString(COLUMN_NAME_PHONE));
			user.setAdmin(DAO.charToBool(rs.getString(COLUMN_NAME_ADMIN)));
			user.setDisabled(DAO.charToBool(rs.getString(COLUMN_NAME_DISABLED)));
			user.setSelectedWatchList(rs.getInt(COLUMN_NAME_SELECTED_WATCH_LIST));
			user.setHomeUrl(rs.getString(COLUMN_NAME_HOME_URL));
			user.setLastLogin(rs.getLong(COLUMN_NAME_LAST_LOGIN));
			user.setReceiveAlarmEmails(rs.getInt(COLUMN_NAME_RECEIVE_ALARM_EMAILS));
			user.setReceiveOwnAuditEvents(DAO.charToBool(rs.getString(COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS)));
			user.setHideMenu(rs.getBoolean(COLUMN_NAME_HIDE_MENU));
			user.setTheme(rs.getString(COLUMN_NAME_THEME));
			user.setFirstName(rs.getString(COLUMN_NAME_FIRST_NAME));
			user.setLastName(rs.getString(COLUMN_NAME_LAST_NAME));
			user.setLang(rs.getString(COLUMN_NAME_LANG));
			user.setEnableFullScreen(rs.getBoolean(COLUMN_NAME_ENABLE_FULL_SCREEN));
			user.setHideShortcutDisableFullScreen(rs.getBoolean(COLUMN_NAME_HIDE_SHORTCUT_DISABLE_FULL_SCREEN));
			return user;
		}
	}

	@Override
	public List<Integer> getAll() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getAll()");
		}

		return DAO.getInstance().getJdbcTemp().queryForList(USER_SELECT_ID, Integer.class);
	}

	@Override
	public User getUser(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getUser(int id) id:" + id);
		}

		User user;
		try {
			user = DAO.getInstance().getJdbcTemp().queryForObject(USER_SELECT_WHERE_ID, new Object[]{id}, new UserRowMapper());
		} catch (EmptyResultDataAccessException e) {
			user = null;
		}
		return user;
	}

	@Override
	public User getUser(String username) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getUser(String username) username:" + username);
		}

		User user;
		try {
			user = DAO.getInstance().getJdbcTemp().queryForObject(USER_SELECT_WHERE_USERNAME, new Object[]{username}, new UserRowMapper());
		} catch (EmptyResultDataAccessException e) {
			user = null;
		}
		return user;
	}

	@Override
	public List<User> getUsers() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getUsers()");
		}

		return DAO.getInstance().getJdbcTemp().query(USER_SELECT_ORDER, new UserRowMapper());
	}

	@Override
	public List<User> getActiveUsers() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getActiveUsers()");
		}

		return DAO.getInstance().getJdbcTemp().query(USER_SELECT_ACTIVE, new Object[]{DAO.boolToChar(false)}, new UserRowMapper());
	}

	@Override
	public void updateHomeUrl(int userId, String homeUrl) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateHomeUrl(int userId, String homeUrl) userId:" + userId + ", homeUrl:" + homeUrl);
		}

		DAO.getInstance().getJdbcTemp().update(USER_UPDATE_HOME_URL, new Object[]{homeUrl, userId});
	}

	@Override
	public void updateLogin(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateLogin(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(USER_UPDATE_LOGIN, new Object[]{System.currentTimeMillis(), userId});
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public int insert(final User user) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(User user) user:" + user.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(USER_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[]{
						user.getUsername(),
						user.getFirstName(),
						user.getLastName(),
						user.getPassword(),
						user.getEmail(),
						user.getPhone(),
						DAO.boolToChar(user.isAdmin()),
						DAO.boolToChar(user.isDisabled()),
						user.getHomeUrl(),
						user.getReceiveAlarmEmails(),
						DAO.boolToChar(user.isReceiveOwnAuditEvents()),
						user.isHideMenu(),
						user.getLang(),
						user.isEnableFullScreen(),
						user.isHideShortcutDisableFullScreen(),
						user.getTheme()
				}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void update(final User user) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(User user) user:" + user);
		}
		DAO.getInstance().getJdbcTemp().update(USER_UPDATE,
				user.getUsername(),
				user.getFirstName(),
				user.getLastName(),
				user.getPassword(),
				user.getEmail(),
				user.getPhone(),
				DAO.boolToChar(user.isAdmin()),
				DAO.boolToChar(user.isDisabled()),
				user.getHomeUrl(),
				user.getReceiveAlarmEmails(),
				DAO.boolToChar(user.isReceiveOwnAuditEvents()),
				user.isHideMenu(),
				user.getLang(),
				user.isEnableFullScreen(),
				user.isHideShortcutDisableFullScreen(),
				user.getTheme(),
				user.getId());
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void delete(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int userId) userId:" + userId);
		}
		DAO.getInstance().getJdbcTemp().update(USER_DELETE, userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void updateUserPassword(int userId, String newPassword) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateUserPassword(int userId, String newPassword) userId:" + userId);
		}
		DAO.getInstance().getJdbcTemp().update(USER_UPDATE_PASSWORD, newPassword, userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void updateUserLang(int userId, String lang) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateUserPassword(int userId, String newPassword) userId:" + userId);
		}
		DAO.getInstance().getJdbcTemp().update(USER_UPDATE_LANG, lang, userId);
	}
}
