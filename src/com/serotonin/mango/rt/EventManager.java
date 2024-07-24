/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.EmailHandlerRT;
import com.serotonin.mango.rt.event.handlers.EventHandlerRT;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.util.NotifyEventUtils;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.ILifecycle;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.ws.model.WsEventMessage;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew Lohbihler
 */
public class EventManager implements ILifecycle {
	private final Log log = LogFactory.getLog(EventManager.class);

	private final List<EventInstance> activeEvents = new CopyOnWriteArrayList<EventInstance>();
	private MangoEvent eventService;
	private MangoUser userService;
	private long lastAlarmTimestamp = 0;
	private int highestActiveAlarmLevel = 0;
	private IHighestAlarmLevelService highestAlarmLevelService;
	private UserEventServiceWebSocket userEventServiceWebSocket;

	//
	//
	// Basic event management.
	//
	public void raiseEvent(EventType type, long time, boolean rtnApplicable,
						   int alarmLevel, LocalizableMessage message,
						   Map<String, Object> context) {
		raiseEvent(type, time, rtnApplicable, alarmLevel, message, message, context);
	}

	public void raiseEvent(EventType type, long time, boolean rtnApplicable,
						   int alarmLevel, LocalizableMessage message, LocalizableMessage shortMessage,
						   Map<String, Object> context) {
		// Check if there is an event for this type already active.
		EventInstance dup = get(type);
		if (dup != null) {
			// Check the duplicate handling.
			int dh = type.getDuplicateHandling();
			if (dh == EventType.DuplicateHandling.DO_NOT_ALLOW) {
				// Create a log error...
				log.error("An event was raised for a type that is already active: type="
						+ type + ", message=" + message.getKey());
				// ... but ultimately just ignore the thing.
				return;
			}

			if (dh == EventType.DuplicateHandling.IGNORE)
				// Safely return.
				return;

			if (dh == EventType.DuplicateHandling.IGNORE_SAME_MESSAGE) {
				// Ignore only if the message is the same. There may be events
				// of this type with different messages,
				// so look through them all for a match.
				for (EventInstance e : getAll(type)) {
					if (e.getMessage().equals(message))
						return;
				}
			}

			// Otherwise we just continue...
		}

		// Determine if the event should be suppressed.
		boolean suppressed = isSuppressed(type);

		EventInstance evt = new EventInstance(type, time, rtnApplicable,
				alarmLevel, message, shortMessage, context);

		if (!suppressed)
			setHandlers(evt);

		// Get id from database by inserting event immediately.
		eventService.saveEvent(evt);

		// Create user alarm records for all applicable users
		List<Integer> eventUserIds = new ArrayList<>();
		Set<String> emailUsers = new HashSet<>();
		List<User> eventConfirmForUsers = new ArrayList<>();

		for (User user : userService.getActiveUsers()) {
			// Do not create an event for this user if the event type says the
			// user should be skipped.
			if (type.excludeUser(user))
				continue;

			if (Permissions.hasEventTypePermission(user, type)) {
				eventUserIds.add(user.getId());
				if( !suppressed && evt.isAlarm() ) {
					notifyEventRaise(evt, user);
				}
				if (evt.isAlarm() && user.getReceiveAlarmEmails() > 0
						&& alarmLevel >= user.getReceiveAlarmEmails())
					emailUsers.add(user.getEmail());

				eventConfirmForUsers.add(user);
				if(evt.getAlarmLevel() > AlarmLevels.NONE)
					notifyEventUpdate(user, WsEventMessage.create(evt));
			}
		}

		if (eventUserIds.size() > 0) {
			if(evt.getAlarmLevel() != AlarmLevels.NONE)
				eventService.insertUserEvents(evt.getId(), eventUserIds, evt.isAlarm());
			if (!suppressed && evt.isAlarm())
				setLastAlarmTimestamp(System.currentTimeMillis());
		}

		if (evt.isRtnApplicable())
			activeEvents.add(evt);

		if (suppressed) {
			if(evt.getAlarmLevel() != AlarmLevels.NONE) {
				User admin = userService.getUser("admin");
				if(admin != null) {
					eventService.ackEvent(
							evt,
							time,
							admin,
							EventInstance.AlternateAcknowledgementSources.MAINTENANCE_MODE,
							false); // no signaling of AlarmLevel change
					for(User user: eventConfirmForUsers) {
						notifyEventAck(evt, user);
					}
				} else {
					log.warn("The username admin does not exist! " + LoggingUtils.eventInfo(evt) + " is not acknowledged!");
				}
			}
		} else {
			if (evt.isRtnApplicable()) {
				if (alarmLevel > highestActiveAlarmLevel) {
					int oldValue = highestActiveAlarmLevel;
					highestActiveAlarmLevel = alarmLevel;
					SystemEventType
							.raiseEvent(
									new SystemEventType(
											SystemEventType.TYPE_MAX_ALARM_LEVEL_CHANGED),
									time,
									false,
									getAlarmLevelChangeMessage(
											"event.alarmMaxIncreased", oldValue, alarmLevel));
				}
			}

			// Call raiseEvent handlers.
			handleRaiseEvent(evt, emailUsers);

			if (log.isDebugEnabled())
				log.debug("Event raised: type=" + type + ", message="
						+ message.getLocalizedMessage(Common.getBundle()));
		}
	}

