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

import com.mysql.jdbc.Statement;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

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

		return DAO.getInstance().getJdbcTemp().queryForObject(REPORT_INSTANCE_SELECT_WHERE_ID, new Object[]{id}, new ReportInstanceRowMapper());
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
	public void deleteReportBefore(final long time) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteReportBefore(final long time) time:" + time);
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_INSTANCE_DELETE_BEFORE, new Object[]{ time, DAO.boolToChar(false)});
	}

	/*
		ReportInstanceData and ReportInstanceDataAnnotation
	 */

	public static final String COLUMN_NAME_D_POINT_VALUE = "pointValue";
	public static final String COLUMN_NAME_D_TS = "ts";
	public static final String COLUMN_NAME_D_POINT_VALUE_ID = "pointValueId";
	public static final String COLUMN_NAME_D_REPORT_INSTANCE_POINT_ID = "reportInstancePointId";

	public static final String COLUMN_NAME_A_TEXT_POINT_VALUE_SHORT = "textPointValueShort";
	public static final String COLUMN_NAME_A_TEXT_POINT_VALUE_LONG = "textPointValueLong";
	public static final String COLUMN_NAME_A_SOURCE_VALUE = "sourceValue";
	public static final String COLUMN_NAME_A_SOURCE_TYPE = "sourceType";
	public static final String COLUMN_NAME_A_SOURCE_ID = "sourceId";
	public static final String COLUMN_NAME_A_POINT_VALUE_ID = "pointValueId";
	public static final String COLUMN_NAME_A_REPORT_INSTANCE_POINT_ID = "reportInstancePointId";

	private static final String COLUMN_NAME_DATA_POINT_ID = "dataPointId";
	private static final String COLUMN_NAME_DATA_TYPE = "dataType";
	private static final String COLUMN_NAME_REPORT_INSTANCE_ID = "reportInstanceId";

	// @formatter:off
	public static final String REPORT_INSTANCE_DATA_INSERT = ""
			+ "insert into reportInstanceData "
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ "?, "
				+ COLUMN_NAME_D_POINT_VALUE + ", "
				+ COLUMN_NAME_D_TS + " "
			+ "from pointValue where "
				+ COLUMN_NAME_DATA_POINT_ID + "=? "
			+ "and "
				+ COLUMN_NAME_DATA_TYPE + "=? ";

	private static final String REPORT_INSTANCE_DATA_SELECT = ""
			+ "select "
				+ "rd." + COLUMN_NAME_D_POINT_VALUE + ", "
				+ "rda." + COLUMN_NAME_A_TEXT_POINT_VALUE_SHORT + ", "
				+ "rda." + COLUMN_NAME_A_TEXT_POINT_VALUE_LONG + ", "
				+ "rd." + COLUMN_NAME_D_TS + ", "
				+ "rda." + COLUMN_NAME_A_SOURCE_VALUE + ", "
			+ "from reportInstanceData rd left join reportInstanceDataAnnotations rda on "
				+ "rd." + COLUMN_NAME_D_POINT_VALUE_ID + "="
				+ "rda." + COLUMN_NAME_A_POINT_VALUE_ID + " "
			+ "and "
				+ "rd." + COLUMN_NAME_D_REPORT_INSTANCE_POINT_ID + "="
				+ "rda." + COLUMN_NAME_A_REPORT_INSTANCE_POINT_ID + " ";

	public static final String REPORT_INSTANCE_DATA_SELECT_WHERE = ""
				+ REPORT_INSTANCE_DATA_SELECT
			+ "where "
				+ "rd." + COLUMN_NAME_D_REPORT_INSTANCE_POINT_ID + "=? "
			+ "order by "
				+ "rd." + COLUMN_NAME_D_TS + " ";

	public static final String REPORT_INSTANCE_POINT_SELECT_MIN_MAX = ""
			+ "select min("
				+ "rd." + COLUMN_NAME_D_TS + "), "
			+ "max("
				+ "rd." + COLUMN_NAME_D_TS + ") "
			+ "from reportInstancePoints rp "
			+ "join reportInstanceData rd "
			+ "on "
				+ "rp." + COLUMN_NAME_ID + "="
				+ "rd." + COLUMN_NAME_A_REPORT_INSTANCE_POINT_ID + " "
			+ "where "
				+ "rp." + COLUMN_NAME_REPORT_INSTANCE_ID + "=? ";

	private static final String REPORT_INSTANCE_DATA_ANNOTATION_INSERT_FIRST = ""
			+ "insert into reportInstanceDataAnnotations ("
				+ COLUMN_NAME_A_POINT_VALUE_ID + ", "
				+ COLUMN_NAME_A_REPORT_INSTANCE_POINT_ID + ", "
				+ COLUMN_NAME_A_TEXT_POINT_VALUE_SHORT + ", "
				+ COLUMN_NAME_A_TEXT_POINT_VALUE_LONG + ", "
				+ COLUMN_NAME_A_SOURCE_VALUE + ") "
			+ "select "
				+ "rd." + COLUMN_NAME_D_POINT_VALUE_ID + ", "
				+ "rd." + COLUMN_NAME_D_REPORT_INSTANCE_POINT_ID + ", "
				+ "pva." + COLUMN_NAME_A_TEXT_POINT_VALUE_SHORT + ", "
				+ "pva." + COLUMN_NAME_A_TEXT_POINT_VALUE_LONG + ", ";


	private static final String REPORT_INSTANCE_DATA_ANNOTATION_INSERT_SECOND = ""
			+ "from reportInstanceData rd "
			+ "join reportInstancePoints rp on "
				+ "rd." + COLUMN_NAME_D_REPORT_INSTANCE_POINT_ID + "="
				+ "rp." + COLUMN_NAME_ID + " "
			+ "join pointValueAnnotations pva on "
				+ "rd." + COLUMN_NAME_D_POINT_VALUE_ID + "="
				+ "pva." + COLUMN_NAME_A_POINT_VALUE_ID + " "
			+ "left join users u on "
				+ "pva." + COLUMN_NAME_A_SOURCE_TYPE + "=1 "
			+ "and "
				+ "pva." + COLUMN_NAME_A_SOURCE_ID + "="
				+ "u." + COLUMN_NAME_ID + " "
			+ "where "
				+ "rp." + COLUMN_NAME_ID + "=?";


	// @formatter:on

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(Object[] params, final int reportPointId, final String timestampSql) {

		final Object [] allParams = new Object[]{reportPointId, params};

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(REPORT_INSTANCE_DATA_INSERT + timestampSql, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(allParams).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}



	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insertReportInstanceDataAnnotations(String annotationCase, final int reportPointId) {


		final String template = REPORT_INSTANCE_DATA_ANNOTATION_INSERT_FIRST + annotationCase + REPORT_INSTANCE_DATA_ANNOTATION_INSERT_SECOND;

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(template, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[]{reportPointId}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

}
