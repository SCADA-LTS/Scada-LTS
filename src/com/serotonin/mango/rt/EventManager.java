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
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.ActiveEvents;
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
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;

import java.util.*;
import java.util.function.Predicate;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;

/**
 * @author Matthew Lohbihler
 */
public class EventManager implements ILifecycle {
	private static final Log LOG = LogFactory.getLog(EventManager.class);

	private final ActiveEvents activeEvents;
	private final MangoEvent eventService;
	private final MangoUser userService;
	private long lastAlarmTimestamp = 0;
	private int highestActiveAlarmLevel = 0;
	private final IHighestAlarmLevelService highestAlarmLevelService;
	private final UserEventServiceWebSocket userEventServiceWebSocket;

	public EventManager() {
		eventService = new EventService();
		userService = new UserService();
		highestAlarmLevelService = ApplicationBeans.getHighestAlarmLevelServiceBean();
		userEventServiceWebSocket = ApplicationBeans.getUserEventServiceWebsocketBean();
		activeEvents = ActiveEvents.newSync(eventService);
	}

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
						   int alarmLevel, LocalizableMessage message,
						   Map<String, Object> context, DataSourceRT dataSourceRT) {
		raiseEvent(type, time, rtnApplicable, alarmLevel, message, message, context, dataSourceRT);
	}

	public void raiseEvent(EventType type, long time, boolean rtnApplicable,
						   int alarmLevel, LocalizableMessage message, LocalizableMessage shortMessage,
						   Map<String, Object> context) {
		raiseEvent(type, time, rtnApplicable, alarmLevel, message, shortMessage,
				context, null);
	}

	public void raiseEvent(EventType type, long time, boolean rtnApplicable,
						   int alarmLevel, LocalizableMessage message, LocalizableMessage shortMessage,
						   Map<String, Object> context, DataSourceRT dataSourceRT) {
		// Check if there is an event for this type already active.
		EventInstance evt = new EventInstance(type, time, rtnApplicable,
				alarmLevel, message, shortMessage, context);

		// Determine if the event should be suppressed.
		boolean suppressed = isSuppressed(type);

		if(activeEvents.isIgnoreIfNotThenAddActiveEvent(evt, suppressed)) {
			return;
		}

		if(evt.isRtnApplicable() && dataSourceRT != null) {
			setUnreliableDataPoints(type, dataSourceRT, activeEvents);
		}

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
					notifyEventCreate(user, evt);
			}
		}

		if (!eventUserIds.isEmpty()) {
			if(evt.isAlarm())
				eventService.insertUserEvents(evt.getId(), eventUserIds, evt.isAlarm());
			if (!suppressed && evt.isAlarm())
				setLastAlarmTimestamp(System.currentTimeMillis());
		}

		if (suppressed) {
			if(evt.isAlarm()) {
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
					LOG.warn("The username admin does not exist! " + LoggingUtils.eventInfo(evt) + " is not acknowledged!");
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

			if (LOG.isDebugEnabled())
				LOG.debug("Event raised: type=" + type + ", message="
						+ message.getLocalizedMessage(Common.getBundle()));
		}
	}

	public void returnToNormal(EventType type, long time, DataSourceRT dataSourceRT) {
		returnToNormal(type, time, EventInstance.RtnCauses.RETURN_TO_NORMAL, null, dataSourceRT);
	}

	public void returnToNormal(EventType type, long time) {
		returnToNormal(type, time, EventInstance.RtnCauses.RETURN_TO_NORMAL, null, null);
	}

	public void returnToNormal(EventType type, long time, int cause, LocalizableMessage onlyWithThisMessage, DataSourceRT dataSourceRT) {
		List<EventInstance> removedEvents = activeEvents.removeActiveEvents(type, onlyWithThisMessage);

		if(removedEvents != null) {
			for (EventInstance evt : removedEvents) {
				doDeactivateEvent(time, cause, evt);
			}
			removedEvents.clear();

			if(dataSourceRT != null)
				resetUnreliableDataPoints(type, dataSourceRT, activeEvents);
		}

		if (LOG.isDebugEnabled())
			LOG.debug("Event returned to normal: type=" + type);
	}



	private boolean deactivateEvent(EventInstance evt, long time, int inactiveCause) {
		EventInstance copy = evt.copy();
		try {
			resetHighestAlarmLevel(time, false);
			copy.returnToNormal(time, inactiveCause);
			eventService.saveEvent(copy);
			notifyEventRtn(copy);
			// Call inactiveEvent handlers.
			handleInactiveEvent(copy);
			return true;
		} catch (Throwable throwable) {
			LOG.error(LoggingUtils.exceptionInfo(throwable));
			return false;
		}
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
		cancelEventsFor(type -> type.getDataPointId() == dataPointId);
	}

	public void cancelEventsForDataSource(int dataSourceId) {
		cancelEventsFor(type -> type.getDataSourceId() == dataSourceId);
	}

	public void cancelEventsForPublisher(int publisherId) {
		cancelEventsFor(type -> type.getPublisherId() == publisherId);
	}

	public void cancelEventsForHandler(int handlerId) {
		cancelEventsFor(type -> type.getEventHandlerId() == handlerId);
	}

	private void resetHighestAlarmLevel(long time, boolean init) {
		int max = activeEvents.calculateGlobalHighestAlarmLevel();

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

		// Get all active events from the database.
		activeEvents.initActiveEvents(eventService.getActiveEvents());
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

	public void notifyEventReset() {
		NotifyEventUtils.notifyEventReset(highestAlarmLevelService, userEventServiceWebSocket, userService);
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

	public void notifyEventCreate(User user, EventInstance event) {
		NotifyEventUtils.notifyEventCreate(event, user, userEventServiceWebSocket);
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

	public void returnToNormal(EventType type, long time, int cause, DataSourceRT dataSourceRT) {
		returnToNormal(type, time, cause, null, dataSourceRT);
	}

	public void returnToNormal(EventType type, long time, int cause) {
		returnToNormal(type, time, cause, null, null);
	}

	public void returnToNormal(EventType type, long time, LocalizableMessage onlyWithThisMessage, DataSourceRT dataSourceRT) {
		returnToNormal(type, time, EventInstance.RtnCauses.RETURN_TO_NORMAL, onlyWithThisMessage, dataSourceRT);
	}

	private void cancelEventsFor(Predicate<EventType> cancelIf) {
		List<EventInstance> removedEvents = activeEvents.removeActiveEvents(cancelIf);
		if(removedEvents != null) {
			for (EventInstance event : removedEvents) {
				doDeactivateEvent(System.currentTimeMillis(), EventInstance.RtnCauses.SOURCE_DISABLED, event);
			}
			removedEvents.clear();
		}
	}

	private void doDeactivateEvent(long time, int cause, EventInstance evt) {
		boolean deactivated = deactivateEvent(evt, time, cause);
		if(!deactivated) {
			activeEvents.addActiveEvent(evt);
		} else {
			evt.returnToNormal(time, cause);
		}
	}
}
