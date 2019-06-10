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

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.util.Tuple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DataPointUser DAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class DataPointUserDAO {

	private static final Log LOG = LogFactory.getLog(DataPointUserDAO.class);

	private static final String COLUMN_NAME_ID = "userId";
	private static final String COLUMN_NAME_DP_ID = "dataPointId";
	private static final int COLUMN_INDEX_DP_ID = 1;
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final int COLUMN_INDEX_USER_ID = 2;
	private static final String COLUMN_NAME_PERMISSION = "permission";
	private static final int COLUMN_INDEX_PERMISSION = 3;

	// @formatter:off
	private static final String DATA_POINT_USER_SELECT_WHERE_DP_ID = ""
			+ "select "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_PERMISSION + " "
			+ "from dataPointUsers where "
				+ COLUMN_NAME_DP_ID + "=? ";

	private static final String DATA_POINT_USER_SELECT_WHERE_USER_ID = ""
			+ "select "
				+ COLUMN_NAME_DP_ID + ", "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_PERMISSION + " "
			+ "from dataPointUsers where "
				+ COLUMN_NAME_ID + "=? ";


	private static final String DATA_POINT_USER_INSERT = ""
			+ "insert into dataPointUsers values (?,?,?) ";


	private static final String DATA_POINT_USER_DELETE = ""
			+ "delete from dataPointUsers where "
				+ COLUMN_NAME_USER_ID + "=? ";

	private static final String DATA_POINT_USER_DELETE_WHERE_DATA_POINT_ID = ""
			+ "delete from dataPointUsers where "
				+ COLUMN_NAME_DP_ID + "=? ";

	// @formatter:on

	public List<Tuple<Integer, Integer>> getDataPointUsers(final int dataPointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointUsers(final int dataPointId) dataPointId:" + dataPointId);
		}

		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_USER_SELECT_WHERE_DP_ID, new Object[]{dataPointId}, new RowMapper<Tuple<Integer, Integer>>() {
			@Override
			public Tuple<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Tuple<Integer, Integer>(rs.getInt(COLUMN_NAME_DP_ID), rs.getInt(COLUMN_NAME_USER_ID));
			}
		});
	}

	public List<DataPointAccess> getDataPointAccessList(final int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointAccessList(final int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_USER_SELECT_WHERE_USER_ID, new Object[]{userId}, new RowMapper<DataPointAccess>() {
			@Override
			public DataPointAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataPointAccess dataPointAccess = new DataPointAccess();
				dataPointAccess.setDataPointId(rs.getInt(COLUMN_NAME_DP_ID));
				dataPointAccess.setPermission(rs.getInt(COLUMN_NAME_PERMISSION));
				return dataPointAccess;
			}
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void insert(final List<Tuple<Integer, Integer>> ups, final int dataPointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final List<Tuple<Integer, Integer>> ups, final int dataPointId) ups:" + ups.toString() + ", dataPointId:" + dataPointId);
		}

		DAO.getInstance().getJdbcTemp().batchUpdate(DATA_POINT_USER_INSERT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, dataPointId);
				ps.setInt(2, ups.get(i).getElement1());
				ps.setInt(3, ups.get(i).getElement2());
			}

			@Override
			public int getBatchSize() {
				return ups.size();
			}
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void insertPermissions(final User user) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insertPermissions(final User user) user:" + user);
		}

		DAO.getInstance().getJdbcTemp().batchUpdate(DATA_POINT_USER_INSERT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(COLUMN_INDEX_DP_ID, user.getDataPointPermissions().get(i).getDataPointId());
				ps.setInt(COLUMN_INDEX_USER_ID, user.getId());
				ps.setInt(COLUMN_INDEX_PERMISSION, user.getDataPointPermissions().get(i).getPermission());
			}

			@Override
			public int getBatchSize() {
				return user.getDataPointPermissions().size();
			}
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void delete(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(DATA_POINT_USER_DELETE, new Object[]{userId});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void deleteWhereDataPointId(int dataPointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteWhereDataPointId(int dataPointId) datapointId:" + dataPointId);
		}

		DAO.getInstance().getJdbcTemp().update(DATA_POINT_USER_DELETE_WHERE_DATA_POINT_ID, new Object[]{dataPointId});
	}

}
