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

import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierRowMapper;
import org.scada_lts.utils.PlcAlarmsUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * DAO for DataPoint
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PostgresDataPointDAO implements IDataPointDAO {
	
	private static final Log LOG = LogFactory.getLog(PostgresDataPointDAO.class);

	private static final String TABLE_NAME = "dataPoints";

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA_SOURCE_ID = "datasourceid";
	private static final String COLUMN_NAME_DATA = "data";
	private static final String COLUMN_NAME_PLC_ALARM_LEVEL = "plcalarmlevel";

	private static final String COLUMN_NAME_DS_NAME = "name";
	private static final String COLUMN_NAME_DATAPOINT_NAME = "pointname";
	private static final String COLUMN_NAME_DS_ID = "id";
	private static final String COLUMN_NAME_DS_XID = "xid";
	private static final String COLUMN_NAME_DS_DATA_SOURCE_TYPE = "datasourcetype";

	private static final String COLUMN_NAME_EVENT_TYPE_ID = "eventtypeid";
	private static final String COLUMN_NAME_EVENT_TYPE_REF1 = "eventtyperef1";

	private static final String COLUMN_NAME_DATA_POINT_ID = "datapointid";
	private static final String COLUMN_NAME_PERMISSION = "permission";
	private static final String COLUMN_NAME_USER_ID = "userId";

	//dataPointUsers
	private static final String COLUMN_NAME_DPU_USER_ID = "userId";
	private static final String COLUMN_NAME_DPU_ACCESS_TYPE = "permission";
	private static final String COLUMN_NAME_DPU_DATA_POINT_ID = "dataPointId";

	//userProfile
	private static final String COLUMN_NAME_UP_DATA_POINT_ID = "dataoointid";
	private static final String COLUMN_NAME_UP_USER_PRFILE_ID = "userprofileid";
	private static final String COLUMN_NAME_UP_PERMISSION = "permission";

	// @formatter:off
	private static final String DATA_POINT_SELECT = ""
			+ "select "
				+ "dp." + COLUMN_NAME_ID + ", "
				+ "dp." + COLUMN_NAME_XID + ", "
				+ "dp." + COLUMN_NAME_DATA_SOURCE_ID + ", "
				+ "dp." + COLUMN_NAME_DATA + ", "
				+ "ds." + COLUMN_NAME_DS_NAME + ", "
				+ "ds." + COLUMN_NAME_DS_XID + " as dsxid, "
				+ "ds." + COLUMN_NAME_DS_DATA_SOURCE_TYPE + " "
			+ "from datapoints dp join datasources ds on "
				+ "ds." + COLUMN_NAME_DS_ID + "="
				+ "dp." + COLUMN_NAME_DATA_SOURCE_ID + " ";

    private static final String DATA_POINT_IDENTIFIER_SELECT = ""
            + "select "
            + "dp." + COLUMN_NAME_ID + ", "
            + "dp." + COLUMN_NAME_XID + ", "
            + "dp." + COLUMN_NAME_DATAPOINT_NAME + ", "
			+ "dp." + COLUMN_NAME_DATA_SOURCE_ID + " "
            + "from dataPoints dp ";

	private static final String DATA_POINT_SELECT_PLC = "" +
			"SELECT " +
			"dp." + COLUMN_NAME_ID + ", " +
			"dp." + COLUMN_NAME_XID + ", " +
			"dp." + COLUMN_NAME_DATA_SOURCE_ID + ", " +
			"dp." + COLUMN_NAME_DATA + ", " +
			"dp." + COLUMN_NAME_DATAPOINT_NAME + ", " +
			"dp." + COLUMN_NAME_PLC_ALARM_LEVEL + " " +
			"FROM dataPoints dp ";

	private static final String DATA_POINT_SELECT_ID = ""
			+ "select DISTINCT "
				+ COLUMN_NAME_ID + " "
			+ "from dataPoints where "
				+ COLUMN_NAME_DATA_SOURCE_ID + "=? ";

	private static final String DATA_POINT_INSERT = ""
			+ "insert into dataPoints ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_DATAPOINT_NAME + ", "
				+ COLUMN_NAME_DATA_SOURCE_ID + ", "
				+ COLUMN_NAME_DATA + ", "
				+ COLUMN_NAME_PLC_ALARM_LEVEL
			+ ") "
			+ "values (?,?,?,?,?) ";

	private static final String DATA_POINT_UPDATE = ""
			+ "update dataPoints set "
				+ COLUMN_NAME_PLC_ALARM_LEVEL + "=?, "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_DATAPOINT_NAME + "=?, "
				+ COLUMN_NAME_DATA + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String DATA_POINT_DELETE = ""
			+ "delete from dataPoints where "
				+ COLUMN_NAME_ID;

	private static final String DELETE_EVENT_HANDLER_WHERE = ""
			+ "delete from eventHandlers where "
				+ COLUMN_NAME_EVENT_TYPE_ID + "="
				+ EventType.EventSources.DATA_POINT + " "
			+ "and "
				+ COLUMN_NAME_EVENT_TYPE_REF1;

	private static final String DATA_POINT_FILTER_BASE_ON_USER_ID_ORDER_BY_NAME = " "
			+ "dp." + COLUMN_NAME_ID + " in (select dpu."+COLUMN_NAME_DATA_POINT_ID+" from dataPointUsers dpu where dpu."+COLUMN_NAME_USER_ID+"=? and dpu."+COLUMN_NAME_PERMISSION+">0) "
			+ "order by dp." + COLUMN_NAME_DATAPOINT_NAME;


	//dataSourceUsers
	private static final String COLUMN_NAME_DSU_USER_ID = "userId";
	private static final String COLUMN_NAME_DSU_DATA_SOURCE_ID = "dataSourceId";

	//userProfile
	private static final String COLUMN_NAME_UP_DATA_SOURCE_ID = "dataSourceId";

	public static final String DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID_ORDER_BY_DP_NAME = ""
			+ "dp.id in (select dpu." + COLUMN_NAME_DPU_DATA_POINT_ID + " from dataPointUsers dpu where dpu." + COLUMN_NAME_DPU_USER_ID + "=? and dpu." + COLUMN_NAME_DPU_ACCESS_TYPE +">?) or "
			+ "dp.id in (select dpup." + COLUMN_NAME_UP_DATA_POINT_ID+" from dataPointUsersProfiles dpup where dpup." +COLUMN_NAME_UP_USER_PRFILE_ID + "=? and dpup."+COLUMN_NAME_UP_PERMISSION+">?) or "
			+ "dp.dataSourceId in (select dsu." + COLUMN_NAME_DSU_DATA_SOURCE_ID + " from dataSourceUsers dsu where dsu." + COLUMN_NAME_DSU_USER_ID + "=?) or "
			+ "dp.dataSourceId in (select dsup." + COLUMN_NAME_UP_DATA_SOURCE_ID + " from dataSourceUsersProfiles dsup where dsup." + COLUMN_NAME_UP_USER_PRFILE_ID + "=?) "
			+ "order by dp." + COLUMN_NAME_DATAPOINT_NAME;

	// @formatter:on

	private class DataPointRowMapper implements RowMapper<DataPointVO> {

		
		@Override
		public DataPointVO mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			byte[] dataBytes = resultSet.getBytes(COLUMN_NAME_DATA);
			ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
			DataPointVO dataPoint = (DataPointVO) new SerializationData().readObject(bais);

			dataPoint.setId(resultSet.getInt(COLUMN_NAME_ID));
			dataPoint.setXid(resultSet.getString(COLUMN_NAME_XID));
			dataPoint.setDataSourceId(resultSet.getInt(COLUMN_NAME_DATA_SOURCE_ID));
			dataPoint.setDataSourceXid( resultSet.getString("dsxid"));
			dataPoint.setDataSourceName(resultSet.getString(COLUMN_NAME_DS_NAME));
			dataPoint.setDataSourceTypeId(resultSet.getInt(COLUMN_NAME_DS_DATA_SOURCE_TYPE));
			
			return dataPoint;
		}
	}

	private class DataPointSimpleRowMapper implements RowMapper<DataPointVO> {


		@Override
		public DataPointVO mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			byte[] dataBytes = resultSet.getBytes(COLUMN_NAME_DATA);
			ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
			DataPointVO dataPoint = (DataPointVO) new SerializationData().readObject(bais);

			dataPoint.setId(resultSet.getInt(COLUMN_NAME_ID));
			dataPoint.setXid(resultSet.getString(COLUMN_NAME_XID));
			dataPoint.setDataSourceId(resultSet.getInt(COLUMN_NAME_DATA_SOURCE_ID));
			dataPoint.setName(resultSet.getString(COLUMN_NAME_DATAPOINT_NAME));

			return dataPoint;
		}
	}

	public DataPointVO getDataPoint(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoint(int id) id:" + id);
		}

		String templateSelectWhereId = DATA_POINT_SELECT + " where dp." + COLUMN_NAME_ID + "=? ";

		return DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new DataPointRowMapper());
		
	}

	public DataPointVO getDataPoint(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoint(String xid) xid:" + xid);
		}

		String templateSelectWhereXid = DATA_POINT_SELECT + " where dp." + COLUMN_NAME_XID + "=? ";

		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereXid, new Object[] {xid}, new DataPointRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		
	}

	public List<DataPointVO> getDataPoints() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoints()");
		}

		try {
			return DAO.getInstance().getJdbcTemp().query(DATA_POINT_SELECT, new DataPointRowMapper());
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
	
		return (List<DataPointVO>) DAO.getInstance().getJdbcTemp().query(DATA_POINT_SELECT+" where "+ filter + myLimit, args, new DataPointRowMapper());
	
	}

	public List<DataPointVO> getDataPoints(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoints(int dataSourceId) dataSourceId:" + dataSourceId);
		}

		String templateSelectWhereId = DATA_POINT_SELECT + " where dp." + COLUMN_NAME_DATA_SOURCE_ID + "=?";

		List<DataPointVO> dataPointList = DAO.getInstance().getJdbcTemp().query(templateSelectWhereId, new Object[] {dataSourceId}, new DataPointRowMapper());
		return dataPointList;
	}

	public List<DataPointVO> getDataPointByKeyword(String[] keywords) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointByKeyword(String search) search:" + keywords.toString());
		}
		String templateSelectWhereSearch = DATA_POINT_SELECT + " WHERE true ";
		List<String> args = new ArrayList<String>();
		for (String keyword : keywords) {
			templateSelectWhereSearch += " AND dp." + COLUMN_NAME_DATAPOINT_NAME + " LIKE ? ";
			args.add("%"+keyword+"%");
		}
		return DAO.getInstance().getJdbcTemp().query(templateSelectWhereSearch, new DataPointRowMapper(), args.toArray());
	}

	public List<DataPointVO> getPlcDataPoints(int dataSourceId) {

		String templateSelectPlcWhereId = DATA_POINT_SELECT_PLC + " where (dp." + COLUMN_NAME_DATA_SOURCE_ID + "=? AND dp.plcAlarmLevel>0)";
		List<DataPointVO> dataPointList = DAO.getInstance().getJdbcTemp().query(templateSelectPlcWhereId, new Object[] {dataSourceId}, new DataPointSimpleRowMapper());
		return dataPointList;

	}

	public List<Integer> getDataPointsIds(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPointIds(int dataSourceId) dataSourceId:" + dataSourceId);
		}

		return DAO.getInstance().getJdbcTemp().queryForList(DATA_POINT_SELECT_ID, new Object[] {dataSourceId}, Integer.class);
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
				PreparedStatement ps = connection.prepareStatement(DATA_POINT_INSERT, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, dataPoint.getXid());
				ps.setString(2, dataPoint.getName());
				ps.setInt(3, dataPoint.getDataSourceId());

				ByteArrayInputStream bais = new SerializationData().writeObject(dataPoint);
				ps.setBytes(4, bais.readAllBytes());

				ps.setInt(5, PlcAlarmsUtils.getPlcAlarmLevelByDataPoint(dataPoint));
				return ps;
			}
		}, keyHolder);

		@SuppressWarnings("unchecked")
		Map<String, Object> keys = keyHolder.getKeys();
		return ((Number) keys.get("id")).intValue();
	}

	/**
	 * Create DataPoint method v2
	 *
	 * DataPoint creation is the same but instead of
	 * basic version this one returns DataPointVO object.
	 *
	 * @param entity Object to create
	 * @return DataPointVO entity with unique ID number
	 */
	public DataPointVO create(DataPointVO entity) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(connection -> {
			PreparedStatement ps = connection.prepareStatement(DATA_POINT_INSERT, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, entity.getXid());
			ps.setString(2, entity.getName());
			ps.setInt(3, entity.getDataSourceId());

			ByteArrayInputStream bais = new SerializationData().writeObject(entity);
			ps.setBytes(4, bais.readAllBytes());

			ps.setInt(5, PlcAlarmsUtils.getPlcAlarmLevelByDataPointName(entity.getName()));

			return ps;
		}, keyHolder);

		Map<String, Object> keys = keyHolder.getKeys();
		entity.setId(((Number) keys.get("id")).intValue());

		return entity;
	}

	public DataPointVO getById(int id) throws EmptyResultDataAccessException {
		return getDataPoint(id);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int update(DataPointVO dataPoint) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(DataPointVO dataPoint) dataPoint:" + dataPoint);
		}
		try {
			ByteArrayInputStream bais = new SerializationData().writeObject(dataPoint);
			byte[] bytes = bais.readAllBytes();

			return DAO.getInstance().getJdbcTemp().update(
					DATA_POINT_UPDATE,
					PlcAlarmsUtils.getPlcAlarmLevelByDataPoint(dataPoint),
					dataPoint.getXid(),
					dataPoint.getName(),
					bytes,
					dataPoint.getId()
			);

		} catch (EmptyResultDataAccessException e) {
			LOG.error("Data Point entity with id= " + dataPoint.getId() + " does not exist!");
			return 0;
		} catch (Exception e) {
			LOG.error(e);
			return -1;
		}
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		String templateDeleteIn = DATA_POINT_DELETE + "=?";

		try {
			DAO.getInstance().getJdbcTemp().update(templateDeleteIn, id);
			return 0;
		} catch (Exception e) {
			String message = "FAILED ON DELETING DataPoint witj ID: ";
			LOG.error(message + id);
			LOG.error(e);
			return -1;
		}
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteWithIn(String dataPointIdList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(String dataPointIdList) dataPointIdList:" + dataPointIdList);
		}

		String[] idStrings  = dataPointIdList.split(",");
		List<Object> parameters = new ArrayList<>();

		for (String id : idStrings) {
			parameters.add(Integer.parseInt(id.trim()));
		}

		StringBuilder queryBuilder = new StringBuilder(DATA_POINT_DELETE + " in (?");
		for (int i = 1; i < parameters.size(); i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), parameters.toArray());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteEventHandler(String dataPointIdList) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteEventHandler(String dataPointIdList) dataPointIdList:" + dataPointIdList);
		}

		String[] stringIds = dataPointIdList.split(",");
		Integer[] intIds = new Integer[stringIds.length];
		for (int i = 0; i < stringIds.length; i++) {
			intIds[i] = Integer.valueOf(stringIds[i].trim());
		}

		StringBuilder queryBuilder = new StringBuilder(DELETE_EVENT_HANDLER_WHERE + " in (?");
		for (int i = 1; i < intIds.length; i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), (Object[]) intIds);
	}

	@Deprecated
	public List<DataPointVO> selectDataPointsWithAccess(final int userId) {
		return filtered(DATA_POINT_FILTER_BASE_ON_USER_ID_ORDER_BY_NAME, new Object[]{userId}, 0);
	}

	@Deprecated
	public List<ScadaObjectIdentifier> selectDataPointIdentifiersWithAccess(int userId) {
		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_IDENTIFIER_SELECT + " where " + DATA_POINT_FILTER_BASE_ON_USER_ID_ORDER_BY_NAME,
		new Object[] { userId },
		new ScadaObjectIdentifierRowMapper.Builder()
			.idColumnName(COLUMN_NAME_ID)
			.xidColumnName(COLUMN_NAME_XID)
			.nameColumnName(COLUMN_NAME_DATAPOINT_NAME)
			.build());
	}

	public List<DataPointVO> selectDataPointsWithAccess(int userId, int profileId) {
		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_SELECT + " where " + DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID_ORDER_BY_DP_NAME,
				new Object[] { userId, ShareUser.ACCESS_NONE, profileId ,ShareUser.ACCESS_NONE, userId, profileId },
				new DataPointRowMapper());
	}

	public List<ScadaObjectIdentifier> selectDataPointIdentifiersWithAccess(int userId, int profileId) {
		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_IDENTIFIER_SELECT + " where " + DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID_ORDER_BY_DP_NAME,
				new Object[] { userId, ShareUser.ACCESS_NONE, profileId, ShareUser.ACCESS_NONE, userId, profileId },
				new ScadaObjectIdentifierRowMapper.Builder()
						.idColumnName(COLUMN_NAME_ID)
						.xidColumnName(COLUMN_NAME_XID)
						.nameColumnName(COLUMN_NAME_DATAPOINT_NAME)
						.build());
	}

	public List<ScadaObjectIdentifier> findIdentifiers() {
		ScadaObjectIdentifierRowMapper mapper = new ScadaObjectIdentifierRowMapper.Builder()
				.nameColumnName(COLUMN_NAME_DATAPOINT_NAME)
				.idColumnName(COLUMN_NAME_ID)
				.xidColumnName(COLUMN_NAME_XID)
				.build();
		return DAO.getInstance().getJdbcTemp()
				.query(mapper.selectScadaObjectIdFrom(TABLE_NAME), mapper);
	}

	public List<ScadaObjectIdentifier> findIdentifiers(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("findIdentifiers(int dataSourceId) dataSourceId:" + dataSourceId);
		}

		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_IDENTIFIER_SELECT + " where dp." + COLUMN_NAME_DATA_SOURCE_ID + "=?", new Object[] {dataSourceId},
				new ScadaObjectIdentifierRowMapper.Builder()
						.idColumnName(COLUMN_NAME_ID)
						.xidColumnName(COLUMN_NAME_XID)
						.nameColumnName(COLUMN_NAME_DATAPOINT_NAME)
						.build());
	}

	public List<DataPointVO> getDataPoints(String dataSourceXid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataPoints(String dataSourceXid) dataSourceXid:" + dataSourceXid);
		}

		String templateSelectWhereXid = DATA_POINT_SELECT + " where ds." + COLUMN_NAME_DS_XID + "=?";
		return DAO.getInstance().getJdbcTemp().query(templateSelectWhereXid, new Object[] {dataSourceXid}, new DataPointRowMapper());
	}
}
