/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.WatchList;

/**
 * @author Matthew Lohbihler
 */
public class WatchListDao extends BaseDao {
	public String generateUniqueXid() {
		String str = generateUniqueXid(WatchList.XID_PREFIX, "watchLists");
		// System.out.println("generateUniqueXid >>> " + str);
		return str;
	}

	public boolean isXidUnique(String xid, int watchListId) {
		return isXidUnique(xid, watchListId, "watchLists");
	}

	/**
	 * Note: this method only returns basic watchlist information. No data
	 * points or share users.
	 * 
	 * @param userProfile
	 */
	public List<WatchList> getWatchLists(final int userId, int userProfile) {
		return query(
				"select id, xid, userId, name from watchLists " //
						+ "where userId=? or id in (select watchListId from watchListUsersProfiles where userProfileId=?) " //
						+ "or id in (select watchListId from watchListUsers where userId=? and accessType>0) "
						+ "order by name", new Object[] { userId, userProfile,
						userId }, new WatchListRowMapper());
	}

	/**
	 * Note: this method only returns basic watchlist information. No data
	 * points or share users.
	 */
	public List<WatchList> getWatchLists() {
		return query(
				"select id, xid, userId, name from watchLists order by name",
				new WatchListRowMapper());
	}

	public WatchList getWatchList(int watchListId) {
		// Get the watch lists.
		WatchList watchList = queryForObject(
				"select id, xid, userId, name from watchLists where id=?",
				new Object[] { watchListId }, new WatchListRowMapper());
		populateWatchlistData(watchList);
		return watchList;
	}

	public void populateWatchlistData(List<WatchList> watchLists) {
		for (WatchList watchList : watchLists)
			populateWatchlistData(watchList);
	}

	public void populateWatchlistData(WatchList watchList) {
		if (watchList == null)
			return;

		// Get the points for each of the watch lists.
		List<Integer> pointIds = queryForList(
				"select dataPointId from watchListPoints where watchListId=? order by sortOrder",
				new Object[] { watchList.getId() }, Integer.class);
		List<DataPointVO> points = watchList.getPointList();
		DataPointDao dataPointDao = new DataPointDao();
		for (Integer pointId : pointIds)
			points.add(dataPointDao.getDataPoint(pointId));

		setWatchListUsers(watchList);
	}

	/**
	 * Note: this method only returns basic watchlist information. No data
	 * points or share users.
	 */
	public WatchList getWatchList(String xid) {
		return queryForObject(
				"select id, xid, userId, name from watchLists where xid=?",
				new Object[] { xid }, new WatchListRowMapper(), null);
	}

	class WatchListRowMapper implements GenericRowMapper<WatchList> {
		public WatchList mapRow(ResultSet rs, int rowNum) throws SQLException {
			WatchList wl = new WatchList();
			wl.setId(rs.getInt(1));
			wl.setXid(rs.getString(2));
			wl.setUserId(rs.getInt(3));
			wl.setName(rs.getString(4));
			return wl;
		}
	}

	public void saveSelectedWatchList(int userId, int watchListId) {
		ejt.update("update users set selectedWatchList=? where id=?",
				new Object[] { watchListId, userId });
	}

	public WatchList createNewWatchList(WatchList watchList, int userId) {
		// System.out.println("userId >>> " + userId);
		watchList.setUserId(userId);
		String guxid = generateUniqueXid();
		// System.out.println("guxid >>> " + guxid);
		watchList.setXid(guxid);
		// System.out.println("watchList >>> " + watchList);
		if (Common.getEnvironmentProfile().getString("db.type")
				.equals("postgres")) {
			try {
				Connection conn = DriverManager
						.getConnection(
								Common.getEnvironmentProfile().getString(
										"db.url"),
								Common.getEnvironmentProfile().getString(
										"db.username"),
								Common.getEnvironmentProfile().getString(
										"db.password"));
				PreparedStatement preStmt = conn
						.prepareStatement("insert into watchLists (xid, userId, name) values (?,?,?)");
				preStmt.setString(1, watchList.getXid());
				preStmt.setInt(2, userId);
				preStmt.setString(3, watchList.getName());
				preStmt.executeUpdate();

				ResultSet resSEQ = conn.createStatement().executeQuery(
						"SELECT currval('watchlists_id_seq')");
				resSEQ.next();
				int id = resSEQ.getInt(1);

				conn.close();

				watchList.setId(id);

			} catch (SQLException ex) {
				ex.printStackTrace();
				watchList.setId(0);
			}
		} else {
			watchList
					.setId(doInsert(
							"insert into watchLists (xid, userId, name) values (?,?,?)",
							new Object[] { watchList.getXid(), userId,
									watchList.getName() }));
		}
		return watchList;
	}

