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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.event.UserEvent;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * User Event DAO
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class UserEventDAO implements GenericDaoCR<UserEvent> {
	
	private static final Log LOG = LogFactory.getLog(UserEventDAO.class);
	
	private static final String COLUMN_NAME_SILENCED = "silenced";
	private static final int 	COLUMN_INDEX_SILENCED = 2;
	private static final String COLUMN_NAME_EVENT_ID = "eventId";
	private static final int    COLUMN_INDEX_EVENT_ID =  3;
	private static final String COLUMN_NAME_USER_ID = "userId";
	private static final int    COLUMN_INDEX_USER_ID = 1;
	
	// @formatter:off
	
	private static final String USER_EVENT_SELECT=""	
			+"select "
				+COLUMN_NAME_EVENT_ID+","
				+COLUMN_NAME_SILENCED+","
				+COLUMN_NAME_USER_ID+" "
			+"from "+
				"userevents ";
	
	private static final String USER_EVENT_SELECT_BASE_ON_PK = ""+
			USER_EVENT_SELECT
			+"where "
				+COLUMN_NAME_EVENT_ID+"=? and "
				+COLUMN_NAME_USER_ID+"=?";
	
	private static final String USER_EVENT_INSERT="" +
			"insert into userEvents ("
			    +COLUMN_NAME_EVENT_ID+","
			    +COLUMN_NAME_USER_ID+","
			    +COLUMN_NAME_SILENCED+") "
			+ "select e.id, ?, ? FROM events e where e.id = ?;";

	
	private static final String USER_EVENT_ACK ="" +
			"update "
				+"userEvents set "
				+ COLUMN_NAME_SILENCED+"=? " 
			+"where "
				+ COLUMN_NAME_EVENT_ID+"=?";

	private static final String USER_EVENT_SILENCE ="" +
			"UPDATE "
			+"userEvents SET "
			+ COLUMN_NAME_SILENCED+"='Y' "
			+" WHERE "
			+ COLUMN_NAME_USER_ID+"=? AND "
			+ COLUMN_NAME_EVENT_ID+"=?";

	private static final String USER_EVENT_UNSILENCE ="" +
			"UPDATE "
			+"userEvents SET "
			+ COLUMN_NAME_SILENCED+"='N' "
			+" WHERE "
			+ COLUMN_NAME_USER_ID+"=? AND "
			+ COLUMN_NAME_EVENT_ID+"=?";

	private static final String USER_EVENTS_SILENCE ="" +
			"INSERT INTO userEvents (" +
			COLUMN_NAME_EVENT_ID + ", " +
			COLUMN_NAME_USER_ID + ", " +
			COLUMN_NAME_SILENCED +
			")" +
			" VALUES (?,?,'Y') ON DUPLICATE KEY UPDATE " +
			COLUMN_NAME_SILENCED + " = VALUES(" + COLUMN_NAME_SILENCED + "); ";

	private static final String USER_EVENTS_UNSILENCE ="" +
			"INSERT INTO userEvents (" +
			COLUMN_NAME_EVENT_ID + ", " +
			COLUMN_NAME_USER_ID + ", " +
			COLUMN_NAME_SILENCED +
			")" +
			" VALUES (?,?,'N') ON DUPLICATE KEY UPDATE " +
			COLUMN_NAME_SILENCED + " = VALUES(" + COLUMN_NAME_SILENCED + "); ";

	private static final String USER_EVENT_DELETE = ""
			+ "delete from userEvents where "
				+ COLUMN_NAME_USER_ID + "=? ";
	// @formatter:onn
	
	// RowMapper
	private class UserEventRowMapper implements RowMapper<UserEvent> {
		public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserEvent userEvent;

			userEvent = new UserEvent();
			userEvent.setEventId(rs.getLong(COLUMN_NAME_EVENT_ID));
			userEvent.setUserId(rs.getLong(COLUMN_NAME_USER_ID));
			userEvent.setSilenced(DAO.charToBool(rs.getString(COLUMN_NAME_SILENCED)));
						
			return userEvent;
			
		}
	}


	@Override
	public List<UserEvent> findAllWithUserName() {
		return null;
	}

	@Override
	public List<UserEvent> findAll() {
		return (List<UserEvent>) DAO.getInstance().getJdbcTemp().query(USER_EVENT_SELECT, new Object[]{ }, new UserEventRowMapper());
	}

	@Override
	public UserEvent findById(Object[] pk) {
		return ((List<UserEvent>) DAO.getInstance().getJdbcTemp().query(USER_EVENT_SELECT_BASE_ON_PK, pk, new UserEventRowMapper())).get(0);
	}

	@Override
	public List<UserEvent> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		Object[] args;
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+" ? ";
			args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
		} else {
			args=argsFilter;
		}
	
		return (List<UserEvent>) DAO.getInstance().getJdbcTemp().query(USER_EVENT_SELECT+" where "+ filter + myLimit, args, new UserEventRowMapper());
	}

	@Override
	public Object[] create(UserEvent entity) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace(entity);
		}
		
		DAO.getInstance().getJdbcTemp().update(USER_EVENT_INSERT,  new Object[] {
			 						entity.getUserId(),
			 						DAO.boolToChar(entity.isSilenced()),
									entity.getEventId(),
			 				});

		return new Object[] {entity.getEventId(), entity.getUserId()};
		
	}
	
	public void batchUpdate(final int eventId,	final List<Integer> userIds, final boolean alarm) {

		DAO.getInstance().getJdbcTemp().batchUpdate(USER_EVENT_INSERT, new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return userIds.size();
			}
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(COLUMN_INDEX_EVENT_ID, eventId);
				ps.setInt(COLUMN_INDEX_USER_ID, userIds.get(i));
				ps.setString(COLUMN_INDEX_SILENCED, DAO.boolToChar(!alarm));
			}
		  });
	}

	public void updateAck(long eventId, boolean silenced ) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("eventId:"+eventId);
		}
				
		DAO.getInstance().getJdbcTemp().update( USER_EVENT_ACK, new Object[]  { DAO.boolToChar(silenced), eventId } );
	}

	public void silenceEvent(long eventId, int userId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("eventId:"+eventId);
		}

		DAO.getInstance().getJdbcTemp().update( USER_EVENT_SILENCE, new Object[]  { userId, eventId } );
	}

	public void unsilenceEvent(long eventId, int userId) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("eventId:"+eventId);
		}

		DAO.getInstance().getJdbcTemp().update( USER_EVENT_UNSILENCE, new Object[]  { userId, eventId } );
	}

	public void silenceEvents(List<Integer> eventIds, int userId) {
		String ids = eventIds.stream().map(String::valueOf).collect(Collectors.joining(",")) ;
		if (LOG.isTraceEnabled()) {
			LOG.trace("eventId: "+ids);
		}
		for (int eventId :eventIds) {
			DAO.getInstance().getJdbcTemp().update( USER_EVENTS_SILENCE, new Object[]  { eventId, userId});
		}
	}

	public void unsilenceEvents(List<Integer> eventIds, int userId) {
		String ids = eventIds.stream().map(String::valueOf).collect(Collectors.joining(",")) ;
		if (LOG.isTraceEnabled()) {
			LOG.trace("eventId: "+ids);
		}
		for (int eventId :eventIds) {
			DAO.getInstance().getJdbcTemp().update( USER_EVENTS_UNSILENCE, new Object[]  { eventId, userId});
		}
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int userId) userId:" + userId);
		}

		DAO.getInstance().getJdbcTemp().update(USER_EVENT_DELETE, new Object[]{userId});
	}

}
