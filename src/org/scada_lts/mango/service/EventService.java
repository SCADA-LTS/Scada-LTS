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

package org.scada_lts.mango.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.scada_lts.cache.PendingEventsCache;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.dao.model.event.Event;
import org.scada_lts.mango.adapter.MangoEvent;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;

/** 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class EventService implements MangoEvent {
	
	private static final Log LOG = LogFactory.getLog(EventService.class);
	private static final int MAX_PENDING_EVENTS = 100;
	
	private EventDAO eventDAO;
	private UserEventDAO userEventDAO;
	
	private Event mapping(EventInstance event) {
		return null;
	}
	
	private List<EventInstance> mapping(List<Event> lst){
		ArrayList<EventInstance> lst1 = new ArrayList<EventInstance>();
		return lst1;
	}
	
	class UserPendingEventRetriever implements Runnable {
		private final int userId;

		UserPendingEventRetriever(int userId) {
			this.userId = userId;
		}

		@Override
		public void run() {
			addToCache(
					userId,
					getPendingEvents(EventType.EventSources.DATA_POINT, -1,
							userId));
		}
	}

	
	public EventService() {
		eventDAO = new EventDAO();
		userEventDAO = new UserEventDAO();
	}
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void ackEvent(int eventId, long time, int userId, int alternateAckSource, boolean signalAlarmLevelChange) {
		
		eventDAO.updateAck(time, userId, alternateAckSource, eventId);
		// true silenced
		userEventDAO.updateAck(eventId, true);
		
		//clearCache();
		if( signalAlarmLevelChange ) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(System.currentTimeMillis());
			Common.ctx.getEventManager().notifyEventAck(eventId,  userId);
		}
		
	}
	
	@Override
	public void ackEvent(int eventId, long time, int userId, int alternateAckSource) {
		ackEvent(eventId, time, userId, alternateAckSource, true);
	}
	
	@Override
	public void insertUserEvents(int eventId, List<Integer> userIds, boolean alarm) {
		userEventDAO.batchUpdate(eventId, userIds, alarm);
	}
	
	@Override
	public List<EventInstance> getActiveEvents() {
		List<EventInstance> result = mapping(eventDAO.filtered(EventDAO.EVENT_ACTIVE, new Object[]{DAO.boolToChar(true)}, EventDAO.NO_LIMIT));
		attachRelationInfo(result); 
		return result;
	}
	
	@Override
	public List<EventInstance> getEventsForDataPoint(int dataPointId, int userId) {
		return mapping(eventDAO.getEventsForDataPoint(dataPointId, userId));
	}

	@Override
	public List<EventInstance> getPendingEventsForDataPoint(int dataPointId, int userId) {
		// Check the cache
		List<EventInstance> userEvents = getFromCache(userId);
		if (userEvents == null) {
			// This is a potentially long running query, so run it offline.
			userEvents = Collections.emptyList();
			addToCache(userId, userEvents);
			//TODO rewrite to delete relation of seroUtils
			Common.timer.execute(new UserPendingEventRetriever(userId));
		}
		List<EventInstance> list = null;
		for (EventInstance e : userEvents) {
			if (e.getEventType().getDataPointId() == dataPointId) {
				if (list == null)
					list = new ArrayList<EventInstance>();
				list.add(e);
			}
		}
		if (list == null)
			return Collections.emptyList();
		return list;		
	}

	@Override
	public List<EventInstance> getPendingEventsForDataSource(int dataSourceId, int userId) {	
		return getPendingEvents(EventType.EventSources.DATA_SOURCE, dataSourceId, userId);
	}	
	
	@Override
	public List<EventInstance> getPendingEventsForPublisher(int publisherId, int userId) {
		return getPendingEvents(EventType.EventSources.PUBLISHER, publisherId,
				userId);
	}
	
	@Override
	public List<EventInstance> getPendingEvents(int userId) {
		List<EventInstance> results = null;
		try {
			boolean cacheEnable = ScadaConfig.getInstance().getBoolean(ScadaConfig.ENABLE_CACHE, false);
			if (cacheEnable) {
			  results = PendingEventsCache.getInstance().getPendingEvents(userId);
			} else {
			
				results = mapping(eventDAO.getPendingEventsLimit(userId, MAX_PENDING_EVENTS));				
				attachRelationalInfo(results);
			}
		} catch (SchedulerException | IOException e) {
			LOG.error(e);	
		}
		return results;
	}
	
	@Override
	public void attachRelationalInfo(List<EventInstance> list) {
		//TODO very slow set in config disable this function.
		for (EventInstance e : list)
			attachRelationalInfo(e);
	}

	@Override
	public EventInstance insertEventComment(int eventId, UserComment comment) {
		
		new UserDAO().createComments(UserComment.TYPE_EVENT, eventId, comment);
		 
		//TODO getEventIns
		return null;
	}
	
	@Override
	public int purgeEventsBefore(long time) {
		return eventDAO.purgeEventsBefore(time);
	}
	
	@Override
	public int getEventCount() {
		return eventDAO.getEventCount();
	}
	
	@Override
	public List<EventInstance> searchOld(int eventId, int eventSourceType, String status, int alarmLevel,
			String[] keywords, int maxResults, int userId, ResourceBundle bundle) {
		return mapping(eventDAO.searchOld(eventId, eventSourceType, status, alarmLevel, keywords, maxResults, userId, bundle));
	}
	
	@Override
	public List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel,
			String[] keywords, int userId, ResourceBundle bundle, int from, int to, Date date) {
		
		return mapping(eventDAO.search(eventId, eventSourceType, status, alarmLevel, keywords,
				-1, -1, userId, bundle, from, to, date));
	}
	
	@Override
	public List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel,
			String[] keywords, long dateFrom, long dateTo, int userId, ResourceBundle bundle, int from, int to,
			Date date) {
		return mapping(eventDAO.search(eventId,eventSourceType,status,alarmLevel,keywords,dateFrom,dateTo,userId,bundle,from,to,date));
	}
	
	@Override
	public int getSearchRowCount() {
		return eventDAO.getSearchRowCount();
	}

	@Override
	public int getStartRow() {
		return eventDAO.getStartRow();
	}
	//-------------
	
	@Override
	public void saveEvent(EventInstance event) {
		
		if (event.getId() > 0 ) {
			eventDAO.updateCause(event.getRtnCause(), event.getRtnTimestamp());
		} else {
			eventDAO.create(mapping(event));
		}
		
	}

	@Override
	public List<EventInstance> getPendingEvents(int typeId, int typeRef1, int userId) {
		
		List<Event> lst;
		if (typeRef1 == -1) {
			lst = eventDAO.getPendingEvents(typeId, userId);
		} else {
			lst = eventDAO.getPendingEvents(typeId, typeRef1, userId);
		}
		attachRelationInfo(mapping(lst));
		
		return mapping(lst);
		
	}

	


	@Override
	public void attachRelationalInfo(EventInstance event) {
		// TODO Auto-generated method stub
		// very slow We not use
	}

	

	

	@Override
	public String generateUniqueXid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EventType getEventHandlerType(int handlerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventHandlerVO> getEventHandlers(EventType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventHandlerVO> getEventHandlers(EventTypeVO type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventHandlerVO> getEventHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventHandlerVO getEventHandler(int eventHandlerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventHandlerVO getEventHandler(String xid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventHandlerVO saveEventHandler(EventType type, EventHandlerVO handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventHandlerVO saveEventHandler(EventTypeVO type, EventHandlerVO handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateEventHandler(EventHandlerVO handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEventHandler(int handlerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean toggleSilence(int eventId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getHighestUnsilencedAlarmLevel(int userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<EventInstance> getFromCache(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToCache(int userId, List<EventInstance> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCache(EventInstance event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUserIdFromCache(int userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attachRelationInfo(List<EventInstance> list) {
		for (EventInstance e:list) {
			eventDAO.attacheRelationalInfo(mapping(e));
		}
		
	}

}
