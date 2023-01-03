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
package com.serotonin.mango.db.dao;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.scada_lts.dao.impl.DAO;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.mango.service.EventService;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;

/**
 * Rewrite (ultimately to remove) and only use EventService
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class EventDao  {
	
	private MangoEvent eventService = new EventService();

	public void saveEvent(EventInstance event) {
		eventService.saveEvent(event);
	}

	public void ackEvent(int eventId, long time, int userId,
			int alternateAckSource, boolean signalAlarmLevelChange) {
		eventService.ackEvent(eventId, time, userId, alternateAckSource);
	}
	
	public void ackEvent(int eventId, long time, int userId,
			int alternateAckSource) {
		eventService.ackEvent(eventId, time, userId, alternateAckSource);
	}

	public void insertUserEvents(final int eventId,
			final List<Integer> userIds, final boolean alarm) {
		eventService.insertUserEvents(eventId, userIds, alarm);
	}

	public List<EventInstance> getActiveEvents() {
		return eventService.getActiveEvents();
	}

	public List<EventInstance> getEventsForDataPoint(int dataPointId, int userId) {
		return eventService.getEventsForDataPoint(dataPointId, userId);
	}

	public List<EventInstance> getPendingEventsForDataPoint(int dataPointId,
			int userId) {
		return eventService.getPendingEventsForDataPoint(dataPointId, userId);
	}

	public List<EventInstance> getPendingEventsForDataSource(int dataSourceId,
			int userId) {
		return eventService.getPendingEvents(EventType.EventSources.DATA_SOURCE,
				dataSourceId, userId);
	}

	public List<EventInstance> getPendingEventsForPublisher(int publisherId,
			int userId) {
		return eventService.getPendingEvents(EventType.EventSources.PUBLISHER, publisherId,
				userId);
	}

	public List<EventInstance> getPendingEvents(int userId) {
		return eventService.getPendingEvents(userId);
	}

	public void attachRelationalInfo(List<EventInstance> list) {
		eventService.attachRelationalInfo(list);
	}

	public EventInstance insertEventComment(int eventId, UserComment comment) {
		return eventService.insertEventComment(eventId, comment);
	}

	public int purgeEventsBefore(final long time) {
		return eventService.purgeEventsBefore(time);
	}

	public int getEventCount() {
		return eventService.getEventCount();
	}

	public List<EventInstance> searchOld(int eventId, int eventSourceType,
			String status, int alarmLevel, final String[] keywords,
			final int maxResults, int userId, final ResourceBundle bundle) {
		return eventService.searchOld(eventId, eventSourceType, status, alarmLevel, keywords, maxResults, userId, bundle);
		
	}

	public List<EventInstance> search(int eventId, int eventSourceType,
			String status, int alarmLevel, final String[] keywords, int userId,
			final ResourceBundle bundle, final int from, final int to,
			final Date date) {
		return eventService.search(eventId, eventSourceType, status, alarmLevel, keywords, userId, bundle, from, to, date);

	}

	public List<EventInstance> search(int eventId, int eventSourceType,
			String status, int alarmLevel, final String[] keywords,
			long dateFrom, long dateTo, int userId,
			final ResourceBundle bundle, final int from, final int to,
			final Date date) {
		
		return eventService.search(eventId, eventSourceType, status, alarmLevel, keywords, dateFrom, dateTo, userId, bundle, from, to, date);
	}

	public int getSearchRowCount() {
		return eventService.getSearchRowCount();
	}

	public int getStartRow() {
		return eventService.getStartRow();
	}

	//
	// /
	// / Event handlers
	// /
	//
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(EventHandlerVO.XID_PREFIX, "eventHandlers");
	}

	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "eventHandlers");
	}

	public EventType getEventHandlerType(int handlerId) {
		return eventService.getEventHandlerType(handlerId);
	}

	public List<EventHandlerVO> getEventHandlers(EventType type) {
		return eventService.getEventHandlers(type);
	}

	public List<EventHandlerVO> getEventHandlers(EventTypeVO type) {
		return eventService.getEventHandlers(type);		
	}

	public List<EventHandlerVO> getEventHandlers() {
		return eventService.getEventHandlers();
	}

	public EventHandlerVO getEventHandler(int eventHandlerId) {
		return eventService.getEventHandler(eventHandlerId);
	}

	public EventHandlerVO getEventHandler(String xid) {
		return eventService.getEventHandler(xid);
	}

	public EventHandlerVO saveEventHandler(final EventType type,
			final EventHandlerVO handler) {
		return eventService.saveEventHandler(type, handler);
	}

	public EventHandlerVO saveEventHandler(final EventTypeVO type,
			final EventHandlerVO handler) {
		return eventService.saveEventHandler(type, handler);
	}

	public void deleteEventHandler(final int handlerId) {
		eventService.deleteEventHandler(handlerId);
	}
	
	public boolean toggleSilence(int eventId, int userId) {
		return eventService.toggleSilence(eventId, userId);
	}

	@Deprecated
	public int getHighestUnsilencedAlarmLevel(int userId) {		
		return eventService.getHighestUnsilencedAlarmLevel(userId);
	}

	public static List<EventInstance> getFromCache(int userId) {
		return EventService.getFromCache(userId);
	}

	public static void addToCache(int userId, List<EventInstance> list) {
		EventService.addToCache(userId, list);
	}

	public static void updateCache(EventInstance event) {
		EventService.updateCache(event);
	}

	public static void removeUserIdFromCache(int userId) {
		EventService.removeUserIdFromCache(userId);
	}

	public static void clearCache() {
		EventService.clearCache();
	}
	
	
}