	public void returnToNormal(EventType type, long time) {
		returnToNormal(type, time, EventInstance.RtnCauses.RETURN_TO_NORMAL);
	}

	public void returnToNormal(EventType type, long time, int cause) {
		EventInstance evt = remove(type);

		// Loop in case of multiples
		while (evt != null) {
			resetHighestAlarmLevel(time, false);

			evt.returnToNormal(time, cause);
			eventService.saveEvent(evt);
			notifyEventRtn(evt);
			// Call inactiveEvent handlers.
			handleInactiveEvent(evt);

			// Check for another
			evt = remove(type);
		}

		if (log.isDebugEnabled())
			log.debug("Event returned to normal: type=" + type);
	}

	private void deactivateEvent(EventInstance evt, long time, int inactiveCause) {
		activeEvents.remove(evt);
		resetHighestAlarmLevel(time, false);
		evt.returnToNormal(time, inactiveCause);
		eventService.saveEvent(evt);
		notifyEventRtn(evt);
		// Call inactiveEvent handlers.
		handleInactiveEvent(evt);
	}
	
	public void setLastAlarmTimestamp(long alarmTimestamp) {
		this.lastAlarmTimestamp = alarmTimestamp;
		notifyAlarmTimestampChange(alarmTimestamp);
	}

	//
	//
	// Canceling events.
	//
	public void cancelEventsForDataPoint(int dataPointId) {
		for (EventInstance e : activeEvents) {
			if (e.getEventType().getDataPointId() == dataPointId)
				deactivateEvent(e, System.currentTimeMillis(),
						EventInstance.RtnCauses.SOURCE_DISABLED);
		}
	}

	public void cancelEventsForDataSource(int dataSourceId) {
		for (EventInstance e : activeEvents) {
			if (e.getEventType().getDataSourceId() == dataSourceId)
				deactivateEvent(e, System.currentTimeMillis(),
						EventInstance.RtnCauses.SOURCE_DISABLED);
		}
	}

	public void cancelEventsForPublisher(int publisherId) {
		for (EventInstance e : activeEvents) {
			if (e.getEventType().getPublisherId() == publisherId)
				deactivateEvent(e, System.currentTimeMillis(),
						EventInstance.RtnCauses.SOURCE_DISABLED);
		}
	}

	public void cancelEventsForHandler(int handlerId) {
		for (EventInstance e : activeEvents) {
			if (e.getEventType().getEventHandlerId() == handlerId)
				deactivateEvent(e, System.currentTimeMillis(),
						EventInstance.RtnCauses.SOURCE_DISABLED);
		}
	}

	private void resetHighestAlarmLevel(long time, boolean init) {
		int max = 0;
		for (EventInstance e : activeEvents) {
			if (e.getAlarmLevel() > max)
				max = e.getAlarmLevel();
		}

		if (!init) {
			if (max > highestActiveAlarmLevel) {
				int oldValue = highestActiveAlarmLevel;
				highestActiveAlarmLevel = max;
				SystemEventType.raiseEvent(
						new SystemEventType(
								SystemEventType.TYPE_MAX_ALARM_LEVEL_CHANGED),
						time,
						false,
						getAlarmLevelChangeMessage("event.alarmMaxIncreased",
								oldValue, max));
			} else if (max < highestActiveAlarmLevel) {
				int oldValue = highestActiveAlarmLevel;
				highestActiveAlarmLevel = max;
				SystemEventType.raiseEvent(
						new SystemEventType(
								SystemEventType.TYPE_MAX_ALARM_LEVEL_CHANGED),
						time,
						false,
						getAlarmLevelChangeMessage("event.alarmMaxDecreased",
								oldValue, max));
			}
		}
	}

