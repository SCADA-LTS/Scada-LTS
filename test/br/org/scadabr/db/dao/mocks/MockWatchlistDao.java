package br.org.scadabr.db.dao.mocks;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.vo.WatchList;

public class MockWatchlistDao extends WatchListDao {

	public WatchList createNewWatchList(String name, int id, int userId) {
		WatchList vo = new WatchList();

		
		/*vo.setId(doInsert(
				"insert into watchLists (xid, id, name, userId) values (?,?,?,?)",
				new Object[] { name, id, name, userId }));*/

		return vo;
	}

	public WatchList getWatchList(int watchListId) {
		WatchList watchList = null;
		/*queryForObject(
				"select id, xid, userId, name from watchLists where id=?",
				new Object[] { watchListId }, new WatchListRowMapper());
		populateWatchlistData(watchList);*/
		return watchList;
	}

	class WatchListRowMapper implements GenericRowMapper<WatchList> {
		public WatchList mapRow(ResultSet rs, int rowNum) throws SQLException {
			WatchList wl = new WatchList();
			wl.setId(rs.getInt(1));
			return wl;
		}
	}
}
