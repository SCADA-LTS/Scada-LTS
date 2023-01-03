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
package org.scada_lts.dao.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.serotonin.mango.rt.event.type.AuditEventUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.GenericDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;

/**
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class CompoundEventDetectorDAO implements GenericDAO<CompoundEventDetectorVO> {
	
	private Log LOG = LogFactory.getLog(CompoundEventDetectorDAO.class);
	
	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_RETURN_TO_NORMAL = "returnToNormal";
	private static final String COLUMN_NAME_DISABLED = "disabled";
	private static final String COLUMN_NAME_CONDITION_TEXT = "conditionText";
	
	// @formatter:off
	private static final String COMPOUND_EVENT_DETECTOR_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID+", "
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_NAME+", "
				+ COLUMN_NAME_ALARM_LEVEL+", "
				+ COLUMN_NAME_RETURN_TO_NORMAL+", "
				+ COLUMN_NAME_DISABLED+", "
				+ COLUMN_NAME_CONDITION_TEXT+" "
			+ "from "
				+ "compoundEventDetectors ";
	
	private static final String COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_ID = ""
				+COLUMN_NAME_ID+"=?";
	
	private static final String COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_XID = ""
			+COLUMN_NAME_ID+"=?";

	
	private static final String COMPOUND_EVENT_INSERT = ""
			+"insert compoundEventDetectors ("
				+ COLUMN_NAME_XID+", "
				+ COLUMN_NAME_NAME+", "
				+ COLUMN_NAME_ALARM_LEVEL+", "
				+ COLUMN_NAME_RETURN_TO_NORMAL+", "
				+ COLUMN_NAME_DISABLED+", "
				+ COLUMN_NAME_CONDITION_TEXT+""
				+ ") "
            + "values (?,?,?,?,?,?)";
	
	private static final String COMPOUND_EVENT_UPDATE = ""
			+"update compoundEventDetectors set "
				+ COLUMN_NAME_XID+"=?, "
				+ COLUMN_NAME_NAME+"=?, "
				+ COLUMN_NAME_ALARM_LEVEL+"=?, "
				+ COLUMN_NAME_RETURN_TO_NORMAL+"=?, "
				+ COLUMN_NAME_DISABLED+"=?, "
				+ COLUMN_NAME_CONDITION_TEXT+"=? "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String COMPOUND_EVENT_DELETE = ""
			+"delete "
				+ "from "
					+ "compoundEventDetectors "
			+ "where "
				+ COLUMN_NAME_ID+"=?";
	
	private static final String COMPOUND_EVENT_DELETE_EVENT_HANLDERS=""
			+ "delete "
				+ "from "
					+ "eventHandlers "
			+ "where "
				+ "eventTypeId=" + EventType.EventSources.COMPOUND+" and "
				+ "eventTypeRef1=?";
			
	
	// @formatter:on
	
	//RowMapper
	class CompoundEventDetectorRowMapper implements RowMapper<CompoundEventDetectorVO> {
        public CompoundEventDetectorVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompoundEventDetectorVO ced = new CompoundEventDetectorVO();
            ced.setId(rs.getInt(COLUMN_NAME_ID));
            ced.setXid(rs.getString(COLUMN_NAME_XID));
            ced.setName(rs.getString(COLUMN_NAME_NAME));
            ced.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
            ced.setReturnToNormal(DAO.charToBool(rs.getString(COLUMN_NAME_RETURN_TO_NORMAL)));
            ced.setDisabled(DAO.charToBool(rs.getString(COLUMN_NAME_DISABLED)));
            ced.setCondition(rs.getString(COLUMN_NAME_CONDITION_TEXT));
            return ced;
        }
    }

	@Override
	public List<CompoundEventDetectorVO> findAllWithUserName() {
		return null;
	}

	@Override
	public List<CompoundEventDetectorVO> findAll() {
		return (List<CompoundEventDetectorVO>) DAO.getInstance().getJdbcTemp().query(COMPOUND_EVENT_DETECTOR_SELECT+" order by name", new Object[]{}, new CompoundEventDetectorRowMapper());
	}

	@Override
	public CompoundEventDetectorVO findById(Object[] pk) {
		try {
			return (CompoundEventDetectorVO) DAO.getInstance().getJdbcTemp().queryForObject(COMPOUND_EVENT_DETECTOR_SELECT+ " where " + COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_ID, pk , new CompoundEventDetectorRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public CompoundEventDetectorVO findByXId(Object[] pk) {
		try {
			return (CompoundEventDetectorVO) DAO.getInstance().getJdbcTemp().queryForObject(COMPOUND_EVENT_DETECTOR_SELECT+ " where " + COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_XID, pk , new CompoundEventDetectorRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<CompoundEventDetectorVO> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		Object[] args;
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<CompoundEventDetectorVO>) DAO.getInstance().getJdbcTemp().query(COMPOUND_EVENT_DETECTOR_SELECT+" where "+ filter + myLimit, args,  new CompoundEventDetectorRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(final CompoundEventDetectorVO entity) {
		if (LOG.isTraceEnabled()) {
			  LOG.trace(entity);
		}
			
		KeyHolder keyHolder = new GeneratedKeyHolder();
					
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(COMPOUND_EVENT_INSERT, Statement.RETURN_GENERATED_KEYS);
						 				new ArgumentPreparedStatementSetter( new Object[] {
						 						entity.getXid(),
						 						entity.getName(),
						 						entity.getAlarmLevel(),
						 						DAO.boolToChar(entity.isReturnToNormal()),
						 						DAO.boolToChar(entity.isDisabled()),
						 						entity.getCondition()	 						
						 				}).setValues(ps);
						 				return ps;
						 			}
					}, keyHolder);
					
			entity.setId(keyHolder.getKey().intValue());		
			return new Object[] {keyHolder.getKey().intValue()};
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void update(CompoundEventDetectorVO entity) {

		DAO.getInstance().getJdbcTemp().update(COMPOUND_EVENT_UPDATE, new Object[]{
				entity.getXid(),
				entity.getName(),
				entity.getAlarmLevel(),
				DAO.boolToChar(entity.isReturnToNormal()),
				DAO.boolToChar(entity.isDisabled()),
				entity.getCondition(),
				entity.getId()
		});
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void delete(CompoundEventDetectorVO entity) {
		DAO.getInstance().getJdbcTemp().update(COMPOUND_EVENT_DELETE_EVENT_HANLDERS,new Object[]{entity.getId()});
		DAO.getInstance().getJdbcTemp().update(COMPOUND_EVENT_DELETE, new Object[]{entity.getId()});
		AuditEventUtils.raiseDeletedEvent(AuditEventType.TYPE_COMPOUND_EVENT_DETECTOR, entity);
	}

}
