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

	private static final String COLUMN_NAME_DP_ID = "dataPointId";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_PERMISSION = "permission";

	private static final String DATA_POINT_USER_SELECT = ""
			+ "select "
				+ COLUMN_NAME_USER_ID + ", "
				+ COLUMN_NAME_PERMISSION + " "
			+ "from dataPointUsers where "
				+ COLUMN_NAME_DP_ID + "=? ";

	private static final String DATA_POINT_USER_INSERT = ""
			+ "insert into dataPointUsers values (?,?,?) ";

	public List<Tuple<Integer, Integer>> getDataPointUsers(final int dataPointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointUsers(final int dataPointId) dataPointId:" + dataPointId);
		}

		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_USER_SELECT, new Object[]{dataPointId}, new RowMapper<Tuple<Integer,Integer>>() {
			@Override
			public Tuple<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Tuple<Integer, Integer>(rs.getInt(COLUMN_NAME_DP_ID), rs.getInt(COLUMN_NAME_USER_ID));
			}
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void insert(final List<Tuple<Integer, Integer>> ups, final int dataPointId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final List<Tuple<Integer, Integer>> ups, final int dataPointId) ups:" + ups.toString() + ", dataPointId:"+ dataPointId);
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
}
