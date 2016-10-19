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
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.vo.WatchList;

/**
 * WatchList DAO
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class WatchListDAO implements GenericDaoCR<WatchList> {
	
	private static final Log LOG = LogFactory.getLog(WatchListDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_NAME = "name";
	
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
				
	@Override
	public List<WatchList> findAll() {
		return  (List<WatchList>) DAO.getInstance().getJdbcTemp().query(WATCH_LIST_SELECT_ORDER_BY_NAME, new Object[] {}, new WatchListRowMapper());
	}

	@Override
	public WatchList findById(Object[] pk) {
		return (WatchList) DAO.getInstance().getJdbcTemp().queryForObject(WATCH_LIST_SELECT_BASE_ON_XID, pk, new WatchListRowMapper());
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
	public Object[] create(WatchList entity) {
		
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
				
				
		return new Object[] {keyHolder.getKey().longValue()};
	}

}
