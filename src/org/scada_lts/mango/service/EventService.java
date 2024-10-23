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

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.AuditEventUtils;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.PendingEventsCache;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.IUserCommentDAO;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.utils.SQLPageWithTotal;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.api.dto.EventCommentDTO;
import org.scada_lts.web.mvc.api.dto.EventDTO;
import org.scada_lts.web.mvc.api.dto.eventHandler.EventHandlerPlcDTO;
import org.scada_lts.web.mvc.api.json.JsonEventSearch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
@Service
public class EventService implements MangoEvent {
	
	private static final Log LOG = LogFactory.getLog(EventService.class);
	
	private final EventDAO eventDAO;
	private final UserEventDAO userEventDAO;
	private final IUserCommentDAO userCommentDAO;
	private final SystemSettingsService systemSettingsService;
	
	public EventService() {
		eventDAO = new EventDAO();
		userEventDAO = new UserEventDAO();
		userCommentDAO = ApplicationBeans.getUserCommentDaoBean();
		systemSettingsService = ApplicationBeans.getBean("systemSettingsService", SystemSettingsService.class);
	}

	class UserPendingEventRetriever extends AbstractBeforeAfterWorkItem implements Runnable {
		private final int userId;
		private final String details;

		@Deprecated
		public UserPendingEventRetriever(int userId) {
			this.userId = userId;
			this.details = "";
		}

		UserPendingEventRetriever(int userId, String details) {
			this.userId = userId;
			this.details = details;
		}

		@Override
		public void run() {
			super.execute();
		}

		@Override
		public void work() {
			addToCache(
					userId,
					getPendingEvents(EventType.EventSources.DATA_POINT, -1,
							userId));
		}

		@Override
		public WorkItemPriority getPriorityType() {
			return WorkItemPriority.HIGH;
		}

		@Override
		public String toString() {
			return "UserPendingEventRetriever{" +
					"userId=" + userId +
					", details='" + details + '\'' +
					'}';
		}

		@Override
		public String getDetails() {
			return this.toString();
		}
	}

	@Override
	public void saveEvent(EventInstance event) {
		
		if (event.getId() == Common.NEW_ID ) {
			if ( event.getAlarmLevel() != AlarmLevels.NONE ) {
				eventDAO.create(event);
				//TODO whay not have add to cache?
			}
		} else {
			eventDAO.updateEvent(event);
			updateCache(event);
		}
	}

	@Deprecated(since = "2.8.0")
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void ackEvent(int eventId, long time, int userId, int alternateAckSource, boolean signalAlarmLevelChange) {
		_ackEvent(eventId, time, userId, alternateAckSource, signalAlarmLevelChange);
	}

	@Deprecated(since = "2.8.0")
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void ackEvent(int eventId, long time, int userId, int alternateAckSource) {
		_ackEvent(eventId, time, userId, alternateAckSource, true);
	}

	@Override
	public void silenceEvent(int eventId, int userId) {
		userEventDAO.silenceEvent(eventId, userId);
	}

	@Override
	public void unsilenceEvent(int eventId, int userId) {
		userEventDAO.unsilenceEvent(eventId, userId);
	}

	@Override
	public void silenceEvents(List<Integer> eventIds, int userId) {
		userEventDAO.silenceEvents(eventIds, userId);
	}

	@Override
	public void unsilenceEvents(List<Integer> eventIds, int userId) {
		userEventDAO.unsilenceEvents(eventIds, userId);
	}

	@Override
	public void ackAllPending(long time, int userId, int alternateAckSource) {
		MangoEvent eventService = new EventService();
		UserService userEvent = new UserService();
		User user = userEvent.getUser(userId);
		for (EventInstance evt : eventService.getPendingEvents(user.getId())) {
			if(!evt.isActive())
				eventService.ackEvent(evt, time, user, alternateAckSource);
		}
	}

	@Override
	public void silenceAll(int userId) {
		eventDAO.silenceEvents(userId);
	}

	@Override
	public void ackSelected(long time, int userId, int alternateAckSource, List<Integer> ids) {
		eventDAO.ackAllPendingSelected(time, userId, alternateAckSource, ids);
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
		int limit = systemSettingsService.getMiscSettings().getEventPendingLimit();
		if (typeRef1 == -1) {
			lst = eventDAO.getPendingEventsLimit(typeId, userId, limit);
		} else {
			lst = eventDAO.getPendingEventsLimit(typeId, typeRef1, userId, limit);
		}
		attachRelationInfo(lst);
		
		return lst;
		
	}

