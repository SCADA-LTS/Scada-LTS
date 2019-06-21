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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.DataPointVO;

/**
 * DAO for DataPoint
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class DataPointDAO {
	
	private static final Log LOG = LogFactory.getLog(DataPointDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA_SOURCE_ID = "dataSourceId";
	private static final String COLUMN_NAME_DATA = "data";
	private static final String COLUMN_NAME_DS_NAME = "name";
	private static final String COLUMN_NAME_DS_DATA_SOURCE_TYPE = "dataSourceType";

	// @formatter:off
	private static final String DATA_POINT_SELECT_WHERE_DPID = "select dp.id, dp.xid, dp.dataSourceId, dp.data, ds.name, ds.xid as dsxid, ds.dataSourceType "
			+ "from dataPoints dp join dataSources ds on ds.id=dp.dataSourceId where dp.id=?";
	private static final String DATA_POINT_SELECT_ =
			"select dp.id, dp.xid, dp.dataSourceId, dp.data, ds.name, ds.xid as dsxid, ds.dataSourceType "
			+ "from dataPoints dp join dataSources ds on ds.id=dp.dataSourceId";
	private static final String GET_DATA_POINTS_BY_DATASOURCEID_ = "select " +
			"dp.id, dp.xid, dp.dataSourceId, dp.data, ds.name, ds.xid as dsxid, ds.dataSourceType "
			+ "from dataPoints dp join dataSources ds on ds.id=dp.dataSourceId where dp.dataSourceId=?";
	private static final String DATA_POINT_SELECT_ID_ = "select DISTINCT id from dataPoints where dataSourceId=? ";
	private static final String DATA_POINT_INSERT_="insert into dataPoints (xid,dataSourceId,data) values (?,?,?) ";
	private static final String DATA_POINT_UPDATE_= "update dataPoints set xid=?,data=? where id=? ";
	private static final String DATA_POINT_DELETE_ = "delete from dataPoints where id";
	private static final String DELETE_EVENT_HANDLER_WHERE_ = "delete from eventHandlers where eventTypeId="
			+ EventType.EventSources.DATA_POINT + "and eventTypeRef1";

	// @formatter:on

	private class DataPointRowMapper implements RowMapper<DataPointVO> {

		
		@Override
		public DataPointVO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			
			DataPointVO dataPoint = (DataPointVO) new SerializationData().readObject(resultSet.getBlob(COLUMN_NAME_DATA).getBinaryStream());
			
			dataPoint.setId(resultSet.getInt(COLUMN_NAME_ID));
			dataPoint.setXid(resultSet.getString(COLUMN_NAME_XID));
			dataPoint.setDataSourceId(resultSet.getInt(COLUMN_NAME_DATA_SOURCE_ID));
			dataPoint.setDataSourceXid( resultSet.getString("dsxid"));
			dataPoint.setDataSourceName(resultSet.getString(COLUMN_NAME_DS_NAME));
			dataPoint.setDataSourceTypeId(resultSet.getInt(COLUMN_NAME_DS_DATA_SOURCE_TYPE));
			
			return dataPoint;
		}
	}

	public DataPointVO getDataPoint(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoint(int id) id:" + id);
		}

		return DAO.getInstance().getJdbcTemp().queryForObject(DATA_POINT_SELECT_WHERE_DPID, new Object[] {id}, new DataPointRowMapper());
		
	}

	public DataPointVO getDataPoint(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoint(String xid) xid:" + xid);
		}


		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(
					"select dp.id, dp.xid, dp.dataSourceId, dp.data, ds.name, ds.xid as dsxid, ds.dataSourceType "
							+ "from dataPoints dp join dataSources ds on ds.id=dp.dataSourceId where dp.xid=? "
					, new Object[] {xid}, new DataPointRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		
	}

	public List<DataPointVO> getDataPoints() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoints()");
		}

		try {
			return DAO.getInstance().getJdbcTemp().query(DATA_POINT_SELECT_, new DataPointRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
		
	public List<DataPointVO> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		Object[] args;
		if (limit != GenericDaoCR.NO_LIMIT) {
			myLimit = GenericDaoCR.LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<DataPointVO>) DAO.getInstance().getJdbcTemp().query(DATA_POINT_SELECT_+" where "+ filter + myLimit, args, new DataPointRowMapper());
	
	}

	public List<DataPointVO> getDataPoints(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoints(int dataSourceId) dataSourceId:" + dataSourceId);
		}

		//datapoints
		return DAO.getInstance().getJdbcTemp().query(GET_DATA_POINTS_BY_DATASOURCEID_, new Object[] {dataSourceId}, new DataPointRowMapper());
	}

	public List<Integer> getDataPointsIds(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointIds(int dataSourceId) dataSourceId:" + dataSourceId);
		}

		return DAO.getInstance().getJdbcTemp().queryForList(DATA_POINT_SELECT_ID_, new Object[] {dataSourceId}, Integer.class);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final DataPointVO dataPoint) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final DataPointVO dataPoint) dataPoint:" + dataPoint);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(DATA_POINT_INSERT_, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
						dataPoint.getXid(),
						dataPoint.getDataSourceId(),
						new SerializationData().writeObject(dataPoint)
				}).setValues(ps);
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(DataPointVO dataPoint) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(DataPointVO dataPoint) dataPoint:" + dataPoint);
		}

		DAO.getInstance().getJdbcTemp().update(DATA_POINT_UPDATE_, new Object[] {
				dataPoint.getXid(),
				new SerializationData().writeObject(dataPoint),
				dataPoint.getId()
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update("delete from dataPoints where id=?", new Object[] {id});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteWithIn(String dataPointIdList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(String dataPointIdList) dataPointIdList:" + dataPointIdList);
		}

		String[] parameters = dataPointIdList.split(",");

		StringBuilder queryBuilder = new StringBuilder(DATA_POINT_DELETE_ + " in (?");
		for (int i = 1; i<parameters.length; i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), (Object[]) parameters);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteEventHandler(String dataPointIdList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteEventHandler(String dataPointIdList) dataPointIdList:" + dataPointIdList);
		}

		String[] parameters = dataPointIdList.split(",");

		StringBuilder queryBuilder = new StringBuilder(DELETE_EVENT_HANDLER_WHERE_ + " in (?");
		for (int i = 1; i<parameters.length; i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), (Object[]) parameters);
	}
}
