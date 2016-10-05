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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.event.Event;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.db.dao.UserCommentRowMapper;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.CompoundDetectorEventType;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.MaintenanceEventType;
import com.serotonin.mango.rt.event.type.PublisherEventType;
import com.serotonin.mango.rt.event.type.ScheduledEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.web.dwr.EventsDwr;
import com.serotonin.util.StringUtils;

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
	
	
	//------------- User comments
	//TODO rewrite to another class
	private static final String COLUMN_NAME_COMMENT_TEXT = "commentText";
	private static final String COLUMN_NAME_TIME_STAMP = "ts";
	private static final String COLUMN_NAME_COMMENT_TYPE = "commentType";
	private static final String COLUMN_NAME_TYPE_KEY = "typeKey";
	
	//------------- Event handlers
	//TODO rewrite to another class
	private static final String COLUMN_NAME_EVENT_TYPE_ID = "eventTypeId";
	private static final String COLUMN_NAME_EVENT_TYPE_REF1 = "eventTypeRef1";
	private static final String COLUMN_NAME_EVENT_TYPE_REF2 = "eventTypeRef2";
	
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
	
	private static final String EVENT_FILTER_USER = ""
			+"ue."+COLUMN_NAME_USER_ID+"=? and "
			+"((e."+COLUMN_NAME_ACT_TS+" is null or e."+COLUMN_NAME_ACT_TS+"=0) "
			+"order by e."+COLUMN_NAME_ACT_TS+ " desc";
	
	private static final String EVENT_COMMENT_SELECT = ""
			+"select "
				+ "uc."+COLUMN_NAME_USER_ID+", "
				+ "u."+COLUMN_NAME_USER_NAME+", "
				+ "uc."+COLUMN_NAME_TIME_STAMP+","
				+ "uc."+COLUMN_NAME_COMMENT_TEXT+" "
		    + "from "
		    	+ "userComments uc left join users u on uc."+COLUMN_NAME_USER_ID+" = u."+COLUMN_NAME_ID
		    + "where uc."+COLUMN_NAME_COMMENT_TYPE+"= "+UserComment.TYPE_EVENT
				+ " and uc."+COLUMN_NAME_TYPE_KEY+"=? " + "order by uc."+COLUMN_NAME_TIME_STAMP;
	
	private static final String EVENT_DELETE_BEFORE= ""
			+"delete from events "
			+ "where "+COLUMN_NAME_ACTIVE_TS+"<? "
			+ "  and "+COLUMN_NAME_ACT_TS+" is not null "
			+ "  and ("+COLUMN_NAME_RTN_APPLICABLE+"=? or ("+COLUMN_NAME_RTN_APPLICABLE+"=? and "+COLUMN_NAME_ACT_TS+" is not null))";
	
	private static final String COUNT_EVENT=""
			+"select "
				+ "count(*) "
			+ "from "
				+ "events";
	
	private static final String EVENT_HANDLER_TYPE = ""
			+ "select "
				+ COLUMN_NAME_EVENT_TYPE_ID+","
				+ COLUMN_NAME_EVENT_TYPE_REF1+","
				+ COLUMN_NAME_EVENT_TYPE_REF2+" "
			+ "from "
				+ "eventHandlers "
			+ "where id=?";
			
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
	
	//TODO to rewrite - Comments in one field in event not one to many is too much 
	private class UserCommentRowMapper implements RowMapper<UserComment> {	    
	    public UserComment mapRow(ResultSet rs, int rowNum) throws SQLException {
	        UserComment c = new UserComment();
	        c.setUserId(rs.getInt(COLUMN_NAME_USER_ID));
	        c.setUsername(rs.getString(COLUMN_NAME_USER_NAME));
	        c.setTs(rs.getLong(COLUMN_NAME_TIME_STAMP));
	        c.setComment(rs.getString(COLUMN_NAME_COMMENT_TEXT));
	        return c;
	    }
	}
	
	private class EventTypeRowMapper implements RowMapper<EventType> {
		public EventType mapRow(ResultSet rs, int rowNum) throws SQLException {
			
				int typeId = rs.getInt(COLUMN_NAME_EVENT_TYPE_ID);
				EventType type = null;
				if (typeId == EventType.EventSources.DATA_POINT) {
					type = new DataPointEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1), rs.getInt(COLUMN_NAME_EVENT_TYPE_REF2));
					
				} else if (typeId == EventType.EventSources.DATA_SOURCE) {
					type = new DataSourceEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1), rs.getInt(COLUMN_NAME_EVENT_TYPE_REF2));
					
				} else if (typeId == EventType.EventSources.SYSTEM) {
					type = new SystemEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1),	rs.getInt(COLUMN_NAME_EVENT_TYPE_REF2));
					
				} else if (typeId == EventType.EventSources.COMPOUND) {
					type = new CompoundDetectorEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1));
					
				} else if (typeId == EventType.EventSources.SCHEDULED) {
					type = new ScheduledEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1));
					
				} else if (typeId == EventType.EventSources.PUBLISHER) { 
					type = new PublisherEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1), rs.getInt(COLUMN_NAME_EVENT_TYPE_REF2));
				
				} else if (typeId == EventType.EventSources.AUDIT) {
					type = new AuditEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1),	rs.getInt(COLUMN_NAME_EVENT_TYPE_REF2));
					
				} else if (typeId == EventType.EventSources.MAINTENANCE) {
					type = new MaintenanceEventType(rs.getInt(COLUMN_NAME_EVENT_TYPE_REF1));
					
				} else {
					
					
						//throw new Exception("Unknown event type: "	+ typeId);
					
				}
				return type;
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
	
	public List<Event> getPendingEventsLimit(int userId, int limit) {
		
		Object[] args = new Object[] {userId, limit};
		String myLimit = LIMIT+" ? ";
		
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(EVENT_SELECT_WITH_USER_DATA+" where " + EVENT_FILTER_USER + myLimit, args, new UserEventRowMapper() );
		
	}

	public void attacheRelationalInfo(Event event) {
		List<UserComment> lstUserComments = (List<UserComment>) DAO.getInstance().getJdbcTemp().query(EVENT_COMMENT_SELECT, new Object[] { event.getId() }, new UserCommentRowMapper() );
		event.setEventComments(lstUserComments); 
	}
	
	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int purgeEventsBefore(long time) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("delete events before ts:"+time);
		}
		
		int count = DAO.getInstance().getJdbcTemp().update(EVENT_DELETE_BEFORE, new Object[]  { time, DAO.boolToChar(false), DAO.boolToChar(true) });
		
		//TODO rewrite when delete event delete cascade remove user comments
		
		DAO.getInstance().getJdbcTemp().update(EVENT_DELETE_BEFORE, new Object[]  { time, DAO.boolToChar(false), DAO.boolToChar(true) });
		
		return count;
		
	}

	public int getEventCount() {
		return DAO.getInstance().getJdbcTemp().queryForObject(COUNT_EVENT, Integer.class);
	}
	
	//TODO rewrite
	public List<Event> searchOld(int eventId, int eventSourceType, String status, int alarmLevel, final String[] keywords,
			final int maxResults, int userId, final ResourceBundle bundle) {
		List<String> where = new ArrayList<String>();
		List<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder();
		sql.append(EVENT_SELECT_WITH_USER_DATA);
		sql.append("where ue.userId=?");
		params.add(userId);

		if (eventId != 0) {
			where.add("e.id=?");
			params.add(eventId);
		}

		if (eventSourceType != -1) {
			where.add("e.typeId=?");
			params.add(eventSourceType);
		}

		if (EventsDwr.STATUS_ACTIVE.equals(status)) {
			where.add("e.rtnApplicable=? and e.rtnTs is null");
			params.add(DAO.boolToChar(true));
		} else if (EventsDwr.STATUS_RTN.equals(status)) {
			where.add("e.rtnApplicable=? and e.rtnTs is not null");
			params.add(DAO.boolToChar(true));
		} else if (EventsDwr.STATUS_NORTN.equals(status)) {
			where.add("e.rtnApplicable=?");
			params.add(DAO.boolToChar(false));
		}

		if (alarmLevel != -1) {
			where.add("e.alarmLevel=?");
			params.add(alarmLevel);
		}

		if (!where.isEmpty()) {
			for (String s : where) {
				sql.append(" and ");
				sql.append(s);
			}
		}
		sql.append(" order by e.activeTs desc");

		final List<Event> results = new ArrayList<Event>();
		final UserEventRowMapper rowMapper = new UserEventRowMapper();

		DAO.getInstance().getJdbcTemp().query(sql.toString(), params.toArray(), new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				while (rs.next()) {
					Event e = rowMapper.mapRow(rs, 0);
					//TODO
					//attachRelationalInfo(e);
					boolean add = true;

					if (keywords != null) {
						// Do the text search. If the instance has a match, put
						// it in the result. Otherwise ignore.
						StringBuilder text = new StringBuilder();
						//TODO
						//text.append(e.getMessage().getLocalizedMessage(bundle));
						for (UserComment comment : e.getEventComments())
							text.append(' ').append(comment.getComment());

						String[] values = text.toString().split("\\s+");

						for (String keyword : keywords) {
							if (!StringUtils.globWhiteListMatchIgnoreCase(
									values, keyword)) {
								add = false;
								break;
							}
						}

					}

					if (add) {
						results.add(e);
						if (results.size() >= maxResults)
							break;
					}
				}

				return null;
			}
		});

		return results;
	}
	
	//TODO rewrit on base code in EventDao
	private int searchRowCount;
	private int startRow;

	public int getSearchRowCount() {
		return searchRowCount;
	}

	public int getStartRow() {
		return startRow;
	}
	
	public List<Event> search(int eventId, int eventSourceType,
			String status, int alarmLevel, final String[] keywords,
			long dateFrom, long dateTo, int userId,
			final ResourceBundle bundle, final int from, final int to,
			final Date date) {
		List<String> where = new ArrayList<String>();
		List<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder();
		sql.append(EVENT_SELECT_WITH_USER_DATA);
		sql.append("where ue.userId=?");
		params.add(userId);

		if (eventId != 0) {
			where.add("e.id=?");
			params.add(eventId);
		}

		if (eventSourceType != -1) {
			where.add("e.typeId=?");
			params.add(eventSourceType);
		}

		if (EventsDwr.STATUS_ACTIVE.equals(status)) {
			where.add("e.rtnApplicable=? and e.rtnTs is null");
			params.add(DAO.boolToChar(true));
		} else if (EventsDwr.STATUS_RTN.equals(status)) {
			where.add("e.rtnApplicable=? and e.rtnTs is not null");
			params.add(DAO.boolToChar(true));
		} else if (EventsDwr.STATUS_NORTN.equals(status)) {
			where.add("e.rtnApplicable=?");
			params.add(DAO.boolToChar(false));
		}

		if (alarmLevel != -1) {
			where.add("e.alarmLevel=?");
			params.add(alarmLevel);
		}

		if (dateFrom != -1) {
			where.add("activeTs>=?");
			params.add(dateFrom);
		}

		if (dateTo != -1) {
			where.add("activeTs<?");
			params.add(dateTo);
		}

		if (!where.isEmpty()) {
			for (String s : where) {
				sql.append(" and ");
				sql.append(s);
			}
		}
		sql.append(" order by e.activeTs desc");

		final List<Event> results = new ArrayList<Event>();
		final UserEventRowMapper rowMapper = new UserEventRowMapper();

		final int[] data = new int[2];

		DAO.getInstance().getJdbcTemp().query(sql.toString(), params.toArray(), new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				int row = 0;
				long dateTs = date == null ? -1 : date.getTime();
				int startRow = -1;

				while (rs.next()) {
					Event e = rowMapper.mapRow(rs, 0);
					//TODO comments
					//attachRelationalInfo(e);
					boolean add = true;

					if (keywords != null) {
						// Do the text search. If the instance has a match, put
						// it in the result. Otherwise ignore.
						StringBuilder text = new StringBuilder();
						//text.append(e.getMessage().getLocalizedMessage(bundle));
						for (UserComment comment : e.getEventComments())
							text.append(' ').append(comment.getComment());

						String[] values = text.toString().split("\\s+");

						for (String keyword : keywords) {
							if (keyword.startsWith("-")) {
								if (StringUtils.globWhiteListMatchIgnoreCase(
										values, keyword.substring(1))) {
									add = false;
									break;
								}
							} else {
								if (!StringUtils.globWhiteListMatchIgnoreCase(
										values, keyword)) {
									add = false;
									break;
								}
							}
						}
					}

					if (add) {
						if (date != null) {
							if (e.getActiveTimestamp() <= dateTs
									&& results.size() < to - from) {
								if (startRow == -1)
									startRow = row;
								results.add(e);
							}
						} else if (row >= from && row < to)
							results.add(e);

						row++;
					}
				}

				data[0] = row;
				data[1] = startRow;

				return null;
			}
		});

		searchRowCount = data[0];
		startRow = data[1];

		return results;
	}
	
	public EventType getEventHandlerType(int handlerId) {
		return (EventType) DAO.getInstance().getJdbcTemp().query(EVENT_HANDLER_TYPE,new Object[] { handlerId }, new EventTypeRowMapper() );
	}
	
}