	@Override
	public List<EventInstance> getEventsForDataPoint(int dataPointId, int userId) {
		int limit = systemSettingsService.getMiscSettings().getEventPendingLimit();
		List<EventInstance> lst = eventDAO.getEventsForDataPointLimit(dataPointId, userId, limit);
		attachRelationInfo(lst);
		return lst;
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
			UserPendingEventRetriever userPendingEventRetriever = new UserPendingEventRetriever(userId, "dataPointId: " + dataPointId);
			Common.ctx.getBackgroundProcessing().addWorkItem(userPendingEventRetriever);
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
		int limit = systemSettingsService.getMiscSettings().getEventPendingLimit();
		return getPendingEventsAlarmLevelMin(userId, -1, limit);
	}
	@Override
	public List<EventInstance> getPendingEventsAlarmLevelMin(int userId, int alarmLevelMin, int limit) {
		List<EventInstance> results = null;
		try {
			boolean cacheEnable = systemSettingsService.getMiscSettings().isEventPendingCacheEnabled();
			int fromSystemSettingsLimit = systemSettingsService.getMiscSettings().getEventPendingLimit();
			int calcLimit = limit > -1 && limit <= fromSystemSettingsLimit ? limit : fromSystemSettingsLimit;
			if (cacheEnable) {
				PendingEventsCache.getInstance().startUpdate();
				results = PendingEventsCache.getInstance().getPendingEvents(userId).stream()
						.sorted(Comparator.comparing(EventInstance::getActiveTimestamp).reversed())
						.filter(a -> alarmLevelMin < 0 || a.getAlarmLevel() >= alarmLevelMin)
						.limit(calcLimit)
						.collect(Collectors.toList());
			} else {
				PendingEventsCache.getInstance().stopUpdate();
				if(alarmLevelMin > 0) {
					results = eventDAO.getPendingEventsLimitAlarmLevelMin(userId, alarmLevelMin, calcLimit);
				} else {
					results = eventDAO.getPendingEventsLimit(userId, calcLimit);
				}
				attachRelationalInfo(results);
			}
		} catch (IOException e) {
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
		userCommentDAO.insert(comment, UserComment.TYPE_EVENT, eventId);
		return eventDAO.findById(new Object[]{eventId});
	}
	
	@Override
	public int purgeEventsBefore(long time) {
		int result = eventDAO.purgeEventsBefore(time);
		Common.ctx.getEventManager().notifyEventReset();
		return result;
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

	public List<EventHandlerPlcDTO> getPlcEventHandlers() { return eventDAO.getPlcEventHandlers(); }

	public List<EventHandlerPlcDTO> getEventHandlersByDatapointId(int datapointId) {
		return eventDAO.getEventHandlersByDatapointId(datapointId);
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
	public EventHandlerVO saveEventHandler(EventTypeVO type, EventHandlerVO handler) {
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

		AuditEventUtils.raiseDeletedEvent(AuditEventType.TYPE_EVENT_HANDLER,	handler);
		Common.ctx.getEventManager().cancelEventsForHandler(handlerId);
	}

	public void deleteEventHandler(final String handlerXid) {
		EventHandlerVO handler = getEventHandler(handlerXid);
		eventDAO.delete(handler.getId());
		AuditEventUtils.raiseDeletedEvent(AuditEventType.TYPE_EVENT_HANDLER, handler);
		Common.ctx.getEventManager().cancelEventsForHandler(handler.getId());
	}
	
	@Override
	@Deprecated(since = "2.8.0")
	public boolean toggleSilence(int eventId, int userId) {
		boolean updated = eventDAO.toggleSilence(eventId, userId, false);
		if (updated) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(System.currentTimeMillis());
			Common.ctx.getEventManager().notifyEventToggle(eventId, userId);
		} else {
			Common.ctx.getEventManager().notifyEventRaise(eventId, userId);
		}
		return updated;
	}

	@Override
	public int getHighestUnsilencedAlarmLevel(int userId) {
		return Common.ctx.getEventManager().getHighestAlarmLevel(userId);
	}
	
	@Override
	public void attachRelationalInfo(EventInstance event) {
		//TODO very slow We not use
		List<UserComment> lstUserComments = userCommentDAO.getEventComments(event);
		event.setEventComments(lstUserComments);
	}

	@Override
	public void insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler) {
		eventDAO.insertEventHandler(typeId, typeRef1, typeRef2, handler);
		AuditEventUtils.raiseAddedEvent(AuditEventType.TYPE_EVENT_HANDLER, handler);
	}

	@Override
	public void updateEventHandler(EventHandlerVO handler) {
		EventHandlerVO old = getEventHandler(handler.getId());
		eventDAO.updateEventHandler(handler);
		AuditEventUtils.raiseChangedEvent(AuditEventType.TYPE_EVENT_HANDLER,
				old, handler);
	}
		
	@Override
	public void attachRelationInfo(List<EventInstance>  events) {
		for (EventInstance event: events) {
			attachRelationalInfo(event);
		}
		
	}

	@Override
	public EventInstance getEvent(int eventId) {
		return eventDAO.findById(new Object[]{eventId});
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

	public List<EventDTO> getDataPointEventsWithLimit(int datapointId, int limit, int offset) {
		return eventDAO.findEventsWithLimit(EventType.EventSources.DATA_POINT, datapointId, limit, offset);
	}

	public SQLPageWithTotal<EventDTO> getEventsWithLimit(JsonEventSearch query, User user) {
		return eventDAO.findEvents(query, user);
	}

	@Deprecated(since = "2.8.0")
	private void notifyEventAck(int eventId) {
		Common.ctx.getEventManager().notifyEventAck(eventId);
  }

	public List<EventCommentDTO> findCommentsByEventId(int eventId) {
		return eventDAO.findCommentsByEventId(eventId);
	}

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(EventHandlerVO.XID_PREFIX, "eventHandlers");
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "eventHandlers");
	}

