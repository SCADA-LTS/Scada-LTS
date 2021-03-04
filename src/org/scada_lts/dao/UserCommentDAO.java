/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.dao;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO for UserComment
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class UserCommentDAO {

	private static final Log LOG = LogFactory.getLog(UserCommentDAO.class);

	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_COMMENT_TYPE = "commentType";
	private static final String COLUMN_NAME_TYPE_KEY = "typeKey";
	private static final String COLUMN_NAME_TS = "ts";
	private static final String COLUMN_NAME_COMMENT_TEXT = "commentText";

	private static final String COLUMN_NAME_U_ID = "id";
	private static final String COLUMN_NAME_U_USERNAME = "username";

	// @formatter:off
	private static final String USER_COMMENT_SELECT = ""
			+ "select "
				+ "uc." + COLUMN_NAME_USER_ID + ", "
				+ "u." + COLUMN_NAME_U_USERNAME + ", "
				+ "uc." + COLUMN_NAME_TS + ", "
				+ "uc." + COLUMN_NAME_COMMENT_TEXT + " "
			+ "from userComments uc left join users u on "
				+ "uc." + COLUMN_NAME_USER_ID + "="
				+ "u." + COLUMN_NAME_U_ID + " ";

	private static final String EVENT_COMMENT_SELECT = USER_COMMENT_SELECT
			+ "where "
				+ "uc." + COLUMN_NAME_COMMENT_TYPE + "="
				+ UserComment.TYPE_EVENT + " "
			+ "and "
				+ "us." + COLUMN_NAME_TYPE_KEY + "=? "
			+ "order by "
				+ "uc." + COLUMN_NAME_TS;

	private static final String POINT_COMMENT_SELECT = USER_COMMENT_SELECT
			+ "where "
				+ "uc." + COLUMN_NAME_COMMENT_TYPE + "="
				+ UserComment.TYPE_POINT + " "
			+ "and "
				+ "uc." + COLUMN_NAME_TYPE_KEY + "=? "
			+ "order by "
				+ "uc." + COLUMN_NAME_TS;

	private static final String USER_COMMENT_INSERT = ""
			+ "insert into userComments ("
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_COMMENT_TYPE + ", "
				+ COLUMN_NAME_TYPE_KEY + ", "
				+ COLUMN_NAME_TS + ", "
				+ COLUMN_NAME_COMMENT_TEXT + ") "
			+ "values (?,?,?,?,?) ";

	private static final String USER_COMMENT_UPDATE_ID_TO_NULL = ""
			+ "update userComments set "
				+ COLUMN_NAME_USER_ID + "=null "
			+ "where "
				+ COLUMN_NAME_USER_ID + "=? ";

	private static final String USER_COMMENT_DELETE = ""
			+ "delete from userComments where "
				+ COLUMN_NAME_COMMENT_TYPE + "=? "
			+ "and "
				+ COLUMN_NAME_TYPE_KEY + " ";

	private static final String USER_COMMENT_DELETE_ONE = "" +
			"DELETE FROM userComments WHERE " +
			COLUMN_NAME_USER_ID + "=? AND " +
			COLUMN_NAME_COMMENT_TYPE + "=? AND " +
			COLUMN_NAME_TYPE_KEY + "=? AND " +
			COLUMN_NAME_TS + "=?";
	// @formatter:on

	private class UserCommentRowMapper implements RowMapper<UserComment> {

		@Override
		public UserComment mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserComment uC = new UserComment();
			uC.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
			uC.setUsername(rs.getString(COLUMN_NAME_U_USERNAME));
			uC.setTs(rs.getLong(COLUMN_NAME_TS));
			uC.setComment(rs.getString(COLUMN_NAME_COMMENT_TEXT));
			return uC;
		}
	}

	public List<UserComment> getEventComments(EventInstance event) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getEventComments(EventInstance event) event:" + event.toString());
		}

		return DAO.getInstance().getJdbcTemp().query(EVENT_COMMENT_SELECT, new Object[] {event.getId()}, new UserCommentRowMapper());

	}

	public List<UserComment> getPointComments(DataPointVO dataPoint) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointComments(DataPointVO dataPoint) dataPoint:" + dataPoint.toString());
		}

		return DAO.getInstance().getJdbcTemp().query(POINT_COMMENT_SELECT, new Object[] {dataPoint.getId()}, new UserCommentRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final UserComment userComment, final int typeId, final int referenceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(UserComment comment, int typeId, int referenceId) userComment:" + userComment.toString() + " typeId:" + typeId + " referenceId:" + referenceId);
		}

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(USER_COMMENT_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
						userComment.getUserId(),
						typeId,
						referenceId,
						userComment.getTs(),
						userComment.getComment()
				}).setValues(ps);
				return ps;
			}
		});

		return userComment.getUserId();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(USER_COMMENT_UPDATE_ID_TO_NULL, new Object[] {userId});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteUserCommentEvent() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteUserCommentEvent()");
		}

		String templateDeleteTypeEvent = USER_COMMENT_DELETE + "not in (select id from events)";

		DAO.getInstance().getJdbcTemp().update(templateDeleteTypeEvent, new Object[] {UserComment.TYPE_EVENT});
	}

	public int deleteUserComment(int userId, int typeId, int referenceId, long ts) {
		if(LOG.isTraceEnabled()) {
			LOG.trace("deleteUserComment(UserComment::"+userId+"::"+ts);
		}

		return DAO.getInstance().getJdbcTemp().update(USER_COMMENT_DELETE_ONE, new Object[] {userId, typeId, referenceId, ts});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteUserCommentPoint(String dataPointIdList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteUserCommentPoint(String dataPointIdList) dataPointIdList:" + dataPointIdList);
		}

		ArrayList<String> parameters = new ArrayList<>(Arrays.asList(dataPointIdList.split(",")));

		StringBuilder queryBuilder = new StringBuilder(USER_COMMENT_DELETE + " in (?");
		for (int i = 1; i<parameters.size(); i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		parameters.add(0, String.valueOf(UserComment.TYPE_POINT));

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), parameters.toArray());
	}

}
