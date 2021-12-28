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
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO for Report
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ReportDAO {

	private static final Log LOG = LogFactory.getLog(ReportDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_DATA = "data";

	// @formatter:off
	private static final String REPORT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_DATA + ", "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_NAME + " "
			+ "from reports ";

	private static final String REPORT_INSERT = ""
			+ "insert into reports ("
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DATA + ") "
			+ "values (?,?,?) ";

	private static final String REPORT_UPDATE = ""
			+ "update reports set "
				+ COLUMN_NAME_USER_ID + "=?, "
				+ COLUMN_NAME_NAME + "=?, "
				+ COLUMN_NAME_DATA + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String REPORT_DELETE = ""
			+ "delete from reports where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String REPORT_SELECT_WHERE_ID = ""
				+ REPORT_SELECT
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String REPORT_SELECT_WHERE_USER_ID_ORDER = ""
				+ REPORT_SELECT
			+ "where "
				+ COLUMN_NAME_USER_ID + "=? "
			+ "order by "
				+COLUMN_NAME_NAME;

	// @formatter:on

	private class ReportRowMapper implements RowMapper<ReportVO> {

		@Override
		public ReportVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportVO report = (ReportVO) new SerializationData().readObject(rs.getBlob(COLUMN_NAME_DATA).getBinaryStream());
			report.setId(rs.getInt(COLUMN_NAME_ID));
			report.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
			report.setName(rs.getString(COLUMN_NAME_NAME));
			return report;
		}
	}

	public ReportVO getReport(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReport(int id) id:" + id);
		}

		ReportVO reportVO;
		try {
			reportVO = DAO.getInstance().getJdbcTemp().queryForObject(REPORT_SELECT_WHERE_ID, new Object[]{id}, new ReportRowMapper());
		} catch (EmptyResultDataAccessException e) {
			reportVO = null;
		}
		return reportVO;
	}

	public List<ReportVO> getReports() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReports()");
		}

		return DAO.getInstance().getJdbcTemp().query(REPORT_SELECT, new ReportRowMapper());
	}

	public List<ReportVO> search(Map<String, String> query) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReports()");
		}
		StringBuilder sql = new StringBuilder();
		List<String> filterCondtions = new ArrayList<String>();
		List<Object> params = new ArrayList<Object>();
		if (query.containsKey("keywords") && !"".equals(query.get("keywords"))) {
			List<String> keywordConditions = new ArrayList<String>();

			for (String keyword : query.get("keywords").split(" ")) {
				keywordConditions.add("R.name LIKE ?");
				params.add("%" + keyword + "%");
			}

			filterCondtions.add(joinOr(keywordConditions));
		}

		sql.append("SELECT "
			+ COLUMN_NAME_DATA + ", "
			+ COLUMN_NAME_ID + ", "
			+ COLUMN_NAME_USER_ID + ", "
			+ COLUMN_NAME_NAME + " "
			+ "FROM reports R ");
		sql.append(" WHERE " + joinAnd(filterCondtions));

		return DAO.getInstance().getJdbcTemp().query(sql.toString(), new ReportRowMapper());
	}

	public List<ReportVO> getReports(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReports(int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().query(REPORT_SELECT_WHERE_USER_ID_ORDER, new Object[]{userId}, new ReportRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final ReportVO report) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final ReportVO report) report:" + report.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(REPORT_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[]{
						report.getUserId(),
						report.getName(),
						new SerializationData().writeObject(report)}
				).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(final ReportVO report) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(final ReportVO report) report:" + report.toString());
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_UPDATE, new Object[]{
				report.getUserId(),
				report.getName(),
				new SerializationData().writeObject(report),
				report.getId()}
		);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_DELETE, new Object[]{id});
	}

	public String joinAnd(List<String> conditions) {
		StringBuilder result = new StringBuilder();
		result.append(" 1 ");
		for (String c:conditions) {
			result.append(" AND (");
			result.append(c);
			result.append(") ");
		}
		return result.toString();
	}

	public String joinOr(List<String> conditions) {
		StringBuilder result = new StringBuilder();
		result.append(" 0 ");
		for (String c:conditions) {
			result.append(" OR (");
			result.append(c);
			result.append(") ");
		}
		return result.toString();
	}
}
