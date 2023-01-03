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
package org.scada_lts.permissions.migration.dao;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.impl.DataSourceDAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class OnlyMigrationDataSourceDAO extends DataSourceDAO {

	private static final Log LOG = LogFactory.getLog(OnlyMigrationDataSourceDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_DS_TYPE = "dataSourceType";
	private static final String COLUMN_NAME_DATA = "data";

	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final int COLUMN_INDEX_USER_ID = 2;
	private static final String COLUMN_NAME_DS_USER_ID = "dataSourceId";
	private static final int COLUMN_INDEX_DS_USER_ID = 1;

	private static final String COLUMN_NAME_EH_EVENT_TYPE_ID = "eventTypeId";
	private static final String COLUMN_NAME_EH_EVENT_TYPE_REF = "eventTypeRef1";

	private static final String COLUMN_NAME_USER_PROFILE_ID = "userProfileId";

	// @formatter:off
	private static final String DATA_SOURCE_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DATA + " "
			+ "from dataSources ";

	private static final String DATA_SOURCE_DS_SELECT = ""
			+ "select "
			+ "ds." + COLUMN_NAME_ID + ", "
			+ "ds." + COLUMN_NAME_XID + ", "
			+ "ds." + COLUMN_NAME_NAME + ", "
			+ "ds." + COLUMN_NAME_DATA + " "
			+ "from dataSources ds ";

	private static final String DATA_SOURCE_IDENTIFIER_SELECT = ""
			+ "select "
			+ "ds." + COLUMN_NAME_ID + ", "
			+ "ds." + COLUMN_NAME_XID + ", "
			+ "ds." + COLUMN_NAME_NAME + " "
			+ "from dataSources ds ";

	private static final String DATA_SOURCE_PLC_SELECT = "" +
			"SELECT DISTINCT " +
			"ds." + COLUMN_NAME_ID + ", " +
			"ds." + COLUMN_NAME_XID + ", " +
			"ds." + COLUMN_NAME_NAME + ", " +
			"ds." + COLUMN_NAME_DATA + " " +
			"FROM dataSources ds " +
			"JOIN dataPoints dp ON " +
			"dp.dataSourceId = ds." + COLUMN_NAME_ID + " " +
			"WHERE dp.plcAlarmLevel>0";


	private static final String DATA_SOURCE_SELECT_WHERE_ID = ""
				+ DATA_SOURCE_SELECT
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String DATA_SOURCE_SELECT_WHERE_XID = ""
			+ DATA_SOURCE_SELECT
			+ "where "
				+ COLUMN_NAME_XID + "=? ";

	private static final String DATA_SOURCE_INSERT = ""
			+ "insert into dataSources ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DS_TYPE + ", "
				+ COLUMN_NAME_DATA + ") "
			+ "values (?,?,?,?)";

	private static final String DATA_SOURCE_UPDATE = ""
			+ "update dataSources set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_NAME + "=?, "
				+ COLUMN_NAME_DATA + " =? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String DATA_SOURCE_DELETE_WHERE_ID = ""
			+ "delete from dataSources where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String DATA_SOURCE_USER_SELECT_WHERE_DS_ID = ""
			+ "select "
				+ COLUMN_NAME_USER_ID + " "
			+ "from dataSourceUsers where "
				+ COLUMN_NAME_DS_USER_ID + "=? ";

	private static final String DATA_SOURCE_USER_SELECT_WHERE_USER_ID = ""
			+ "select "
				+ COLUMN_NAME_DS_USER_ID + " "
			+ "from dataSourceUsers where "
				+ COLUMN_NAME_USER_ID + "=? ";

	private static final String DATA_SOURCE_SELECT_WHERE_LIKE_NAME = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DATA + " "
			+ "from dataSources where name like ?";

	private static final String DATA_SOURCE_USER_INSERT = ""
			+ "insert into dataSourceUsers values (?,?) ";

	private static final String DATA_SOURCE_USER_DELETE_WHERE_DS_ID = ""
			+ "delete from dataSourceUsers where "
				+ COLUMN_NAME_DS_USER_ID + "=? ";

	private static final String DATA_SOURCE_USER_DELETE_WHERE_USER_ID = ""
			+ "delete from dataSourceUsers where "
				+ COLUMN_NAME_USER_ID + "=? ";

	private static final String EVENT_HANDLER_DELETE = ""
			+ "delete from eventHandlers where "
				+ COLUMN_NAME_EH_EVENT_TYPE_ID + "="
			+ EventType.EventSources.DATA_SOURCE
			+ " and "
				+ COLUMN_NAME_EH_EVENT_TYPE_REF + "=? ";

	private static final String DATA_SOURCE_USERS_DELETE_DATA_SOURCE_ID_AND_USER_ID = ""
			+"delete "
			+ "from "
			+ "dataSourceUsers "
			+ "where "
			+ COLUMN_NAME_DS_USER_ID+"=? "
			+ "and "
			+ COLUMN_NAME_USER_ID+"=?";

	private static final String DATA_SOURCE_USERS_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE=""
			+"insert dataSourceUsers ("
			+COLUMN_NAME_DS_USER_ID+","
			+COLUMN_NAME_USER_ID+")"
			+ " values (?,?) ON DUPLICATE KEY UPDATE " +
			COLUMN_NAME_DS_USER_ID + "=?";

	private static final String DATA_SOURCE_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID = ""
			+ "select "
			+ COLUMN_NAME_DS_USER_ID+ ", "
			+ COLUMN_NAME_USER_PROFILE_ID + " "
			+ "from "
			+ "dataSourceUsersProfiles "
			+ "where "
			+ COLUMN_NAME_USER_PROFILE_ID+ "=?";

	private static final String DATA_SOURCE_USERS_SELECT_BASE_ON_USER_ID = ""
			+"select "
			+ COLUMN_NAME_DS_USER_ID+", "
			+ COLUMN_NAME_USER_ID+" "
			+ "from "
			+ "dataSourceUsers "
			+ "where "
			+ COLUMN_NAME_USER_ID+"=?";

	private static final String DATA_SOURCE_FILTER_BASE_ON_USER_ID_ORDER_BY_NAME = " "
			+ "ds." + COLUMN_NAME_ID + " in (select dsu." + COLUMN_NAME_DS_USER_ID +  " from dataSourceUsers dsu where dsu."+COLUMN_NAME_USER_ID+"=?) "
			+ "order by ds." + COLUMN_NAME_NAME;

	// @formatter:on

	private class DataSourceRowMapper implements RowMapper<DataSourceVO<?>> {

		@Override
		public DataSourceVO<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataSourceVO dataSourceVO = (DataSourceVO) new SerializationData().readObject(rs.getBinaryStream(COLUMN_NAME_DATA));
			dataSourceVO.setId(rs.getInt(COLUMN_NAME_ID));
			dataSourceVO.setXid(rs.getString(COLUMN_NAME_XID));
			dataSourceVO.setName(rs.getString(COLUMN_NAME_NAME));
			return dataSourceVO;
		}
	}

	static class DataSourceNameComparator implements Comparator<DataSourceVO<?>> {

		@Override
		public int compare(DataSourceVO<?> ds1, DataSourceVO<?> ds2) {
			if (ds1.getName() == null || ds1.getName().trim().length() == 0) {
				return -1;
			}
			return ds1.getName().compareToIgnoreCase(ds2.getName());
		}
	}

	public List<DataSourceVO<?>> getDataSources() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataSources()");
		}

		List<DataSourceVO<?>> objList = DAO.getInstance().getJdbcTemp().query(DATA_SOURCE_SELECT, new DataSourceRowMapper());
		Collections.sort(objList, new DataSourceNameComparator());
		return objList;
	}

	public List<DataSourceVO<?>> getDataSourcesPlc() {
		List<DataSourceVO<?>> list = DAO.getInstance().getJdbcTemp().query(DATA_SOURCE_PLC_SELECT, new DataSourceRowMapper());
		return list;
	}

	public List<DataSourceVO<?>> getDataSourceBaseOfName( String partOfNameDS) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataSources()");
		}

		List<DataSourceVO<?>> objList = DAO.getInstance().getJdbcTemp().query(
				DATA_SOURCE_SELECT_WHERE_LIKE_NAME,
				new Object[]{String.format("%%%s%%",partOfNameDS)},
				new DataSourceRowMapper());

		return objList;
	}

	public List<Integer> getDataSourceUsersId(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataSourceUsersId(int id) id:" + id);
		}

		return DAO.getInstance().getJdbcTemp().queryForList(DATA_SOURCE_USER_SELECT_WHERE_DS_ID, new Object[]{id}, Integer.class);
	}

	public List<Integer> getDataSourceIdFromDsUsers(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataSourceId(int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().queryForList(DATA_SOURCE_USER_SELECT_WHERE_USER_ID, new Object[]{userId}, Integer.class);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void batchInsert(final List<Integer> userIds, final int toDataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("batchInsert(final List<Integer> userIds, final int toDataSourceId) userIds:" + userIds.toString() + ", toDataSourceId:" + toDataSourceId);
		}

		DAO.getInstance().getJdbcTemp().batchUpdate(DATA_SOURCE_USER_INSERT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(COLUMN_INDEX_DS_USER_ID, toDataSourceId);
				ps.setInt(COLUMN_INDEX_USER_ID, userIds.get(i));
			}

			@Override
			public int getBatchSize() {
				return userIds.size();
			}
		});
	}

	public DataSourceVO<?> getDataSource(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataSource(int id) id:" + id);
		}


		return DAO.getInstance().getJdbcTemp().queryForObject(DATA_SOURCE_SELECT_WHERE_ID, new Object[]{id}, new DataSourceRowMapper());
		
	}

	public DataSourceVO<?> getDataSource(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getDataSource(String xid) xid:" + xid);
		}
		try {
			return DAO.getInstance().getJdbcTemp().queryForObject(DATA_SOURCE_SELECT_WHERE_XID, new Object[]{xid}, new DataSourceRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
			
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public int insert(final DataSourceVO<?> dataSource) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(final DataSourceVO<?> dataSource): dataSource" + dataSource.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(DATA_SOURCE_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[]{
						dataSource.getXid(),
						dataSource.getName(),
						dataSource.getType().getId(),
						new SerializationData().writeObject(dataSource)
				}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void insertPermissions(final User user) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insertPermissions(final User user) user:" + user.toString());
		}

		DAO.getInstance().getJdbcTemp().batchUpdate(DATA_SOURCE_USER_INSERT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(COLUMN_INDEX_DS_USER_ID, user.getDataSourcePermissions().get(i));
				ps.setInt(COLUMN_INDEX_USER_ID, user.getId());
			}

			@Override
			public int getBatchSize() {
				return user.getDataSourcePermissions().size();
			}
		});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public int update(final DataSourceVO<?> dataSource) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(final DataSourceVO<?> dataSource): dataSource" + dataSource.toString());
		}

		DataSourceVO<?> oldDataSource = getDataSource(dataSource.getId());

		return DAO.getInstance().getJdbcTemp().update(DATA_SOURCE_UPDATE, new Object[]{
				dataSource.getXid(),
				dataSource.getName(),
				new SerializationData().writeObject(dataSource),
				dataSource.getId()}
		);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public int delete(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int dataSourceId): dataSourceId" + dataSourceId);
		}

		DAO.getInstance().getJdbcTemp().update(EVENT_HANDLER_DELETE, new Object[]{dataSourceId});
		DAO.getInstance().getJdbcTemp().update(DATA_SOURCE_USER_DELETE_WHERE_DS_ID, new Object[]{dataSourceId});
		return DAO.getInstance().getJdbcTemp().update(DATA_SOURCE_DELETE_WHERE_ID, new Object[]{dataSourceId});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void deleteDataSourceUser(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteDataSourceUser(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(DATA_SOURCE_USER_DELETE_WHERE_USER_ID, new Object[]{userId});
	}

	public List<Integer> selectDataSourcePermissions(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("selectWatchListPermissions(final int userId) userId:" + userId);
		}

		return DAO.getInstance().getJdbcTemp().query(DATA_SOURCE_USERS_SELECT_BASE_ON_USER_ID,
				new Object[]{userId}, (rs, rowNum) -> rs.getInt(COLUMN_NAME_DS_USER_ID));
	}

	public List<Integer> selectDataSourcePermissionsByProfileId(int profileId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("selectDataSourcePermissionsByProfileId(int profileId) profileId:" + profileId);
		}

		return DAO.getInstance().getJdbcTemp()
				.query(DATA_SOURCE_USERS_PROFILES_SELECT_BASE_ON_USERS_PROFILE_ID,
						new Object[]{profileId}, (rs, rowNum) -> rs.getInt(COLUMN_NAME_DS_USER_ID));
	}

	public int[] insertPermissions(int userId, List<Integer> toInsert) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("insertPermissions(int userId, List<WatchListAccess> toInsert) user:" + userId + "");
		}

		int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER};

		List<Object[]> batchArgs = toInsert.stream()
				.map(a -> new Object[] {a, userId, a})
				.collect(Collectors.toList());

		return DAO.getInstance().getJdbcTemp()
				.batchUpdate(DATA_SOURCE_USERS_INSERT_ON_DUPLICATE_KEY_UPDATE_ACCESS_TYPE, batchArgs, argTypes);
	}

	public int[] deletePermissions(int userId, List<Integer> toDelete) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deletePermissions(int userId, List<Integer> toDelete) user:" + userId);
		}

		int[] argTypes = {Types.INTEGER, Types.INTEGER };

		List<Object[]> batchArgs = toDelete.stream()
				.map(a -> new Object[] {a, userId})
				.collect(Collectors.toList());

		return DAO.getInstance().getJdbcTemp()
				.batchUpdate(DATA_SOURCE_USERS_DELETE_DATA_SOURCE_ID_AND_USER_ID, batchArgs, argTypes);
	}
}