package org.scada_lts.permissions.migration.dao;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.CharTo;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.IUserDAO;
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

public final class OnlyMigrationUserDAO implements IUserDAO {

	private static final Log LOG = LogFactory.getLog(OnlyMigrationUserDAO.class);

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

	// @formatter:off
	private static final String USER_SELECT_ID = ""
			+ "select "
				+ COLUMN_NAME_ID + " "
			+ "from users ";

	private static final String USER_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_USERNAME + ", "
				+ COLUMN_NAME_PASSWORD + ", "
				+ COLUMN_NAME_EMAIL + ", "
				+ COLUMN_NAME_PHONE + ", "
				+ COLUMN_NAME_ADMIN + ", "
				+ COLUMN_NAME_DISABLED + ", "
				+ COLUMN_NAME_SELECTED_WATCH_LIST + ", "
				+ COLUMN_NAME_HOME_URL + ", "
				+ COLUMN_NAME_LAST_LOGIN + ", "
				+ COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", "
				+ COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + " "
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
				+ COLUMN_NAME_PASSWORD + ", "
				+ COLUMN_NAME_EMAIL + ", "
				+ COLUMN_NAME_PHONE + ", "
				+ COLUMN_NAME_ADMIN + ", "
				+ COLUMN_NAME_DISABLED + ", "
				+ COLUMN_NAME_HOME_URL + ", "
				+ COLUMN_NAME_RECEIVE_ALARM_EMAILS + ", "
				+ COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + ") "
			+ "values (?,?,?,?,?,?,?,?,?) ";

	private static final String USER_UPDATE = ""
			+ "update users set "
				+ COLUMN_NAME_USERNAME + "=?, "
				+ COLUMN_NAME_PASSWORD + "=?, "
				+ COLUMN_NAME_EMAIL + "=?, "
				+ COLUMN_NAME_PHONE + "=?, "
				+ COLUMN_NAME_ADMIN + "=?, "
				+ COLUMN_NAME_DISABLED + "=?, "
				+ COLUMN_NAME_HOME_URL + "=?, "
				+ COLUMN_NAME_RECEIVE_ALARM_EMAILS + "=?, "
				+ COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS + "=? "
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
			user.setAdmin(CharTo.charToBool(rs.getString(COLUMN_NAME_ADMIN)));
			user.setDisabled(CharTo.charToBool(rs.getString(COLUMN_NAME_DISABLED)));
			user.setSelectedWatchList(rs.getInt(COLUMN_NAME_SELECTED_WATCH_LIST));
			user.setHomeUrl(rs.getString(COLUMN_NAME_HOME_URL));
			user.setLastLogin(rs.getLong(COLUMN_NAME_LAST_LOGIN));
			user.setReceiveAlarmEmails(rs.getInt(COLUMN_NAME_RECEIVE_ALARM_EMAILS));
			user.setReceiveOwnAuditEvents(CharTo.charToBool(rs.getString(COLUMN_NAME_RECEIVE_OWN_AUDIT_EVENTS)));
			return user;
		}
	}

	public List<Integer> getAll() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getAll()");
		}

		return DAO.getInstance().getJdbcTemp().queryForList(USER_SELECT_ID, Integer.class);
	}

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

	public List<User> getUsers() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getUsers()");
		}

		return DAO.getInstance().getJdbcTemp().query(USER_SELECT_ORDER, new UserRowMapper());
	}

	public List<User> getActiveUsers() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getActiveUsers()");
		}

		return DAO.getInstance().getJdbcTemp().query(USER_SELECT_ACTIVE, new Object[]{CharTo.boolToChar(false)}, new UserRowMapper());
	}

	public void updateHomeUrl(int userId, String homeUrl) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateHomeUrl(int userId, String homeUrl) userId:" + userId + ", homeUrl:" + homeUrl);
		}

		DAO.getInstance().getJdbcTemp().update(USER_UPDATE_HOME_URL, new Object[]{homeUrl, userId});
	}

	public void updateLogin(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateLogin(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(USER_UPDATE_LOGIN, new Object[]{System.currentTimeMillis(), userId});
	}

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
						user.getPassword(),
						user.getEmail(),
						user.getPhone(),
						CharTo.boolToChar(user.isAdmin()),
						CharTo.boolToChar(user.isDisabled()),
						user.getHomeUrl(),
						user.getReceiveAlarmEmails(),
						CharTo.boolToChar(user.isReceiveOwnAuditEvents())
				}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void update(final User user) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(User user) user:" + user);
		}
		DAO.getInstance().getJdbcTemp().update(USER_UPDATE, new Object[]{
				user.getUsername(),
				user.getPassword(),
				user.getEmail(),
				user.getPhone(),
				CharTo.boolToChar(user.isAdmin()),
				CharTo.boolToChar(user.isDisabled()),
				user.getHomeUrl(),
				user.getReceiveAlarmEmails(),
				CharTo.boolToChar(user.isReceiveOwnAuditEvents()),
				user.getId()
		});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void delete(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(USER_DELETE, new Object[]{userId});
	}

	@Override
	public void updateHideMenu(User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateScadaTheme(User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateUserPassword(int userId, String newPassword) {
		throw new UnsupportedOperationException();
	}
}