	private LocalizableMessage getAlarmLevelChangeMessage(String key,
			int oldValue, int newValue) {
		return new LocalizableMessage(key,
				AlarmLevels.getAlarmLevelMessage(oldValue),
				AlarmLevels.getAlarmLevelMessage(newValue));
	}

	//
	//
	// Lifecycle interface
	//
	public void initialize() {
		eventService = new EventService();
		userService = new UserService();
		highestAlarmLevelService = ApplicationBeans.getHighestAlarmLevelServiceBean();
		userEventServiceWebSocket = ApplicationBeans.getUserEventServiceWebsocketBean();

		// Get all active events from the database.
		activeEvents.addAll(eventService.getActiveEvents());
		setLastAlarmTimestamp(System.currentTimeMillis());
		resetHighestAlarmLevel(lastAlarmTimestamp, true);
	}

	public MangoUser getUserService() {
		return userService;
	}

	public void terminate() {
		// no op
	}

	public void joinTermination() {
		// no op
	}

	//
	//
	// Convenience
	//
	/**
	 * Returns the first event instance with the given type, or null is there is
	 * none.
	 */
	private EventInstance get(EventType type) {
		for (EventInstance e : activeEvents) {
			if (e.getEventType().equals(type))
				return e;
		}
		return null;
	}

	private List<EventInstance> getAll(EventType type) {
		List<EventInstance> result = new ArrayList<EventInstance>();
		for (EventInstance e : activeEvents) {
			if (e.getEventType().equals(type))
				result.add(e);
		}
		return result;
	}

	/**
	 * Finds and removes the first event instance with the given type. Returns
	 * null if there is none.
	 * 
	 * @param type
	 * @return
	 */
	private EventInstance remove(EventType type) {
		for (EventInstance e : activeEvents) {
			if (e.getEventType().equals(type)) {
				activeEvents.remove(e);
				return e;
			}
		}
		return null;
	}

	private void setHandlers(EventInstance evt) {
		List<EventHandlerVO> vos = eventService
				.getEventHandlers(evt.getEventType());
		List<EventHandlerRT> rts = null;
		for (EventHandlerVO vo : vos) {
			if (!vo.isDisabled()) {
				if (rts == null)
					rts = new ArrayList<EventHandlerRT>();
				rts.add(vo.createRuntime());
			}
		}
		if (rts != null)
			evt.setHandlers(rts);
	}

	private void handleRaiseEvent(EventInstance evt,
			Set<String> defaultAddresses) {
		if (evt.getHandlers() != null) {
			for (EventHandlerRT h : evt.getHandlers()) {
				h.eventRaised(evt);

				// If this is an email handler, remove any addresses to which it
				// was sent from the default addresses
				// so that the default users do not receive multiple
				// notifications.
				if (h instanceof EmailHandlerRT) {
					for (String addr : ((EmailHandlerRT) h)
							.getActiveRecipients())
						defaultAddresses.remove(addr);
				}
			}
		}

		if (!defaultAddresses.isEmpty()) {
			// If there are still any addresses left in the list, send them the
			// notification.
			EmailHandlerRT.sendActiveEmail(evt, defaultAddresses);
		}
	}

	private void handleInactiveEvent(EventInstance evt) {
		if (evt.getHandlers() != null) {
			for (EventHandlerRT h : evt.getHandlers())
				h.eventInactive(evt);
		}
	}

	private boolean isSuppressed(EventType eventType) {
		if (eventType instanceof DataSourceEventType)
			// Data source events can be suppressed by maintenance events.
			return Common.ctx.getRuntimeManager().isActiveMaintenanceEvent(
					eventType.getDataSourceId());

		if (eventType instanceof DataPointEventType)
			// Data point events can be suppressed by maintenance events on
			// their data sources.
			return Common.ctx.getRuntimeManager().isActiveMaintenanceEvent(
					eventType.getDataSourceId());

		return false;
	}
	
	
	///////////////////////////////////////////////
	// UserHighestAlarmLevelListeners registration & notifications
	//
	public void notifyAlarmTimestampChange(long alarmTimestamp) {

	}