	@Deprecated(since = "2.8.0")
	private void _ackEvent(int eventId, long time, int userId, int alternateAckSource, boolean signalAlarmLevelChange) {
		eventDAO.updateAck(time, userId, alternateAckSource, eventId);
		// true silenced
		userEventDAO.updateAck(eventId, true);

		clearCache();
		notifyEventAck(eventId);
	}

	@Override
	public boolean toggleSilence(EventInstance event, User user) {
		boolean updated = eventDAO.toggleSilence(event.getId(), user.getId(), true);
		if (updated) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(System.currentTimeMillis());
			Common.ctx.getEventManager().notifyEventToggle(event, user);
		} else if(!event.isAssignee()) {
			Common.ctx.getEventManager().notifyEventRaise(event, user);
		}
		return updated;
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void ackEvent(EventInstance event, long time, User user, int alternateAckSource, boolean signalAlarmLevelChange) {
		_ackEvent(event, time, user, alternateAckSource, signalAlarmLevelChange);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public void ackEvent(EventInstance event, long time, User user, int alternateAckSource) {
		_ackEvent(event, time, user, alternateAckSource, true);
	}

	@Override
	public boolean assignEvent(EventInstance event, User user) {
		if(!isAssignPermission(event, user)) {
			return false;
		}
		long time = System.currentTimeMillis();
		boolean updated = eventDAO.assign(event.getId(), time, user);
		if (updated) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(time);
			Common.ctx.getEventManager().notifyEventAssignee(event);
			removeUserIdFromCache(user.getId());
			SystemEventType.raiseEvent(new SystemEventType(SystemEventType.TYPE_ASSIGNED_EVENT), time, false,
					new LocalizableMessage("events.assignedBy", LoggingUtils.userInfo(user), LoggingUtils.eventInfo(event)));
		} else {
			Common.ctx.getEventManager().notifyEventRaise(event);
		}
		return updated;
	}

	@Override
	public boolean unassignEvent(EventInstance event, User user) {
		if(!isAssignPermission(event, user)) {
			return false;
		}
		long time = System.currentTimeMillis();
		boolean updated = eventDAO.unassign(event.getId());
		if (updated) {
			Common.ctx.getEventManager().setLastAlarmTimestamp(time);
			removeUserIdFromCache(user.getId());
			SystemEventType.raiseEvent(new SystemEventType(SystemEventType.TYPE_UNASSIGNED_EVENT), time, false,
					new LocalizableMessage("events.unassignedBy", event.getAssigneeUsername(), LoggingUtils.userInfo(user),
							LoggingUtils.eventInfo(event)));
		}
		boolean silence = eventDAO.isSilence(event.getId(), user.getId());
		if(!silence) {
			Common.ctx.getEventManager().notifyEventRaise(event);
		}
		return updated;
	}

	@Override
	public void unassignEvents() {
		eventDAO.unassignEvents();
		Common.ctx.getEventManager().notifyEventReset();
	}

	@Override
	public void ackEvents(User user) {
		long now = System.currentTimeMillis();
		if(user.isAdmin()) {
			eventDAO.ackEvents(now, user.getId(), 0);
			clearCache();
			Common.ctx.getEventManager().notifyEventReset();
		} else {
			for (EventInstance evt : getPendingEvents(user.getId())) {
				if(!evt.isActive())
					ackEvent(evt, now, user, 0);
			}
		}
	}

	@Override
	public List<Integer> silenceEvents(User user) {
		List<Integer> silenced = new ArrayList<>();
		if(user.isAdmin()) {
			eventDAO.silenceEvents(user.getId());
			clearCache();
			Common.ctx.getEventManager().notifyEventReset();
		} else {
			for (EventInstance evt : getPendingEvents(user.getId())) {
				if (!evt.isSilenced()) {
					toggleSilence(evt, user);
					silenced.add(evt.getId());
				}
			}
		}
		return silenced;
	}

	private static boolean isAssignPermission(EventInstance event, User user) {
		return !event.isAssignee() || (user.isAdmin() || Objects.equals(user.getUsername(), event.getAssigneeUsername()));
	}

	private void notifyEventAck(EventInstance event) {
		Common.ctx.getEventManager().notifyEventAck(event);
	}

	private void _ackEvent(EventInstance event, long time, User user, int alternateAckSource, boolean signalAlarmLevelChange) {
		if(event.isActive()) {
			LOG.warn("Event is active! This event cannot be acknowledged.");
			return;
		}
		eventDAO.updateAck(time, user.getId(), alternateAckSource, event.getId());
		// true silenced
		userEventDAO.updateAck(event.getId(), true);

		clearCache();
		notifyEventAck(event);
	}
}
