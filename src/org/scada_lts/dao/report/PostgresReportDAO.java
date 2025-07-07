package org.scada_lts.dao.report;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.report.ReportVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.QueryArgs;
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

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.scada_lts.utils.ReportDaoUtils.searchQuery;


public class PostgresReportDAO implements IReportDAO {

	private static final Log LOG = LogFactory.getLog(PostgresReportDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_DATA = "data";

	// @formatter:off
	private static final String REPORT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_DATA + ", "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_NAME + " "
			+ "from reports ";

	private static final String REPORT_INSERT = ""
			+ "insert into reports ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DATA + ") "
			+ "values (?,?,?,?) ";

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

	private static final String REPORT_SELECT_WHERE_XID = ""
			+ REPORT_SELECT
			+ "where "
			+ COLUMN_NAME_XID + "=? ";

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
			byte[] dataBytes = rs.getBytes(COLUMN_NAME_DATA);
			ReportVO report = (ReportVO) new SerializationData().readObject(new ByteArrayInputStream(dataBytes));
			report.setId(rs.getInt(COLUMN_NAME_ID));
			report.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
			report.setName(rs.getString(COLUMN_NAME_NAME));
			report.setXid(rs.getString(COLUMN_NAME_XID));
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

	public ReportVO getReport(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReport(String xid) xid:" + xid);
		}

		ReportVO reportVO;
		try {
			reportVO = DAO.getInstance().getJdbcTemp().queryForObject(REPORT_SELECT_WHERE_XID, new Object[]{xid}, new ReportRowMapper());
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
		return search(Common.NEW_ID, query);
	}

	public List<ReportVO> search(int userId, Map<String, String> query) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("search(int userId, Map<String, String> query)");
		}
		QueryArgs sql = searchQuery(userId, query, REPORT_SELECT);
		return DAO.getInstance().getJdbcTemp().query(sql.getQuery(), sql.getArgs(), new ReportRowMapper());
	}

	public List<ReportVO> getReports(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReports(int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().query(REPORT_SELECT_WHERE_USER_ID_ORDER, new Object[]{userId}, new ReportRowMapper());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public int insert(final ReportVO report) {
		if (LOG.isTraceEnabled())
			LOG.trace("insert(report): " + report);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(con -> {
			PreparedStatement ps = con.prepareStatement(REPORT_INSERT, new String[]{"id"});
			ps.setString(1, report.getXid());
			ps.setInt(2, report.getUserId());
			ps.setString(3, report.getName());
			byte[] bytes = new SerializationData().writeObject(report).readAllBytes();
			ps.setBytes(4, bytes);
			return ps;
		}, keyHolder);

        return ((Number) keyHolder.getKeys().get("id")).intValue();
	}


	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(final ReportVO report) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(final ReportVO report) report:" + report.toString());
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_UPDATE, ps -> {
			ps.setInt(1, report.getUserId());
			ps.setString(2, report.getName());
			ByteArrayInputStream bais = new SerializationData().writeObject(report);
			ps.setBytes(3, bais.readAllBytes());
			ps.setInt(4, report.getId());
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(REPORT_DELETE, new Object[]{id});
	}

}
