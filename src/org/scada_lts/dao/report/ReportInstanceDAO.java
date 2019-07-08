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
package org.scada_lts.dao.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.event.EventDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.report.ReportInstance;

/**
 * DAO for ReportInstance
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ReportInstanceDAO {

	private static final Log LOG = LogFactory.getLog(ReportInstanceDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_INCLUDE_EVENTS = "includeEvents";
	private static final String COLUMN_NAME_INCLUDE_USER_COMMENTS = "includeUserComments";
	private static final String COLUMN_NAME_REPORT_START_TIME = "reportStartTime";
	private static final String COLUMN_NAME_REPORT_END_TIME = "reportEndTime";
	private static final String COLUMN_NAME_RUN_START_TIME = "runStartTime";
	private static final String COLUMN_NAME_RUN_END_TIME = "runEndTime";
	private static final String COLUMN_NAME_RECORD_COUNT = "recordCount";
	private static final String COLUMN_NAME_PREVENT_PURGE = "preventPurge";

	private static final String COLUMN_NAME_REPORT_INSTANCE_ID = "reportInstanceId";

	// @formatter:off
	private static final String REPORT_INSTANCE_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_INCLUDE_EVENTS + ", "
				+ COLUMN_NAME_INCLUDE_USER_COMMENTS + ", "
				+ COLUMN_NAME_REPORT_START_TIME + ", "
				+ COLUMN_NAME_REPORT_END_TIME + ", "
				+ COLUMN_NAME_RUN_START_TIME + ", "
				+ COLUMN_NAME_RUN_END_TIME + ", "
				+ COLUMN_NAME_RECORD_COUNT + ", "
				+ COLUMN_NAME_PREVENT_PURGE + " "
			+ "from reportInstances ";

	private static final String REPORT_INSTANCE_INSERT = ""
			+ "insert into reportInstances ("
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_INCLUDE_EVENTS + ", "
				+ COLUMN_NAME_INCLUDE_USER_COMMENTS + ", "
				+ COLUMN_NAME_REPORT_START_TIME + ", "
				+ COLUMN_NAME_REPORT_END_TIME + ", "
				+ COLUMN_NAME_RUN_START_TIME + ", "
				+ COLUMN_NAME_RUN_END_TIME + ", "
				+ COLUMN_NAME_RECORD_COUNT + ", "
				+ COLUMN_NAME_PREVENT_PURGE + ") "
			+ "values (?,?,?,?,?,?,?,?,?,?) ";

	private static final String REPORT_INSTANCE_UPDATE_TIME = ""
			+ "update reportInstances set "
				+ COLUMN_NAME_REPORT_START_TIME + "=?, "
				+ COLUMN_NAME_REPORT_END_TIME + "=?, "
				+ COLUMN_NAME_RUN_START_TIME + "=?, "
				+ COLUMN_NAME_RUN_END_TIME + "=?, "
				+ COLUMN_NAME_RECORD_COUNT + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String REPORT_INSTANCE_DELETE = ""
			+ "delete from reportInstances where "
				+ COLUMN_NAME_ID + "=? "
			+ "and "
				+ COLUMN_NAME_USER_ID + "=? ";

	private static final String REPORT_INSTANCE_DELETE_BEFORE = ""
			+ "delete from reportInstances where "
				+ COLUMN_NAME_RUN_START_TIME + "<? "
			+ "and "
				+ COLUMN_NAME_PREVENT_PURGE + "=? ";

	private static final String REPORT_INSTANCE_UPDATE_PREVENT_PURGE = ""
			+ "update reportInstances set "
				+ COLUMN_NAME_PREVENT_PURGE + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? "
			+ "and "
				+ COLUMN_NAME_USER_ID + "=? ";

	private static final String REPORT_INSTANCE_SELECT_WHERE_ID = ""
				+ REPORT_INSTANCE_SELECT
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String REPORT_INSTANCE_SELECT_WHERE_USER_ID_ORDER = ""
				+ REPORT_INSTANCE_SELECT
			+ "where "
				+ COLUMN_NAME_USER_ID + "=? "
			+ "order by "
				+ COLUMN_NAME_RUN_START_TIME + " "
			+ "desc ";
	// @formatter:on

	private class ReportInstanceRowMapper implements RowMapper<ReportInstance> {

		@Override
		public ReportInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportInstance reportInstance = new ReportInstance();
			reportInstance.setId(rs.getInt(COLUMN_NAME_ID));
			reportInstance.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
			reportInstance.setName(rs.getString(COLUMN_NAME_NAME));
			reportInstance.setIncludeEvents(rs.getInt(COLUMN_NAME_INCLUDE_EVENTS));
			reportInstance.setIncludeUserComments(DAO.charToBool(rs.getString(COLUMN_NAME_INCLUDE_USER_COMMENTS)));
			reportInstance.setReportStartTime(rs.getLong(COLUMN_NAME_REPORT_START_TIME));
			reportInstance.setReportEndTime(rs.getLong(COLUMN_NAME_REPORT_END_TIME));
			reportInstance.setRunStartTime(rs.getLong(COLUMN_NAME_RUN_START_TIME));
			reportInstance.setRunEndTime(rs.getLong(COLUMN_NAME_RUN_END_TIME));
			reportInstance.setRecordCount(rs.getInt(COLUMN_NAME_RECORD_COUNT));
			reportInstance.setPreventPurge(DAO.charToBool(rs.getString(COLUMN_NAME_PREVENT_PURGE)));
			return reportInstance;
		}
	}

	public ReportInstance getReportInstance(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReportInstance(int id) id:" + id);
		}

		ReportInstance reportInstance;
		try {
			reportInstance = DAO.getInstance().getJdbcTemp().queryForObject(REPORT_INSTANCE_SELECT_WHERE_ID, new Object[]{id}, new ReportInstanceRowMapper());
		} catch (EmptyResultDataAccessException e) {
			reportInstance = null;
		}
		return reportInstance;
	}

	public List<ReportInstance> getReportInstances(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReportInstances(int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().query(REPORT_INSTANCE_SELECT_WHERE_USER_ID_ORDER, new Object[]{userId}, new ReportInstanceRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final ReportInstance reportInstance) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(ReportInstance reportInstance) reportInstance:" + reportInstance.toString());
		}

		KeyHolder keyholder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(REPORT_INSTANCE_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[]{
						reportInstance.getUserId(),
						reportInstance.getName(),
						reportInstance.getIncludeEvents(),
						DAO.boolToChar(reportInstance.isIncludeUserComments()),
						reportInstance.getReportStartTime(),
						reportInstance.getReportEndTime(),
						reportInstance.getRunStartTime(),
						reportInstance.getRunEndTime(),
						reportInstance.getRecordCount(),
						DAO.boolToChar(reportInstance.isPreventPurge())
				}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyholder);
		return keyholder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void updateTime(ReportInstance reportInstance) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateTime(ReportInstance reportInstance) reportInstance:" + reportInstance.toString());
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_INSTANCE_UPDATE_TIME, new Object[]{
				reportInstance.getReportStartTime(),
				reportInstance.getReportEndTime(),
				reportInstance.getRunStartTime(),
				reportInstance.getRunEndTime(),
				reportInstance.getRecordCount(),
				reportInstance.getId()
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void updatePreventPurge(int id, boolean preventPurge, int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updatePreventPurge(int id, boolean preventPurge, int userId) id:" + id + ", preventPurge:" + preventPurge + ", userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_INSTANCE_UPDATE_PREVENT_PURGE, new Object[]{
				DAO.boolToChar(preventPurge),
				id,
				userId
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id, int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id, int userId) id:" + id + ", userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_INSTANCE_DELETE, new Object[]{id, userId});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int deleteReportBefore(final long time) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteReportBefore(final long time) time:" + time);
		}

		return DAO.getInstance().getJdbcTemp().update(REPORT_INSTANCE_DELETE_BEFORE, new Object[]{ time, DAO.boolToChar(false)});
	}

	/*
		ReportInstanceEvent
	 */

	private static final String COLUMN_NAME_E_ID = "eventId";
	private static final String COLUMN_NAME_E_TYPE_ID = "typeId";
	private static final String COLUMN_NAME_E_TYPE_REF1 = "typeRef1";
	private static final String COLUMN_NAME_E_TYPE_REF2 = "typeRef2";
	private static final String COLUMN_NAME_E_ACTIVE_TS = "activeTs";
	private static final String COLUMN_NAME_E_RTN_APP = "rtnApplicable";
	private static final String COLUMN_NAME_E_RTN_TS = "rtnTs";
	private static final String COLUMN_NAME_E_RTN_CAUSE = "rtnCause";
	private static final String COLUMN_NAME_E_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_E_MESSAGE = "message";
	private static final String COLUMN_NAME_E_ACK_TS = "ackTs";
	private static final String COLUMN_NAME_E_ACK_USERNAME = "ackUsername";
	private static final String COLUMN_NAME_E_ALTERNATE_ACK_SOURCE = "alternateAckSource";

	private static final String REPORT_INSTANCE_EVENT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_E_ID + ", "
				+ COLUMN_NAME_E_TYPE_ID + ", "
				+ COLUMN_NAME_E_TYPE_REF1 + ", "
				+ COLUMN_NAME_E_TYPE_REF2 + ", "
				+ COLUMN_NAME_E_ACTIVE_TS + ", "
				+ COLUMN_NAME_E_RTN_APP + ", "
				+ COLUMN_NAME_E_RTN_TS + ", "
				+ COLUMN_NAME_E_RTN_CAUSE + ", "
				+ COLUMN_NAME_E_ALARM_LEVEL + ", "
				+ COLUMN_NAME_E_MESSAGE + ", "
				+ COLUMN_NAME_E_ACK_TS + ", 0, "
				+ COLUMN_NAME_E_ACK_USERNAME + ", "
				+ COLUMN_NAME_E_ALTERNATE_ACK_SOURCE + " "
			+ "from reportInstanceEvents "
			+ "where "
				+ COLUMN_NAME_REPORT_INSTANCE_ID + "=? "
			+ "order by "
				+ COLUMN_NAME_E_ACTIVE_TS;


	public List<EventInstance> getReportInstanceEvents(int instanceId) {
		return DAO.getInstance().getJdbcTemp().query(REPORT_INSTANCE_EVENT_SELECT, new Object[] {instanceId}, new EventDAO.ReportInstanceEventRowMapper());
	}
}