	public void saveWatchList(final WatchList watchList) {
		final ExtendedJdbcTemplate ejt2 = ejt;
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					@SuppressWarnings("synthetic-access")
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus status) {
						if (watchList.getId() == Common.NEW_ID) {
							if (Common.getEnvironmentProfile()
									.getString("db.type").equals("postgres")) {
								try {
									Connection conn = DriverManager
											.getConnection(
													Common.getEnvironmentProfile()
															.getString("db.url"),
													Common.getEnvironmentProfile()
															.getString(
																	"db.username"),
													Common.getEnvironmentProfile()
															.getString(
																	"db.password"));
									PreparedStatement preStmt = conn
											.prepareStatement("insert into watchLists (xid, name, userId) values (?,?,?)");
									preStmt.setString(1, watchList.getXid());
									preStmt.setString(2, watchList.getName());
									preStmt.setInt(3, watchList.getUserId());
									preStmt.executeUpdate();

									ResultSet resSEQ = conn
											.createStatement()
											.executeQuery(
													"SELECT currval('watchlists_id_seq')");
									resSEQ.next();
									int id = resSEQ.getInt(1);

									conn.close();

									watchList.setId(id);

								} catch (SQLException ex) {
									ex.printStackTrace();
									watchList.setId(0);
								}
							} else {
								watchList
										.setId(doInsert(
												"insert into watchLists (xid, name, userId) values (?,?,?)",
												new Object[] {
														watchList.getXid(),
														watchList.getName(),
														watchList.getUserId() }));
							}
						} else {
							ejt2.update(
									"update watchLists set xid=?, name=? where id=?",
									new Object[] { watchList.getXid(),
											watchList.getName(),
											watchList.getId() });
						}
						ejt2.update(
								"delete from watchListPoints where watchListId=?",
								new Object[] { watchList.getId() });
						ejt2.batchUpdate(
								"insert into watchListPoints values (?,?,?)",
								new BatchPreparedStatementSetter() {
									public int getBatchSize() {
										return watchList.getPointList().size();
									}

									public void setValues(PreparedStatement ps,
											int i) throws SQLException {
										ps.setInt(1, watchList.getId());
										ps.setInt(2, watchList.getPointList()
												.get(i).getId());
										ps.setInt(3, i);
									}
								});

						saveWatchListUsers(watchList);
					}
				});
	}

	public void deleteWatchList(final int watchListId) {
		final ExtendedJdbcTemplate ejt2 = ejt;
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus status) {
						deleteWatchListUsers(watchListId);
						ejt2.update(
								"delete from watchListPoints where watchListId=?",
								new Object[] { watchListId });
						ejt2.update("delete from watchLists where id=?",
								new Object[] { watchListId });
					}
				});
	}

	//
	//
	// Watch list users
	//
	private void setWatchListUsers(WatchList watchList) {
		watchList
				.setWatchListUsers(query(
						"select userId, accessType from watchListUsers where watchListId=?",
						new Object[] { watchList.getId() },
						new WatchListUserRowMapper()));
	}

	class WatchListUserRowMapper implements GenericRowMapper<ShareUser> {
		public ShareUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ShareUser wlu = new ShareUser();
			wlu.setUserId(rs.getInt(1));
			wlu.setAccessType(rs.getInt(2));
			return wlu;
		}
	}

	void deleteWatchListUsers(int watchListId) {
		ejt.update("delete from watchListUsers where watchListId=?",
				new Object[] { watchListId });
	}

	void saveWatchListUsers(final WatchList watchList) {
		// Delete anything that is currently there.
		deleteWatchListUsers(watchList.getId());

		// Add in all of the entries.
		ejt.batchUpdate("insert into watchListUsers values (?,?,?)",
				new BatchPreparedStatementSetter() {
					@Override
					public int getBatchSize() {
						return watchList.getWatchListUsers().size();
					}

					@Override
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ShareUser wlu = watchList.getWatchListUsers().get(i);
						ps.setInt(1, watchList.getId());
						ps.setInt(2, wlu.getUserId());
						ps.setInt(3, wlu.getAccessType());
					}
				});
	}

	public void removeUserFromWatchList(int watchListId, int userId) {
		ejt.update(
				"delete from watchListUsers where watchListId=? and userId=?",
				new Object[] { watchListId, userId });
	}
}
