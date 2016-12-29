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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.scada_lts.cache.PendingEventsCache;
import org.scada_lts.cache.UnsilencedAlarmCache;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.UserCommentDAO;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.mango.adapter.MangoEvent;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.AuditEventType;
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
	
	public EventService() {
		eventDAO = new EventDAO();
		userEventDAO = new UserEventDAO();
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

	@Override
	public void saveEvent(EventInstance event) {
		
		if (event.getId() == Common.NEW_ID ) {
			eventDAO.create(event);
			//TODO whay not have add to cache?
		} else {
			eventDAO.updateEvent(event);
			updateCache(event);
		}
	}
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void ackEvent(int eventId, long time, int userId, int alternateAckSource, boolean signalAlarmLevelChange) {
		
		eventDAO.updateAck(time, userId, alternateAckSource, eventId);
		// true silenced
		userEventDAO.updateAck(eventId, true);
		
		clearCache();
		
		//TODO check
		/*if( signalAlarmLevelChange ) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(System.currentTimeMillis());
			Common.ctx.getEventManager().notifyEventAck(eventId,  userId);
		}*/
		
	}
	
	@Override
	public void ackEvent(int eventId, long time, int userId, int alternateAckSource) {
		ackEvent(eventId, time, userId, alternateAckSource, true);
	}
	
	@Override
	public void insertUserEvents(int eventId, List<Integer> userIds, boolean alarm) {
		userEventDAO.batchUpdate(eventId, userIds, alarm);
		if (alarm) {
			for (int userId: userIds) {
				removeUserIdFromCache(userId);
			}
		}
	}
	
	@Override
	public List<EventInstance> getActiveEvents() {
		List<EventInstance> result = eventDAO.filtered(EventDAO.EVENT_FILTER_ACTIVE, new Object[]{DAO.boolToChar(true)}, EventDAO.NO_LIMIT);
		attachRelationInfo(result); 
		return result;
	}
	
	@Override
	public List<EventInstance> getPendingEvents(int typeId, int typeRef1, int userId) {
		
		List<EventInstance> lst;
		if (typeRef1 == -1) {
			lst = eventDAO.getPendingEvents(typeId, userId);
		} else {
			lst = eventDAO.getPendingEvents(typeId, typeRef1, userId);
		}
		attachRelationInfo(lst);
		
		return lst;
		
	}
	
	@Override
	public List<EventInstance> getEventsForDataPoint(int dataPointId, int userId) {
		return eventDAO.getEventsForDataPoint(dataPointId, userId);
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
			
				results = eventDAO.getPendingEventsLimit(userId, MAX_PENDING_EVENTS);				
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
		
		new UserCommentDAO().insert(comment, UserComment.TYPE_EVENT, eventId);
		 
		return eventDAO.findById(new Object[]{eventId});
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
		return eventDAO.searchOld(eventId, eventSourceType, status, alarmLevel, keywords, maxResults, userId, bundle);
	}
	
	@Override
	public List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel,
			String[] keywords, int userId, ResourceBundle bundle, int from, int to, Date date) {
		
		return eventDAO.search(eventId, eventSourceType, status, alarmLevel, keywords,
				-1, -1, userId, bundle, from, to, date);
	}
	
	@Override
	public List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel,
			String[] keywords, long dateFrom, long dateTo, int userId, ResourceBundle bundle, int from, int to,
			Date date) {
		return eventDAO.search(eventId,eventSourceType,status,alarmLevel,keywords,dateFrom,dateTo,userId,bundle,from,to,date);
	}
	
	@Override
	public int getSearchRowCount() {
		return eventDAO.getSearchRowCount();
	}
	
	@Override
	public int getStartRow() {
		return eventDAO.getStartRow();
	}
	
	@Override
	public EventType getEventHandlerType(int handlerId) {
		return eventDAO.getEventHandlerType(handlerId);
	}
	
	@Override
	public List<EventHandlerVO> getEventHandlers(EventType type) {
		return eventDAO.getEventHandlers(type.getEventSourceId(), type.getReferenceId1(), type.getReferenceId2());
	}
	
	@Override
	public List<EventHandlerVO> getEventHandlers(EventTypeVO type) {
		return eventDAO.getEventHandlers(type.getTypeId(), type.getTypeRef1(), type.getTypeRef2());
	}
	
	@Override
	public List<EventHandlerVO> getEventHandlers() {
		return eventDAO.getEventHandlers();
	}
	
	@Override
	public EventHandlerVO getEventHandler(int eventHandlerId) {
		return eventDAO.getEventHandler(eventHandlerId);
	} 
	
	@Override
	public EventHandlerVO getEventHandler(String xid) {
		return eventDAO.getEventHandler(xid);
	}
	
	@Override
	public EventHandlerVO saveEventHandler(final EventType type, final EventHandlerVO handler) {
		if (type == null) {
			return eventDAO.saveEventHandler(0, 0, 0, handler);
		} else {
			return eventDAO.saveEventHandler(type.getEventSourceId(),
				type.getReferenceId1(), type.getReferenceId2(), handler);
		}
	}
	
	@Override
	public EventHandlerVO saveEventHandler(final EventTypeVO type, final EventHandlerVO handler) {
		if (type == null) {
			return eventDAO.saveEventHandler(0, 0, 0, handler);
		} else {
			return eventDAO.saveEventHandler(type.getTypeId(),
				type.getTypeRef1(), type.getTypeRef2(), handler);
		}
	}
	
	@Override
	public void deleteEventHandler(final int handlerId) {
		EventHandlerVO handler = getEventHandler(handlerId);
		
		eventDAO.delete(handlerId);
		
		AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_EVENT_HANDLER,	handler);
	}
	
	@Override
	public boolean toggleSilence(int eventId, int userId) {		
		boolean silenced;
		Boolean updated=false;
		silenced = eventDAO.toggleSilence(eventId, userId, updated);
		if (updated) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(System.currentTimeMillis());
			Common.ctx.getEventManager().notifyEventToggle(eventId,  userId, silenced);
		}
		return silenced;
	}
	
	public int getHighestUnsilencedAlarmLevel(int userId) {		
		int result = -1;
		try {
			boolean cacheEnable = ScadaConfig.getInstance().getBoolean(ScadaConfig.ENABLE_CACHE, false);
			if (cacheEnable) {
				result = UnsilencedAlarmCache.getInstance().getHighestUnsilencedAlarmLevel(userId);
			} else {
				result = new EventDAO().getHighestUnsilencedAlarmLevel(userId);
			}
		} catch (SchedulerException | IOException e) {
			LOG.error(e);	
		}		
		return result;
	}
	
	@Override
	public void attachRelationalInfo(EventInstance event) {
		//TODO very slow We not use
		eventDAO.attachRelationalInfo(event);
	}
	
	@Override
	public void insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler) {
		eventDAO.insertEventHandler(typeId, typeRef1, typeRef2, handler);
		AuditEventType.raiseAddedEvent(AuditEventType.TYPE_EVENT_HANDLER, handler);
	}

	@Override
	public void updateEventHandler(EventHandlerVO handler) {
		EventHandlerVO old = getEventHandler(handler.getId());
		eventDAO.updateEventHandler(handler);
		AuditEventType.raiseChangedEvent(AuditEventType.TYPE_EVENT_HANDLER,
				old, handler);
	}
		
	@Override
	public void attachRelationInfo(List<EventInstance> list) {
		for (EventInstance e:list) {
			eventDAO.attachRelationalInfo(e);
		}
		
	}

	public void updateEventAckUserId(int userId) {
		eventDAO.updateEventAckUserId(userId);
	}

	public void deleteUserEvent(int userId) {
		userEventDAO.delete(userId);
	}
	
	// cache 

	//
	// /
	// / Pending event caching
	// /
	// TODO rewrite
	//
	
	private static Map<Integer, PendingEventCacheEntry> pendingEventCache = new ConcurrentHashMap<Integer, PendingEventCacheEntry>();
	
	private static final long CACHE_TTL = 300000; // 5 minutes
	
	static class PendingEventCacheEntry {
		private final List<EventInstance> list;
		private final long createTime;

		public PendingEventCacheEntry(List<EventInstance> list) {
			this.list = list;
			createTime = System.currentTimeMillis();
		}

		public List<EventInstance> getList() {
			return list;
		}

		public boolean hasExpired() {
			return System.currentTimeMillis() - createTime > CACHE_TTL;
		}
	}

	public static List<EventInstance> getFromCache(int userId) {
		PendingEventCacheEntry entry = pendingEventCache.get(userId);
		if (entry == null)
			return null;
		if (entry.hasExpired()) {
			pendingEventCache.remove(userId);
			return null;
		}
		return entry.getList();
	}

	public static void addToCache(int userId, List<EventInstance> list) {
		pendingEventCache.put(userId, new PendingEventCacheEntry(list));
	}

	public static void updateCache(EventInstance event) {
		if (event.isAlarm()
				&& event.getEventType().getEventSourceId() == EventType.EventSources.DATA_POINT)
			pendingEventCache.clear();
	}

	public static void removeUserIdFromCache(int userId) {
		pendingEventCache.remove(userId);
	}

	public static void clearCache() {
		pendingEventCache.clear();
	}

}
