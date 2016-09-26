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
	private static final String COLUMN_NAME_RTN_COUSE = "rtnCause";
	private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_MESSAGE = "message";
	private static final String COLUMN_NAME_ACT_TS = "ackTs";
	private static final String COLUMN_NAME_ACT_USER_ID = "ackUserId";
	private static final String COLUMN_NAME_USER_NAME = "username";
	private static final String COLUMN_NAME_ALTERNATE_ACK_SOURCE = "alternateAckSource";

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
				+ "e."+COLUMN_NAME_RTN_COUSE+", "
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
				+ COLUMN_NAME_RTN_COUSE + ","
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
			event.setRtnCause(rs.getInt(COLUMN_NAME_RTN_COUSE));
			event.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
			event.setMessage(rs.getString(COLUMN_NAME_MESSAGE));
			event.setAckTS(rs.getLong(COLUMN_NAME_ACT_TS));
			event.setActUserId(rs.getLong(COLUMN_NAME_ACT_USER_ID));
			event.setUserName(rs.getString(COLUMN_NAME_USER_NAME));
			event.setAlternateAckSource(rs.getInt(COLUMN_NAME_ALTERNATE_ACK_SOURCE));
						
			return event;
		}
	}

	@Override
	public List<Event> findAll() {
		return (List<Event>) DAO.getInstance().getJdbcTemp().query(BASIC_EVENT_SELECT, new Object[]{ }, new EventRowMapper());
	}

	@Override
	public Event findById(long id) {
		return (Event) DAO.getInstance().getJdbcTemp().queryForObject(EVENT_SELECT_BASE_ON_ID, new Object[]  { id }, new EventRowMapper());
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
	public long create(Event entity) {
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
		
		return keyHolder.getKey().intValue();
	}

}
