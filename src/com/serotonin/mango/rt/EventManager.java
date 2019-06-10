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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.service.UserHighestAlarmLevelListener;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.EmailHandlerRT;
import com.serotonin.mango.rt.event.handlers.EventHandlerRT;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.ILifecycle;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class EventManager implements ILifecycle {
	private final Log log = LogFactory.getLog(EventManager.class);

	private final List<UserHighestAlarmLevelListener> userHighestAlarmLevelListeners = new CopyOnWriteArrayList<UserHighestAlarmLevelListener>();
	private final List<EventInstance> activeEvents = new CopyOnWriteArrayList<EventInstance>();
	private EventDao eventDao;
	private UserDao userDao;
	private long lastAlarmTimestamp = 0;
	private int highestActiveAlarmLevel = 0;

	//
	//
	// Basic event management.
	//
	public void raiseEvent(EventType type, long time, boolean rtnApplicable,
			int alarmLevel, LocalizableMessage message,
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
				alarmLevel, message, context);

		if (!suppressed)
			setHandlers(evt);

		// Get id from database by inserting event immediately.
		eventDao.saveEvent(evt);

		// Create user alarm records for all applicable users
		List<Integer> eventUserIds = new ArrayList<Integer>();
		Set<String> emailUsers = new HashSet<String>();

		for (User user : userDao.getActiveUsers()) {
			// Do not create an event for this user if the event type says the
			// user should be skipped.
			if (type.excludeUser(user))
				continue;

			if (Permissions.hasEventTypePermission(user, type)) {
				eventUserIds.add(user.getId());
				if( !suppressed && evt.isAlarm() ) 
					notifyEventRaise(evt.getId(), user.getId(), evt.getAlarmLevel());
				if (evt.isAlarm() && user.getReceiveAlarmEmails() > 0
						&& alarmLevel >= user.getReceiveAlarmEmails())
					emailUsers.add(user.getEmail());
			}
		}

		if (eventUserIds.size() > 0) {
			eventDao.insertUserEvents(evt.getId(), eventUserIds, evt.isAlarm());
			if (!suppressed && evt.isAlarm())
				setLastAlarmTimestamp(System.currentTimeMillis());
		}

		if (evt.isRtnApplicable())
			activeEvents.add(evt);

		if (suppressed)
			eventDao.ackEvent(
					evt.getId(),
					time,
					0,
					EventInstance.AlternateAcknowledgementSources.MAINTENANCE_MODE,
					false); // no signaling of AlarmLevel change
		else {
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
											"event.alarmMaxIncreased", oldValue));
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
			eventDao.saveEvent(evt);

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
		eventDao.saveEvent(evt);

		// Call inactiveEvent handlers.
		handleInactiveEvent(evt);
	}

	public long getLastAlarmTimestamp() {
		return lastAlarmTimestamp;
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
								oldValue));
			} else if (max < highestActiveAlarmLevel) {
				int oldValue = highestActiveAlarmLevel;
				highestActiveAlarmLevel = max;
				SystemEventType.raiseEvent(
						new SystemEventType(
								SystemEventType.TYPE_MAX_ALARM_LEVEL_CHANGED),
						time,
						false,
						getAlarmLevelChangeMessage("event.alarmMaxDecreased",
								oldValue));
			}
		}
	}

	private LocalizableMessage getAlarmLevelChangeMessage(String key,
			int oldValue) {
		return new LocalizableMessage(key,
				AlarmLevels.getAlarmLevelMessage(oldValue),
				AlarmLevels.getAlarmLevelMessage(highestActiveAlarmLevel));
	}

	//
	//
	// Lifecycle interface
	//
	public void initialize() {
		eventDao = new EventDao();
		userDao = new UserDao();

		// Get all active events from the database.
		activeEvents.addAll(eventDao.getActiveEvents());
		setLastAlarmTimestamp(System.currentTimeMillis());
		resetHighestAlarmLevel(lastAlarmTimestamp, true);
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
		List<EventHandlerVO> vos = eventDao
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
	public void addUserHighestAlarmLevelListener(UserHighestAlarmLevelListener listener) {
		userHighestAlarmLevelListeners.add(listener);
	}

	public void removeUserHighestAlarmLevelListener(UserHighestAlarmLevelListener listener) {
		userHighestAlarmLevelListeners.remove(listener);
	}
	
	public void notifyAlarmTimestampChange(long alarmTimestamp) {
		for( UserHighestAlarmLevelListener listener: userHighestAlarmLevelListeners) {
			listener.onAlarmTimestampChange(alarmTimestamp);
		}
	}

	public void notifyEventRaise(int eventId, int userId, int alarmLevel) {
		for( UserHighestAlarmLevelListener listener: userHighestAlarmLevelListeners) {
			listener.onEventRaise(eventId, userId, alarmLevel);
		}
	}
	
	public void notifyEventAck(int eventId, int userId) {
		for( UserHighestAlarmLevelListener listener: userHighestAlarmLevelListeners) {
			listener.onEventAck(eventId, userId);
		}
	}

	public void notifyEventToggle(int eventId, int userId, boolean isSilenced) {
		for( UserHighestAlarmLevelListener listener: userHighestAlarmLevelListeners) {
			listener.onEventToggle(eventId, userId, isSilenced);
		}
	}

}