	public void resetHighestAlarmLevels() {
		NotifyEventUtils.resetHighestAlarmLevels(highestAlarmLevelService, userEventServiceWebSocket);
	}

	public int getHighestAlarmLevel(int userId) {
		return highestAlarmLevelService.getAlarmLevel(User.onlyId(userId));
	}

	@Deprecated(since = "2.8.0")
	public void notifyEventRaise(int eventId, int userId) {
		if(eventId != Common.NEW_ID) {
			EventInstance evt = eventService.getEvent(eventId);
			User user = userService.getUser(userId);
			notifyEventRaise(evt, user);
		}
	}

	@Deprecated(since = "2.8.0")
	public void notifyEventRaise(int eventId) {
		if(eventId != Common.NEW_ID) {
			for(int userId: ApplicationBeans.getLoggedUsersBean().getUserIds()) {
				notifyEventRaise(eventId, userId);
			}
		}
	}

	public void notifyEventRaise(EventInstance evt, User user) {
		NotifyEventUtils.notifyEventRaise(highestAlarmLevelService, evt, user, userEventServiceWebSocket);
	}

	public void notifyEventAck(EventInstance evt, User user) {
		NotifyEventUtils.notifyEventAck(highestAlarmLevelService, evt, user, userEventServiceWebSocket);
	}

	@Deprecated(since = "2.8.0")
	public void notifyEventAck(int eventId, User user) {
		if(eventId != Common.NEW_ID) {
			EventInstance evt = eventService.getEvent(eventId);
			notifyEventAck(evt, user);
		}
	}

	@Deprecated(since = "2.8.0")
	public void notifyEventAck(int eventId) {
		if(eventId != Common.NEW_ID) {
			for (int userId : ApplicationBeans.getLoggedUsersBean().getUserIds())
				notifyEventAck(eventId, userService.getUser(userId));
		}
	}

	@Deprecated(since = "2.8.0")
	public void notifyEventAssignee(int eventId) {
		if(eventId != Common.NEW_ID) {
			for (int userId : ApplicationBeans.getLoggedUsersBean().getUserIds())
				notifyEventAck(eventId, userService.getUser(userId));
		}
	}

	public void notifyEventRtn(EventInstance evt, User user) {
		NotifyEventUtils.notifyEventRtn(highestAlarmLevelService, evt, user, userEventServiceWebSocket);
	}

	public void notifyEventRtn(EventInstance event) {
		if(event.getId() != Common.NEW_ID) {
			for (User user : ApplicationBeans.getLoggedUsersBean().getUsers()) {
				notifyEventRtn(event, user);
			}
		}
	}

	public void notifyEventToggle(EventInstance evt, User user) {
		NotifyEventUtils.notifyEventToggle(highestAlarmLevelService, evt, user, userEventServiceWebSocket);
	}

	@Deprecated(since = "2.8.0")
	public void notifyEventToggle(int eventId, int userId) {
		if(eventId != Common.NEW_ID) {
			EventInstance evt = eventService.getEvent(eventId);
			User user = userService.getUser(userId);
			notifyEventToggle(evt, user);
		}
	}

	public void notifyEventUpdate(User user, WsEventMessage message) {
		NotifyEventUtils.notifyEventUpdate(user, message, userEventServiceWebSocket);
	}

	public void notifyEventRaise(EventInstance event) {
		if(event.getId() != Common.NEW_ID) {
			for (User user : ApplicationBeans.getLoggedUsersBean().getUsers()) {
				notifyEventRaise(event, user);
			}
		}
	}

	public void notifyEventAck(EventInstance event) {
		if(event.getId() != Common.NEW_ID) {
			for (User user : ApplicationBeans.getLoggedUsersBean().getUsers()) {
				notifyEventAck(event, user);
			}
		}
	}

	public void notifyEventAssignee(EventInstance event) {
		if(event.getId() != Common.NEW_ID) {
			for (User user : ApplicationBeans.getLoggedUsersBean().getUsers()) {
				notifyEventToggle(event, user);
			}
		}
	}
}
