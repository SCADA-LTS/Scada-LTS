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
package org.scada_lts.dao.watchlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericDaoCR;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;

/**
 * WatchList DAO
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
@Repository
public class WatchListDAO implements GenericDaoCR<WatchList> {
	
	private static final Log LOG = LogFactory.getLog(WatchListDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_USER_PROFILE_ID = "userProfileId";
	private static final String COLUMN_NAME_USER_ACCESS_TYPE = "accessType";
	
	//watchlistUser
	private static final String COLUMN_NAME_WLU_USER_ID = "userId";
	private static final String COLUMN_NAME_WLU_ACCESS_TYPE = "accessType";
	private static final String COLUMN_NAME_WLU_WATCHLIST_ID = "watchListId";
	
	private static final int COLUMN_INDEX_WLU_WATCH_LIST_ID = 1;
	private static final int COLUMN_INDEX_WLU_DATA_POINT_ID = 2;
	private static final int COLUMN_INDEX_WLU_SORT_ORDER = 3;
	
	
	//watchlistPoints 
	private static final String COLUMN_NAME_WLP_DATA_POINT_ID = "dataPointId";
	private static final String COLUMN_NAME_WLP_WATCHLIST_ID = "watchListId";
	private static final String COLUMN_NAME_WLP_SORT_ORDER = "sortOrder";
	
	private static final int COLUMN_INDEX_WLP_WATCH_LIST_ID = 1;
	private static final int COLUMN_INDEX_WLP_DATA_POINT_ID = 2;
	private static final int COLUMN_INDEX_WLP_SORT_ORDER = 3;
	
	//users
	private static final String COLUMN_NAME_USERS_SELECTED_WATCH_LIST = "selectedWatchList";
	private static final String COLUMN_NAME_USERS_ID = "id";
	
	// @formatter:off
	private static final String WATCH_LIST_SELECT_ORDER_BY_NAME = ""
			+ "select "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_NAME+" "
			+ "from "
				+ "watchLists "
			+ "order by "+COLUMN_NAME_NAME;
	
	private static final String WATCH_LIST_SELECT_BASE_ON_XID = ""
			+ "select "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_NAME+" "
			+ "from "
				+ "watchLists "
			+ "where "
				+ COLUMN_NAME_XID+"=?";
	
	private static final String WATCH_LIST_SELECT_BASE_ON_ID = ""
			+ "select "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_NAME+" "
			+ "from "
				+ "watchLists "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	
	private static final String WATCH_LIST_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_NAME+" "
			+ "from "
				+ "watchLists ";
	
	private static final String WATCH_LIST_INSERT = ""
			+ "insert watchLists ("
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_NAME+") "
			+ "values (?,?,?)";
	
	public static final String WATCH_LIST_FILTER_BASE_ON_USER_ID_USER_PROFILE_ORDERY_BY_NAME = " "
				+ COLUMN_NAME_USER_ID+"=? or "
				+ "id in (select watchListId from watchListUsersProfiles where "+COLUMN_NAME_USER_PROFILE_ID+"=?) or "
				+ "id in (select watchListId from watchListUsers where "+COLUMN_NAME_USER_ID+"=? and "+COLUMN_NAME_USER_ACCESS_TYPE+">0) "
			+ "order by name";
	
	private static final String WATCH_LIST_USERS_SELECT_BASE_ON_WATCH_LIST_ID = ""
			+"select "
				+ COLUMN_NAME_WLU_USER_ID+", "
				+ COLUMN_NAME_WLU_ACCESS_TYPE+" "
			+ "from "
				+ "watchListUsers "
			+ "where "
				+ COLUMN_NAME_WLU_WATCHLIST_ID+"=?";
	
	private static final String WATCH_LIST_POINTS_SELECT_BASE_ON_WATCH_LIST_ID=" "
			+"select "
				+ COLUMN_NAME_WLP_DATA_POINT_ID+" "
			+ "from "
				+ "watchListPoints "
			+ "where "
				+ COLUMN_NAME_WLP_WATCHLIST_ID+"=? "
			+ "order by "+COLUMN_NAME_WLP_SORT_ORDER;
	
	private static final String WATCH_LIST_FOR_USER_UPDATE = ""
			+"update users "
				+ "set "
					+ COLUMN_NAME_USERS_SELECTED_WATCH_LIST+"=? "
			+ "where "
				+ COLUMN_NAME_USERS_ID+"=?";
	
	private static final String WATCH_LIST_UPDATE = ""
			+ "update watchLists "
				+ "set "
					+ COLUMN_NAME_XID+"=?, "
					+ COLUMN_NAME_NAME+"=? "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String WATCH_LIST_POINTS_DELETE = ""
			+ "delete "
			+ "from "
				+ "watchListPoints "
			+ "where "
				+ COLUMN_NAME_WLP_WATCHLIST_ID+"=?";
	
	private static final String WATCH_LIST_POINTS_INSERT=""
			+ "insert watchListPoints ("
				+ COLUMN_NAME_WLP_WATCHLIST_ID+","
				+ COLUMN_NAME_WLP_DATA_POINT_ID+","
				+ COLUMN_NAME_WLP_SORT_ORDER+") "
			+ "values (?,?,?)";
	
	private static final String WATCH_LIST_USERS_DELETE = ""
			+"delete "
			+ "from "
				+ "watchListUsers "
			+ "where "
				+ COLUMN_NAME_WLP_WATCHLIST_ID+"=?";
	
	private static final String WATCH_LIST_USERS_INSERT=""
			+"insert watchListUsers ("
				+COLUMN_NAME_WLU_WATCHLIST_ID+","
				+COLUMN_NAME_WLU_USER_ID+","
				+COLUMN_NAME_WLU_ACCESS_TYPE+")"
			+ " values (?,?,?)";
	
	private static final String WATCH_LIST_DELETE_BASE_ON_ID = ""
			+ "delete "
				+ "from "
					+ "watchLists "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String WATCH_LIST_DELETE_USER_FROM_WATCH_LIST=""
			+"delete "
				+ "from "
					+ "watchListUsers "
			+ "where "
				+ COLUMN_NAME_WLU_WATCHLIST_ID+"=? and "
				+ COLUMN_NAME_WLU_USER_ID+"=?";

	private static final String WATCH_LIST_DELETE_POINT_WHERE_DATA_POINT = ""
			+ "delete from watchListPoints where "
				+ COLUMN_NAME_WLP_DATA_POINT_ID + " "
			+ "in ";
	
	// @formatter:on
	
	//RowMapper
	class WatchListRowMapper implements RowMapper<WatchList> {
		public WatchList mapRow(ResultSet rs, int rowNum) throws SQLException {
			WatchList wl = new WatchList();
			wl.setId(rs.getInt(COLUMN_NAME_ID));
			wl.setXid(rs.getString(COLUMN_NAME_XID));
			wl.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
			wl.setName(rs.getString(COLUMN_NAME_NAME));
			return wl;
		}
	}
	
	//TODO move to another for example ShareUser
	class WatchListUserRowMapper implements RowMapper<ShareUser> {
		public ShareUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ShareUser wlu = new ShareUser();
			wlu.setUserId(rs.getInt(COLUMN_NAME_WLU_USER_ID));
			wlu.setAccessType(rs.getInt(COLUMN_NAME_WLU_ACCESS_TYPE));
			return wlu;
		}
	}
				
	@Override
	public List<WatchList> findAll() {
		return  (List<WatchList>) DAO.getInstance().getJdbcTemp().query(WATCH_LIST_SELECT_ORDER_BY_NAME, new Object[] {}, new WatchListRowMapper());
	}

	@Override
	public WatchList findById(Object[] pk) {
		try {
			return (WatchList) DAO.getInstance().getJdbcTemp().queryForObject(WATCH_LIST_SELECT_BASE_ON_ID, pk, new WatchListRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public WatchList findByXId(String xid) {
		try {
			return (WatchList) DAO.getInstance().getJdbcTemp().queryForObject(WATCH_LIST_SELECT_BASE_ON_XID, new Object[] {xid}, new WatchListRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<WatchList> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		Object[] args;
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<WatchList>) DAO.getInstance().getJdbcTemp().query(WATCH_LIST_SELECT+" where "+ filter + myLimit, args, new WatchListRowMapper());
	
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(final WatchList entity) {
		
		if (LOG.isTraceEnabled()) {
		  LOG.trace(entity);
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
				
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(WATCH_LIST_INSERT, Statement.RETURN_GENERATED_KEYS);
					 				new ArgumentPreparedStatementSetter( new Object[] { 
					 						entity.getXid(),
					 						entity.getUserId(),
					 						entity.getName()
					 				}).setValues(ps);
					 				return ps;
					 			}
				}, keyHolder);
				
				
		return new Object[] {keyHolder.getKey().intValue()};
	}
	
	public List<ShareUser> getWatchListUsers(int watchListId) {
		return (List<ShareUser>) DAO.getInstance().getJdbcTemp().query(WATCH_LIST_USERS_SELECT_BASE_ON_WATCH_LIST_ID, new Object[] {watchListId}, new WatchListUserRowMapper());
	}
	
	public List<Integer> getPointsWatchList(int watchListId) {
		return (List<Integer>) DAO.getInstance().getJdbcTemp().queryForList(WATCH_LIST_POINTS_SELECT_BASE_ON_WATCH_LIST_ID, new Object[] { watchListId }, Integer.class );
	}
	
	public void updateUsers(int userId, int watchListId) {
		DAO.getInstance().getJdbcTemp().update(WATCH_LIST_FOR_USER_UPDATE, new Object[] {watchListId, userId});
	}
	
	public void update(WatchList watchList) {
		DAO.getInstance().getJdbcTemp().update(WATCH_LIST_UPDATE, new Object[] {watchList.getXid(), watchList.getName(), watchList.getId()});
	}
	
	//TODO rewrite because update is not delete All and add all.
	public void deleteWatchListPoints(int watchListId) {
		DAO.getInstance().getJdbcTemp().update(WATCH_LIST_POINTS_DELETE, new Object[] {watchListId});
	}
	
	//TODO rewrite
	public void deleteWatchListUsers(int watchListId) {
		DAO.getInstance().getJdbcTemp().update(WATCH_LIST_USERS_DELETE, new Object[] {watchListId});
	}
	
	public void deleteWatchList(int watchListId) {
		DAO.getInstance().getJdbcTemp().update(WATCH_LIST_DELETE_BASE_ON_ID, new Object[] {watchListId});
	}
	
	//TODO rewrite
	public void addPointsForWatchList(final WatchList watchList) {
		
		DAO.getInstance().getJdbcTemp().batchUpdate(
				WATCH_LIST_POINTS_INSERT,
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return watchList.getPointList().size();
					}
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setInt(COLUMN_INDEX_WLP_WATCH_LIST_ID, watchList.getId());
						ps.setInt(COLUMN_INDEX_WLP_DATA_POINT_ID, watchList.getPointList().get(i).getId());
						ps.setInt(COLUMN_INDEX_WLP_SORT_ORDER, i);
					}
				}
		);
	}
	
	//TODO rewrite
	public void addWatchListUsers(final WatchList watchList) {
		
		DAO.getInstance().getJdbcTemp().batchUpdate(
				WATCH_LIST_USERS_INSERT,
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return watchList.getWatchListUsers().size();
					}
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ShareUser wlu = watchList.getWatchListUsers().get(i);
						ps.setInt(COLUMN_INDEX_WLU_WATCH_LIST_ID, watchList.getId());
						ps.setInt(COLUMN_INDEX_WLU_DATA_POINT_ID, wlu.getUserId());
						ps.setInt(COLUMN_INDEX_WLU_SORT_ORDER, wlu.getAccessType());
					}
				});
	}
	
	public void deleteUserFromWatchList(int watchListId, int userId) {
		DAO.getInstance().getJdbcTemp().update(WATCH_LIST_DELETE_USER_FROM_WATCH_LIST, new Object[] {watchListId, userId});
	}
	
	public void deleteWatchListPoints(String dataPointIds) {
		String[] parameters = dataPointIds.split(",");

		StringBuilder queryBuilder = new StringBuilder(WATCH_LIST_DELETE_POINT_WHERE_DATA_POINT + "(?");
		for (int i = 1; i<parameters.length; i++) {
			queryBuilder.append(",?");
		}
		queryBuilder.append(")");

		DAO.getInstance().getJdbcTemp().update(queryBuilder.toString(), (Object[]) parameters);
	}
}
