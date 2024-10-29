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

package org.scada_lts.mango.adapter;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;

/** 
 * Adapter for EventService
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public interface MangoEvent {
	
	void saveEvent(EventInstance event);

	@Deprecated(since = "2.8.0")
	void ackEvent(int eventId, long time, int userId, int alternateAckSource, boolean signalAlarmLevelChange);

	@Deprecated(since = "2.8.0")
	void ackEvent(int eventId, long time, int userId, int alternateAckSource);

    void silenceEvent(int eventId, int userId);

	void unsilenceEvent(int eventId, int userId);

    void silenceEvents(List<Integer> eventIds, int userId);

	void unsilenceEvents(List<Integer> eventIds, int userId);

	@Deprecated(since = "2.8.0")
	void ackAllPending(long time, int userId, int alternateAckSource);

	@Deprecated(since = "2.8.0")
    void silenceAll(int userId);

    void ackSelected(long time, int userId, int alternateAckSource, List<Integer> ids);

    //TODO userIds should be the List<Long>
	void insertUserEvents(final int eventId, final List<Integer> userIds, final boolean alarm);
	
	void attachRelationInfo(List<EventInstance> list);
	
	List<EventInstance> getActiveEvents();
	
	List<EventInstance> getEventsForDataPoint(int dataPointId, int userId);
	
	List<EventInstance> getPendingEventsForDataPoint(int dataPointId, int userId);
	
	List<EventInstance> getPendingEventsForDataSource(int dataSourceId,	int userId);
	
	List<EventInstance> getPendingEventsForPublisher(int publisherId, int userId);
	
	List<EventInstance> getPendingEvents(int typeId, int typeRef1, int userId);
	
	List<EventInstance> getPendingEvents(int userId);
	
	void attachRelationalInfo(List<EventInstance> list);
	
	void attachRelationalInfo(EventInstance event);
	
	EventInstance insertEventComment(int eventId, UserComment comment);
	
	int purgeEventsBefore(final long time);

	int getEventCount();
	
	List<EventInstance> searchOld(int eventId, int eventSourceType,	String status, int alarmLevel, final String[] keywords,	final int maxResults, int userId, final ResourceBundle bundle);
	
	List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel, final String[] keywords, int userId, final ResourceBundle bundle, final int from, final int to,	final Date date);
	
	List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel, final String[] keywords, long dateFrom, long dateTo, int userId, final ResourceBundle bundle, final int from, final int to,	final Date date);
	
	int getSearchRowCount();
	
	int getStartRow();
	
	EventType getEventHandlerType(int handlerId);
	
	List<EventHandlerVO> getEventHandlers(EventType type);
	
	List<EventHandlerVO> getEventHandlers(EventTypeVO type);
	
	List<EventHandlerVO> getEventHandlers();
	
	EventHandlerVO getEventHandler(int eventHandlerId);
	
	EventHandlerVO getEventHandler(String xid);
	
	EventHandlerVO saveEventHandler(final EventType type, final EventHandlerVO handler);
	
	EventHandlerVO saveEventHandler(final EventTypeVO type,	final EventHandlerVO handler);
	
	void insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);
	
	void updateEventHandler(EventHandlerVO handler);
	
	void deleteEventHandler(final int handlerId);

	@Deprecated(since = "2.8.0")
	boolean toggleSilence(int eventId, int userId);

	int getHighestUnsilencedAlarmLevel(int userId);

	EventInstance getEvent(int eventId);

	String generateUniqueXid();

	boolean isXidUnique(String xid, int excludeId);

	List<EventInstance> getPendingEventsAlarmLevelMin(int userId, int alarmLevelMin, int limit);

	boolean toggleSilence(EventInstance event, User user);

	boolean assignEvent(EventInstance event, User user);
	boolean unassignEvent(EventInstance event, User user);

	void ackEvent(EventInstance event, long time, User user, int alternateAckSource, boolean signalAlarmLevelChange);
	void ackEvent(EventInstance event, long time, User user, int alternateAckSource);
	void unassignEvents();
	void ackEvents(User user);
	List<Integer> silenceEvents(User user);
}
