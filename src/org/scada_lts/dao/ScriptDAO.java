/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.JsonScript;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.org.scadabr.vo.scripting.ScriptVO;

/** 
 * DAO for Script
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class ScriptDAO  {
	
	private static final Log LOG = LogFactory.getLog(ScriptDAO.class);
	
	private static final String COLUMN_NAME_ID="id";
	private static final String COLUMN_NAME_XID="xid";
	private static final String COLUMN_NAME_NAME="name";
	private static final String COLUMN_NAME_SCRIPT="script";
	private static final String COLUMN_NAME_USERID="userId";
	private static final String COLUMN_NAME_DATA="data";
	
	// @formatter:off	
	private static final String SCRIPT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID +","
				+ COLUMN_NAME_XID +","
				+ COLUMN_NAME_NAME +","
				+ COLUMN_NAME_SCRIPT + ","
				+ COLUMN_NAME_USERID + ","
				+ COLUMN_NAME_DATA + " " 
		    + "from " +
				"scripts ";
	
	
	private static final String SCRIPT_INSERT = ""
			+ "insert scripts ("
				+ COLUMN_NAME_XID + "," 
				+ COLUMN_NAME_NAME + "," 
				+ COLUMN_NAME_SCRIPT + "," 
				+ COLUMN_NAME_USERID + "," 
				+ COLUMN_NAME_DATA 
			+") "
			+ "values (?,?,?,?,?)";
	
	private static final String SCRIPT_UPDATE = ""
			+ "update scripts set "
				+ COLUMN_NAME_XID+"=?,"
				+ COLUMN_NAME_NAME+"=?,"
				+ COLUMN_NAME_SCRIPT+"=?,"
				+ COLUMN_NAME_USERID+"=?,"
				+ COLUMN_NAME_DATA+"=? "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String SCRIPT_DELETE= ""
			+ "delete "
			+ "from "
				+ "scripts "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String SCRIPT_SELECT_ONE=""
			+ SCRIPT_SELECT 
			+ " where "
			  + COLUMN_NAME_ID+"=?";
	
	private static final String SCRIPT_SELECT_BASE_ON_XID=""
			+ SCRIPT_SELECT 
			+ "where "
			  + COLUMN_NAME_XID+"=?";
	
	// @formatter:on
	
	//RowMapper
	private class ScriptRowMapper implements RowMapper<ScriptVO<?>> {
		public ScriptVO<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScriptVO<?> script;
            
            script = (ScriptVO<?>) new SerializationData().readObject(rs.getBlob(COLUMN_NAME_DATA).getBinaryStream());
			script.setId(rs.getInt(COLUMN_NAME_ID));
			script.setXid(rs.getString(COLUMN_NAME_XID));
			script.setName(rs.getString(COLUMN_NAME_NAME));
			script.setScript(rs.getString(COLUMN_NAME_SCRIPT));
			script.setUserId(rs.getInt(COLUMN_NAME_USERID));
			return script;
		}
	}

	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final ScriptVO<?> vo) {
		
			if (LOG.isTraceEnabled()) {
				LOG.trace(vo);
			}
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
				 			@Override
				 			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				 				PreparedStatement ps = connection.prepareStatement(SCRIPT_INSERT, Statement.RETURN_GENERATED_KEYS);
				 				new ArgumentPreparedStatementSetter( new Object[] { 
				 						vo.getXid(), 
				 						vo.getName(), 
				 						vo.getScript(), 
				 						vo.getUserId(),
				 						new SerializationData().writeObject(vo)
				 				}).setValues(ps);
				 				return ps;
				 			}
			}, keyHolder);
			
			return keyHolder.getKey().intValue();
			
	}
	
	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(final ScriptVO<?> vo) {
		
			if (LOG.isTraceEnabled()) {
				LOG.trace(vo);
			}
			
			DAO.getInstance().getJdbcTemp().update(SCRIPT_UPDATE, new Object[]  { 
					vo.getXid(),
					vo.getName(), 
					vo.getScript(), 
					vo.getUserId(),
					new SerializationData().writeObject(vo), 
					vo.getId() });
	}

	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(final int id) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("delete script id:"+id);
		}
		
		DAO.getInstance().getJdbcTemp().update(SCRIPT_DELETE, new Object[]  { id });
		
	}

	public ScriptVO<?> getScript(int id) {
		return (ScriptVO<?>) DAO.getInstance().getJdbcTemp().queryForObject(SCRIPT_SELECT_ONE, new Object[]  { id }, new ScriptRowMapper());
	}
	
	public List<ScriptVO<?>> getScripts() {
		return (List<ScriptVO<?>>) DAO.getInstance().getJdbcTemp().query(SCRIPT_SELECT, new Object[]{ }, new ScriptRowMapper());
	}
		
	public ScriptVO<?> getScript(String xid) {
		
		return (ScriptVO<?>) DAO.getInstance().getJdbcTemp().queryForObject(SCRIPT_SELECT_BASE_ON_XID, new Object[]  { 
				xid }, new ScriptRowMapper());
	}

	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(ScriptVO.XID_PREFIX, "scripts");
	}

	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "scripts");
	}

	public JsonScript findScriptsByPage(String query) {
		return (JsonScript) DAO.getInstance().getJdbcTemp().query(SCRIPT_SELECT + " ", new Object[]{ }, new ScriptRowMapper());
	}
}
