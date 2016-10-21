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

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.view.View;


/**
 * View DAO
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class ViewDAO implements GenericDAO<View> {
	
	private Log LOG = LogFactory.getLog(ViewDAO.class);
	
	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA = "data";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_BACKGROUND = "background";
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final String COLUMN_NAME_ANONYMOUS_ACCESS = "anonymousAccess";
	
	// @formatter:off
	private static final String VIEW_SELECT = ""
			+"select "
				+ COLUMN_NAME_DATA+", "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_NAME+", "
				+ COLUMN_NAME_BACKGROUND+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_ANONYMOUS_ACCESS+" "
			+ "from "
				+ "mangoViews";
	
	private static final String VIEW_FILTER_BASE_ON_ID=""
			 +COLUMN_NAME_ID+"=?";
	
	private static final String VIEW_INSERT = ""
			+"insert mangoViews ("
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_NAME+", "
				+ COLUMN_NAME_BACKGROUND+", "
				+ COLUMN_NAME_USER_ID+", "
				+ COLUMN_NAME_ANONYMOUS_ACCESS+", "
				+ COLUMN_NAME_DATA+") "
			+ "values (?,?,?,?,?,?)";
	
	private static final String VIEW_UPDATE = ""
			+"update mangoViews set "
				+ COLUMN_NAME_XID+"=?, "
				+ COLUMN_NAME_NAME+"=?, "
				+ COLUMN_NAME_BACKGROUND+"=?, "
				+ COLUMN_NAME_ANONYMOUS_ACCESS+"=?, "
				+ COLUMN_NAME_DATA+"=? "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String VIEW_DELETE = ""
			+"delete "
				+ "from "
					+ "mangoViews "
			+ "where "
				+ COLUMN_NAME_ID+"=?";

	// @formatter:on
	
	// RowMapper
	class ViewRowMapper implements RowMapper<View> {
		public View mapRow(ResultSet rs, int rowNum) throws SQLException {
			View v;
			Blob blob = rs.getBlob(1);
			if (blob == null) {
				// This can happen during upgrade
				v = new View();
			} else {
				v = (View) new SerializationData().readObject(blob.getBinaryStream());
			}
			
			v.setId(rs.getInt(2));
			v.setXid(rs.getString(3));
			v.setName(rs.getString(4));
			v.setBackgroundFilename(rs.getString(5));
			v.setUserId(rs.getInt(6));
			v.setAnonymousAccess(rs.getInt(7));

			return v;
		}
	}

	@Override
	public List<View> findAll() {
		return (List<View>) DAO.getInstance().getJdbcTemp().query(VIEW_SELECT, new Object[]{}, new ViewRowMapper() );
	}

	@Override
	public View findById(Object[] pk) {
		return (View) DAO.getInstance().getJdbcTemp().queryForObject(VIEW_SELECT+ " where " + VIEW_FILTER_BASE_ON_ID, pk , new ViewRowMapper());
	}

	@Override
	public List<View> filtered(String filter, Object[] argsFilter, long limit) {
		
		String myLimit="";
		Object[] args;
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<View>) DAO.getInstance().getJdbcTemp().query(VIEW_SELECT+" where "+ filter + myLimit, args, new ViewRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(View entity) {
		
		if (LOG.isTraceEnabled()) {
			  LOG.trace(entity);
		}
			
		KeyHolder keyHolder = new GeneratedKeyHolder();
					
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(VIEW_INSERT, Statement.RETURN_GENERATED_KEYS);
						 				new ArgumentPreparedStatementSetter( new Object[] { 
						 						entity.getXid(),
						 						entity.getName(),
						 						entity.getBackgroundFilename(),
						 						entity.getUserId(),
						 						entity.getAnonymousAccess(),
						 						new SerializationData().writeObject(entity)	 						
						 				}).setValues(ps);
						 				return ps;
						 			}
					}, keyHolder);
					
			entity.setId(keyHolder.getKey().intValue());		
			return new Object[] {keyHolder.getKey().intValue()};
	}

	@Override
	public void update(View entity) {
		
		DAO.getInstance().getJdbcTemp().update(VIEW_UPDATE, new Object[]{
				entity.getXid(),
				entity.getName(),
				entity.getBackgroundFilename(),
				entity.getAnonymousAccess(),
				new SerializationData().writeObject(entity),
				entity.getId()
		});
			
	}

	@Override
	public void delete(View entity) {
		DAO.getInstance().getJdbcTemp().update(VIEW_DELETE, new Object[] { entity.getId() });		
	}

}
