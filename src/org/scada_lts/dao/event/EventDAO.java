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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.event.Event;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;

/**
 * Event DAO
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class EventDAO implements GenericDaoCR<Event> {

	private static final Log LOG = LogFactory.getLog(EventDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_TYPE_ID = "typeId";
	private static final String COLUMN_NAME_TYPE_REF_1 = "typeRef1";
	private static final String COLUMN_NAME_TYPE_REF_2 = "typeRef2";
	private static final String COLUMN_NAME_ACTIVE_TS = "activeTs";
	private static final String COLUMN_NAME_RTN_APPLICABLE = "rtnApplicable";
	private static final String COLUMN_NAME_RTN_TS = "rtnTs";
	private static final String COLUMN_NAME_RTN_CAUSE = "rtnCause";
	private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_MESSAGE = "message";
	private static final String COLUMN_NAME_ACT_TS = "ackTs";
	private static final String COLUMN_NAME_ACT_USER_ID = "ackUserId";
	private static final String COLUMN_NAME_USER_NAME = "username";
	private static final String COLUMN_NAME_ALTERNATE_ACK_SOURCE = "alternateAckSource";
	private static final String COLUMN_NAME_SILENCED = "silenced";
	private static final String COLUMN_NAME_EVENT_ID = "eventId";
	private static final String COLUMN_NAME_USER_ID = "userId";

	// @formatter:off
	private static final String BASIC_EVENT_SELECT = ""
			+"select "
				+ "e."+COLUMN_NAME_ID+", "
				+ "e."+COLUMN_NAME_TYPE_ID+", "
				+ "e."+COLUMN_NAME_TYPE_REF_1+", "
				+ "e."+COLUMN_NAME_TYPE_REF_2+","
				+ "e."+COLUMN_NAME_ACTIVE_TS+","
				+ "e."+COLUMN_NAME_RTN_APPLICABLE+", "
				+ "e."+COLUMN_NAME_RTN_TS+","
				+ "e."+COLUMN_NAME_RTN_CAUSE+", "
				+ "e."+COLUMN_NAME_ALARM_LEVEL+", "
				+ "e."+COLUMN_NAME_MESSAGE+", "
				+ "e."+COLUMN_NAME_ACT_TS+", "
				+ "e."+COLUMN_NAME_ACT_USER_ID+", "
				+ "u."+COLUMN_NAME_USER_NAME+","
				+ "e."+COLUMN_NAME_ALTERNATE_ACK_SOURCE+" "
			+ "from "
				+ "events e " 
			    + "left join users u on e."+COLUMN_NAME_ACT_USER_ID+"=u.id ";
	
	
	private static final String EVENT_INSERT = ""
			+ "insert events ("
				+ COLUMN_NAME_TYPE_ID + "," 
				+ COLUMN_NAME_TYPE_REF_1 + "," 
				+ COLUMN_NAME_TYPE_REF_2 + "," 
				+ COLUMN_NAME_ACTIVE_TS + ","
				+ COLUMN_NAME_RTN_APPLICABLE + ","
				+ COLUMN_NAME_RTN_TS + ","
				+ COLUMN_NAME_RTN_CAUSE + ","
				+ COLUMN_NAME_ALARM_LEVEL + ","
				+ COLUMN_NAME_MESSAGE + ","
				+ COLUMN_NAME_ACT_TS 
				// userId ?
				// ack_source ?
			+") "
			+ "values (?,?,?,?,?,?,?,?,?,?)";
	
	private static final String EVENT_SELECT_BASE_ON_ID = ""+
			BASIC_EVENT_SELECT
			+"where "
				+"e."+COLUMN_NAME_ID+"=?";
	
	private static final String EVENT_UPDATE_CAUSE = ""+
			"update "
			+ "events set "
			+ COLUMN_NAME_RTN_TS + "=?,"
			+ COLUMN_NAME_RTN_CAUSE+"=?";
	
	private static final String EVENT_ACT ="" +
			"update "
				+"events set "
				+ COLUMN_NAME_ACT_TS+"=?, "
				+ COLUMN_NAME_ACT_USER_ID+"=?, "
				+ COLUMN_NAME_ALTERNATE_ACK_SOURCE+"=? "
		  + "where "
				+ COLUMN_NAME_ID+"=? and "
				+ "("+COLUMN_NAME_ACT_TS+" is null or "+COLUMN_NAME_ACT_TS+" = 0) ";
	
	public static final String EVENT_ACTIVE=""+
			BASIC_EVENT_SELECT
			+"where "
				+"e."+ COLUMN_NAME_RTN_APPLICABLE+"=? and e."+ COLUMN_NAME_RTN_TS+" is null";
	
	private static final String EVENT_SELECT_WITH_USER_DATA=""
			+"select "
				+ "e."+COLUMN_NAME_ID+", "
				+ "e."+COLUMN_NAME_TYPE_ID+", "
				+ "e."+COLUMN_NAME_TYPE_REF_1+", "
				+ "e."+COLUMN_NAME_TYPE_REF_2+","
				+ "e."+COLUMN_NAME_ACTIVE_TS+","
				+ "e."+COLUMN_NAME_RTN_APPLICABLE+", "
				+ "e."+COLUMN_NAME_RTN_TS+","
				+ "e."+COLUMN_NAME_RTN_CAUSE+", "
				+ "e."+COLUMN_NAME_ALARM_LEVEL+", "
				+ "e."+COLUMN_NAME_MESSAGE+", "
				+ "e."+COLUMN_NAME_ACT_TS+", "
				+ "e."+COLUMN_NAME_ACT_USER_ID+", "
				+ "u."+COLUMN_NAME_USER_NAME+","
				+ "e."+COLUMN_NAME_ALTERNATE_ACK_SOURCE+", "
				+ "ue."+COLUMN_NAME_SILENCED+" "
			+ "from "
				+ "events e " 
				+ "left join users u on e."+COLUMN_NAME_ACT_USER_ID+"=u.id "
				+ "left join userEvents ue on e."+COLUMN_NAME_ID+"=ue."+COLUMN_NAME_EVENT_ID;
				
	private static final String EVENT_FILTER_FOR_DATA_POINT=""+
			"e."+COLUMN_NAME_TYPE_ID+"=" + EventType.EventSources.DATA_POINT+" and "
		  + "e."+COLUMN_NAME_TYPE_REF_1+"=? and "
		  + "ue."+COLUMN_NAME_USER_ID+"=? "
		  + "order by e."+COLUMN_NAME_ACTIVE_TS+" desc";
	
	private static final String EVENT_FILTER_TYPE_REF_USER = ""
			+"e."+COLUMN_NAME_TYPE_ID+"=? and "
			+"e."+COLUMN_NAME_TYPE_REF_1+"=? and "
			+"ue."+COLUMN_NAME_USER_ID+"=? and "
			+"((e."+COLUMN_NAME_ACT_TS+" is null or e."+COLUMN_NAME_ACT_TS+"=0) or (e."+COLUMN_NAME_RTN_APPLICABLE+"=? and e."+COLUMN_NAME_RTN_TS+" is null and e."+COLUMN_NAME_ALARM_LEVEL+" > 0))"
			+"order by e."+COLUMN_NAME_ACT_TS+ " desc";
	
	private static final String EVENT_FILTER_TYPE_USER = ""
			+"e."+COLUMN_NAME_TYPE_ID+"=? and "
			+"ue."+COLUMN_NAME_USER_ID+"=? and "
			+"((e."+COLUMN_NAME_ACT_TS+" is null or e."+COLUMN_NAME_ACT_TS+"=0) or (e."+COLUMN_NAME_RTN_APPLICABLE+"=? and e."+COLUMN_NAME_RTN_TS+" is null and e."+COLUMN_NAME_ALARM_LEVEL+" > 0))"
			+"order by e."+COLUMN_NAME_ACT_TS+ " desc";
	
			
	
	// @formatter:onn

	// RowMapper
	private class EventRowMapper implements RowMapper<Event> {
		public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
			Event event;

			event = new Event();
			event.setId(rs.getLong(COLUMN_NAME_ID));
			event.setEventType(rs.getInt(COLUMN_NAME_TYPE_ID));
			event.setTypeRef1(rs.getInt(COLUMN_NAME_TYPE_REF_1));
			event.setTypeRef2(rs.getInt(COLUMN_NAME_TYPE_REF_2));
			event.setActiveTimestamp(rs.getLong(COLUMN_NAME_ACTIVE_TS));
			event.setRtnApplicable( DAO.charToBool(rs.getString(COLUMN_NAME_RTN_APPLICABLE)));
			event.setRtnTimestamp(rs.getLong(COLUMN_NAME_RTN_TS));
			event.setRtnCause(rs.getInt(COLUMN_NAME_RTN_CAUSE));
			event.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
			event.setMessage(rs.getString(COLUMN_NAME_MESSAGE));
			event.setAckTS(rs.getLong(COLUMN_NAME_ACT_TS));
			event.setActUserId(rs.getLong(COLUMN_NAME_ACT_USER_ID));
			event.setUserName(rs.getString(COLUMN_NAME_USER_NAME));
			event.setAlternateAckSource(rs.getInt(COLUMN_NAME_ALTERNATE_ACK_SOURCE));
						
			return event;
			
		}
	}
	
	//TODO to rewrite - User have event not event have user silenced;
	private class UserEventRowMapper extends EventRowMapper {
		@Override
		public Event mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Event event = super.mapRow(rs, rowNum);
			event.setSilenced(DAO.charToBool(rs.getString(COLUMN_NAME_SILENCED)));
			if (!rs.wasNull())
				event.setUserNotified(true);
			return event;
		}
	}
	
	

	@Override
	public List<Event> findAll() {
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(BASIC_EVENT_SELECT, new Object[]{ }, new EventRowMapper());
	}

	@Override
	public Event findById(Object[] pk) {
		return (Event) DAO.getInstance().getJdbcTemp().queryForObject(EVENT_SELECT_BASE_ON_ID, pk, new EventRowMapper());
	}

	@Override
	public List<Event> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		Object[] args;
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(BASIC_EVENT_SELECT+" where "+ filter + myLimit, args, new EventRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(final Event entity) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(entity);
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			 			@Override
			 			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			 				PreparedStatement ps = connection.prepareStatement(EVENT_INSERT, Statement.RETURN_GENERATED_KEYS);
			 				new ArgumentPreparedStatementSetter( new Object[] { 
			 						entity.getEventType(),
			 						entity.getTypeRef1(),
			 						entity.getTypeRef2(),
			 						entity.getActiveTimestamp(),
			 						DAO.boolToChar(entity.isRtnApplicable()),
			 						entity.getRtnTimestamp(),
			 						entity.getRtnCause(),
			 						entity.getAlarmLevel(),
			 						entity.getMessage(),
			 						entity.getAckTS()
			 				}).setValues(ps);
			 				return ps;
			 			}
		}, keyHolder);
		
		return new Object[] {keyHolder.getKey().intValue()};
	}
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void updateCause(int cause, long ts) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("cause:"+cause+" ts:"+ts);
		}
				
		DAO.getInstance().getJdbcTemp().update( EVENT_UPDATE_CAUSE, new Object[]  { ts, cause } );	
		
	}
	
	public void updateAck(long actTS, long userId, int alternateAckSource, long eventId ) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("actTS:"+actTS+" userId:"+userId+" alternateAckSource:"+alternateAckSource+" eventId:"+eventId);
		}
				
		DAO.getInstance().getJdbcTemp().update( EVENT_ACT, new Object[]  { actTS, userId, alternateAckSource, eventId } );
			
	}
	
	public List<Event> getEventsForDataPoint(int dataPointId, int userId) {	
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(EVENT_SELECT_WITH_USER_DATA+" where "+ EVENT_FILTER_FOR_DATA_POINT, new Object[]{dataPointId, userId}, new UserEventRowMapper());
	}
	
	public List<Event> getPendingEvents(int typeId, int typeRef1, int userId) {
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(EVENT_SELECT_WITH_USER_DATA+" where " + EVENT_FILTER_TYPE_REF_USER, new Object[]{typeId, typeRef1, userId, DAO.boolToChar(true)}, new UserEventRowMapper() );	
	}
	
	public List<Event> getPendingEvents(int typeId, int userId) {
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(EVENT_SELECT_WITH_USER_DATA+" where " + EVENT_FILTER_TYPE_USER, new Object[]{typeId, userId, DAO.boolToChar(true)}, new UserEventRowMapper() );	
	}
	
}
