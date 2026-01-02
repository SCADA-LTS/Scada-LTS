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

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.serotonin.mango.view.ShareUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierRowMapper;
import org.scada_lts.utils.PlcAlarmsUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.DataPointVO;


/**
 * DAO for DataPoint
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class DataPointDAO {
	
	private static final Log LOG = LogFactory.getLog(DataPointDAO.class);

	private static final String TABLE_NAME = "dataPoints";

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA_SOURCE_ID = "dataSourceId";
	private static final String COLUMN_NAME_DATA = "data";
	private static final String COLUMN_NAME_PLC_ALARM_LEVEL = "plcAlarmLevel";

	private static final String COLUMN_NAME_DS_NAME = "name";
	private static final String COLUMN_NAME_DATAPOINT_NAME = "pointName";
	private static final String COLUMN_NAME_DS_ID = "id";
	private static final String COLUMN_NAME_DS_XID = "xid";
	private static final String COLUMN_NAME_DS_DATA_SOURCE_TYPE = "dataSourceType";

	private static final String COLUMN_NAME_EVENT_TYPE_ID = "eventTypeId";
	private static final String COLUMN_NAME_EVENT_TYPE_REF1 = "eventTypeRef1";

	private static final String COLUMN_NAME_DATA_POINT_ID = "dataPointId";
	private static final String COLUMN_NAME_PERMISSION = "permission";
	private static final String COLUMN_NAME_USER_ID = "userId";

	//dataPointUsers
	private static final String COLUMN_NAME_DPU_USER_ID = "userId";
	private static final String COLUMN_NAME_DPU_ACCESS_TYPE = "permission";
	private static final String COLUMN_NAME_DPU_DATA_POINT_ID = "dataPointId";

	//userProfile
	private static final String COLUMN_NAME_UP_DATA_POINT_ID = "dataPointId";
	private static final String COLUMN_NAME_UP_USER_PRFILE_ID = "userProfileId";
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
			+ "from dataPoints dp join dataSources ds on "
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

	public static final String ORDER_BY_NAME = ""
			+ "order by dp." + COLUMN_NAME_DATAPOINT_NAME;

	public static final String DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID = ""
			+ "dp.id in (select dpu." + COLUMN_NAME_DPU_DATA_POINT_ID + " from dataPointUsers dpu where dpu." + COLUMN_NAME_DPU_USER_ID + "=? and dpu." + COLUMN_NAME_DPU_ACCESS_TYPE +">?) or "
			+ "dp.id in (select dpup." + COLUMN_NAME_UP_DATA_POINT_ID+" from dataPointUsersProfiles dpup where dpup." +COLUMN_NAME_UP_USER_PRFILE_ID + "=? and dpup."+COLUMN_NAME_UP_PERMISSION+">?) or "
			+ "dp.dataSourceId in (select dsu." + COLUMN_NAME_DSU_DATA_SOURCE_ID + " from dataSourceUsers dsu where dsu." + COLUMN_NAME_DSU_USER_ID + "=?) or "
			+ "dp.dataSourceId in (select dsup." + COLUMN_NAME_UP_DATA_SOURCE_ID + " from dataSourceUsersProfiles dsup where dsup." + COLUMN_NAME_UP_USER_PRFILE_ID + "=?) ";

	public static final String DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID_ORDER_BY_DP_NAME = ""
			+ DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID
			+ ORDER_BY_NAME;

    private static final String DATA_POINT_SELECT_ONLY_ID = ""
            + "select "
            + "dp." + COLUMN_NAME_ID + " "
            + "from dataPoints dp join dataSources ds on "
            + "ds." + COLUMN_NAME_DS_ID + "="
            + "dp." + COLUMN_NAME_DATA_SOURCE_ID + " ";

	public static final String DATA_POINT_NEXT = ""
			+ " STRCMP(CONCAT_WS(' - ', ds." + COLUMN_NAME_DS_NAME + ", dp." + COLUMN_NAME_DATAPOINT_NAME + "), ?) > 0 ORDER BY ds." + COLUMN_NAME_DS_NAME + " ASC, dp." + COLUMN_NAME_DATAPOINT_NAME + " ASC LIMIT 1 ";

	public static final String DATA_POINT_PREV = ""
			+ " STRCMP(CONCAT_WS(' - ', ds." + COLUMN_NAME_DS_NAME + ", dp." + COLUMN_NAME_DATAPOINT_NAME + "), ?) < 0 ORDER BY ds." + COLUMN_NAME_DS_NAME + " DESC, dp." + COLUMN_NAME_DATAPOINT_NAME + " DESC LIMIT 1 ";


	public static final String DATA_POINT_PREV_ON_USER_ID_USERS_PROFILE_ID = ""
			+ DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID + " AND "
			+ DATA_POINT_PREV;

	public static final String DATA_POINT_NEXT_ON_USER_ID_USERS_PROFILE_ID = ""
			+ DATA_POINT_FILTERED_BASE_ON_USER_ID_USERS_PROFILE_ID + " AND "
			+ DATA_POINT_NEXT;


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

	private class DataPointSimpleRowMapper implements RowMapper<DataPointVO> {


		@Override
		public DataPointVO mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			DataPointVO dataPoint = (DataPointVO) new SerializationData().readObject(resultSet.getBlob(COLUMN_NAME_DATA).getBinaryStream());

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

		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new DataPointRowMapper());
		} catch (EmptyResultDataAccessException e) {
			LOG.warn("datapoint does not exist for id: " + id + ", msg: " + e.getMessage());
			return null;
		}
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

	@Deprecated(since = "2.8.1")
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
				new ArgumentPreparedStatementSetter(new Object[] {
						dataPoint.getXid(),
						dataPoint.getName(),
						dataPoint.getDataSourceId(),
						new SerializationData().writeObject(dataPoint),
						PlcAlarmsUtils.getPlcAlarmLevelByDataPoint(dataPoint)
				}).setValues(ps);
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
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
			new ArgumentPreparedStatementSetter(new Object[]{
					entity.getXid(),
					entity.getName(),
					entity.getDataSourceId(),
					new SerializationData().writeObject(entity),
					PlcAlarmsUtils.getPlcAlarmLevelByDataPointName(entity.getName())
			}).setValues(ps);
			return ps;
		}, keyHolder);
		entity.setId(keyHolder.getKey().intValue());
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
			return DAO.getInstance().getJdbcTemp().update(
					DATA_POINT_UPDATE,
					PlcAlarmsUtils.getPlcAlarmLevelByDataPoint(dataPoint),
					dataPoint.getXid(),
					dataPoint.getName(),
					new SerializationData().writeObject(dataPoint),
					dataPoint.getId());
		} catch (EmptyResultDataAccessException e) {
			LOG.error("Data Point entity with id= " + dataPoint.getId() + " does not exists!");
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

		String[] parameters = dataPointIdList.split(",");

		StringBuilder queryBuilder = new StringBuilder(DATA_POINT_DELETE + " in (?");
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

		StringBuilder queryBuilder = new StringBuilder(DELETE_EVENT_HANDLER_WHERE + " in (?");
		for (int i = 1; i<parameters.length; i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), (Object[]) parameters);
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

	public List<DataPointVO> getDataPointsWithLimit(Set<Integer> excludeIds, int offset, int limit) {
		StringBuilder templateSelectWhereId = new StringBuilder(DATA_POINT_SELECT);
		List<String> args = new ArrayList<>();
		if(excludeIds != null && !excludeIds.isEmpty()) {
			templateSelectWhereId.append(" AND").append(" dp.")
					.append(COLUMN_NAME_ID)
					.append(" NOT IN (")
					.append("?, ".repeat(excludeIds.size() - 1))
					.append("?").append(") ");

			args.addAll(excludeIds.stream().map(Object::toString).collect(Collectors.toList()));
		}

		if(limit > 0) {
			templateSelectWhereId.append(" LIMIT ").append(limit);
			if (offset > 0)
				templateSelectWhereId.append(" OFFSET ").append(offset);
		}
		return DAO.getInstance().getJdbcTemp().query(templateSelectWhereId.toString(), new DataPointRowMapper(), args.toArray());
	}

	public List<DataPointVO> selectDataPoints(Set<Integer> ids) {
		if(ids.isEmpty()) {
			return Collections.emptyList();
		}
		return DAO.getInstance().getJdbcTemp().query(DATA_POINT_SELECT + " where " + " "
						+ "dp." + COLUMN_NAME_ID + " in (" + "?,".repeat(ids.size() - 1) + "?) "
						+ "order by dp." + COLUMN_NAME_DATAPOINT_NAME,
				ids.toArray(),
				new DataPointRowMapper());
	}

	public List<DataPointVO> getDataPointByKeywords(Set<String> keywords, Set<Integer> excludeIds, int offset, int limit) {
		if(keywords.isEmpty())
			return Collections.emptyList();
		StringBuilder templateSelectWhereSearch = new StringBuilder(DATA_POINT_SELECT + " WHERE true ");
		List<String> args = new ArrayList<>();
		for (String keyword : keywords) {
			templateSelectWhereSearch.append(" AND CONCAT(ds.")
					.append(COLUMN_NAME_DS_NAME)
					.append(", ' - ', dp.")
					.append(COLUMN_NAME_DATAPOINT_NAME)
					.append(")")
					.append(" LIKE ? ");
			args.add("%"+keyword+"%");
		}

		if(excludeIds != null && !excludeIds.isEmpty()) {
			templateSelectWhereSearch.append(" AND dp.")
					.append(COLUMN_NAME_ID)
					.append(" NOT IN (")
					.append("?, ".repeat(excludeIds.size() - 1))
					.append("?").append(") ");

			args.addAll(excludeIds.stream().map(Object::toString).collect(Collectors.toList()));
		}

		if(limit > 0) {
			templateSelectWhereSearch.append(" LIMIT ").append(limit);
			if (offset > 0)
				templateSelectWhereSearch.append(" OFFSET ").append(offset);
		}
		return DAO.getInstance().getJdbcTemp().query(templateSelectWhereSearch.toString(), new DataPointRowMapper(), args.toArray());
	}

    public int selectDataPointIdWithAccessPrev(int userId, int profileId, String name) {
        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(DATA_POINT_SELECT_ONLY_ID + " where " + DATA_POINT_PREV_ON_USER_ID_USERS_PROFILE_ID,
                    new Object[] { userId, ShareUser.ACCESS_NONE, profileId ,ShareUser.ACCESS_NONE, userId, profileId, name }, int.class);
        } catch (EmptyResultDataAccessException ex) {
            return -1;
        }
    }

    public int selectDataPointIdWithAccessNext(int userId, int profileId, String name) {
        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(DATA_POINT_SELECT_ONLY_ID + " where " + DATA_POINT_NEXT_ON_USER_ID_USERS_PROFILE_ID,
                    new Object[]{ userId, ShareUser.ACCESS_NONE, profileId, ShareUser.ACCESS_NONE, userId, profileId, name }, int.class);
        } catch (EmptyResultDataAccessException ex) {
            return -1;
        }
    }

    public int selectDataPointIdWithAccessPrev(String name) {
        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(DATA_POINT_SELECT_ONLY_ID + " where " + DATA_POINT_PREV,
                    new Object[] { name }, int.class);
        } catch (EmptyResultDataAccessException ex) {
            return -1;
        }
    }

    public int selectDataPointIdWithAccessNext(String name) {
        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(DATA_POINT_SELECT_ONLY_ID + " where " + DATA_POINT_NEXT,
                    new Object[]{ name }, int.class);
        } catch (EmptyResultDataAccessException ex) {
            return -1;
        }
    }
}
