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
package com.serotonin.mango.rt.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.handlers.EventHandlerRT;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.DateFunctions;
import org.scada_lts.serorepl.utils.StringUtils;

public class EventInstance {
    public interface RtnCauses {
        int RETURN_TO_NORMAL = 1;
        int SOURCE_DISABLED = 4;
    }

    public interface AlternateAcknowledgementSources {
        int DELETED_USER = 1;
        int MAINTENANCE_MODE = 2;
    }

    /**
     * Configuration field. Assigned by the database.
     */
    private int id = Common.NEW_ID;

    /**
     * Configuration field. Provided by the event producer. Identifies where the event came from and what it means.
     */
    private final EventType eventType;

    /**
     * State field. The time that the event became active (i.e. was raised).
     */
    private final long activeTimestamp;

    /**
     * Configuration field. Is this type of event capable of returning to normal (true), or is it stateless (false).
     */
    private final boolean rtnApplicable;

    /**
     * State field. The time that the event returned to normal.
     */
    private long rtnTimestamp;

    /**
     * State field. The action that caused the event to RTN. One of {@link RtnCauses}
     */
    private int rtnCause;

    /**
     * Configuration field. The alarm level assigned to the event.
     * 
     * @see AlarmLevels
     */
    private final int alarmLevel;

    /**
     * Configuration field. The messages associated with the event.
     */
    private final LocalizableMessage message;
    private final LocalizableMessage shortMessage;

    /**
     * User comments on the event. Added in the events interface after the event has been raised.
     */
    private List<UserComment> eventComments;

    private List<EventHandlerRT> handlers;

    private long acknowledgedTimestamp;
    private int acknowledgedByUserId;
    private String acknowledgedByUsername;
    private int alternateAckSource;

    private long assigneeTimestamp;
    private String assigneeUsername;

    //
    //
    // These fields are used only in the context of access by a particular user, providing state filled in from
    // the userEvents table.
    private boolean userNotified;
    private boolean silenced;

    //
    // Contextual data from the source that raised the event.
    private final Map<String, Object> context;

    public EventInstance(EventType eventType, long activeTimestamp, boolean rtnApplicable, int alarmLevel,
                         LocalizableMessage message, Map<String, Object> context) {
        this.eventType = eventType;
        this.activeTimestamp = activeTimestamp;
        this.rtnApplicable = rtnApplicable;
        this.alarmLevel = alarmLevel;
        if (message == null) {
            this.message = new LocalizableMessage("common.noMessage");
            this.shortMessage = new LocalizableMessage("common.noMessage");
        } else {
            this.message = message;
            this.shortMessage = message;
        }
        this.context = context;
    }

    public EventInstance(EventType eventType, long activeTimestamp, boolean rtnApplicable, int alarmLevel,
                         LocalizableMessage message, LocalizableMessage shortMessage, Map<String, Object> context) {
        this.eventType = eventType;
        this.activeTimestamp = activeTimestamp;
        this.rtnApplicable = rtnApplicable;
        this.alarmLevel = alarmLevel;
        if (message == null)
            this.message = new LocalizableMessage("common.noMessage");
        else
            this.message = message;
        if (shortMessage == null)
            this.shortMessage = new LocalizableMessage("common.noMessage");
        else
            this.shortMessage = shortMessage;
        this.context = context;
    }

    public static EventInstance emptySystemNoneEvent(int eventId) {
        EventInstance eventInstance = new EventInstance(new SystemEventType(), 0, false, AlarmLevels.NONE, null, new HashMap<>());
        eventInstance.setId(eventId);
        return eventInstance;
    }

    public LocalizableMessage getRtnMessage() {
        LocalizableMessage rtnKey = null;

        if (!isActive()) {
            if (rtnCause == RtnCauses.RETURN_TO_NORMAL)
                rtnKey = new LocalizableMessage("event.rtn.rtn");
            else if (rtnCause == RtnCauses.SOURCE_DISABLED) {
                if (eventType.getEventSourceId() == EventType.EventSources.DATA_POINT)
                    rtnKey = new LocalizableMessage("event.rtn.pointDisabled");
                else if (eventType.getEventSourceId() == EventType.EventSources.DATA_SOURCE)
                    rtnKey = new LocalizableMessage("event.rtn.dsDisabled");
                else if (eventType.getEventSourceId() == EventType.EventSources.PUBLISHER)
                    rtnKey = new LocalizableMessage("event.rtn.pubDisabled");
                else if (eventType.getEventSourceId() == EventType.EventSources.MAINTENANCE)
                    rtnKey = new LocalizableMessage("event.rtn.maintDisabled");
                else
                    rtnKey = new LocalizableMessage("event.rtn.shutdown");
            }
            else
                rtnKey = new LocalizableMessage("event.rtn.unknown");
        }

        return rtnKey;
    }

    public LocalizableMessage getAckMessage() {
        if (isAcknowledged()) {
            if (acknowledgedByUserId != 0)
                return new LocalizableMessage("events.ackedByUser", acknowledgedByUsername);
            if (alternateAckSource == AlternateAcknowledgementSources.DELETED_USER)
                return new LocalizableMessage("events.ackedByDeletedUser");
            if (alternateAckSource == AlternateAcknowledgementSources.MAINTENANCE_MODE)
                return new LocalizableMessage("events.ackedByMaintenance");
        }

        return new LocalizableMessage("event.auto.acknowledge");
    }

    public LocalizableMessage getAssigneeMessage() {
        if (isAssignee()) {
            if (!StringUtils.isEmpty(assigneeUsername))
                return new LocalizableMessage("events.assigneeByUser", assigneeUsername);
        }
        return new LocalizableMessage("common.noMessage");
    }

    public LocalizableMessage getExportAckMessage() {
        if (isAcknowledged()) {
            if (acknowledgedByUserId != 0 || !StringUtils.isEmpty(acknowledgedByUsername))
                return new LocalizableMessage("events.export.ackedByUser", acknowledgedByUsername);
            if (alternateAckSource == AlternateAcknowledgementSources.DELETED_USER)
                return new LocalizableMessage("events.export.ackedByDeletedUser");
            if (alternateAckSource == AlternateAcknowledgementSources.MAINTENANCE_MODE)
                return new LocalizableMessage("events.export.ackedByMaintenance");
        }

        return new LocalizableMessage("event.auto.acknowledge");
    }

    public String getPrettyActiveTimestamp() {
        return DateFunctions.getTime(activeTimestamp);
    }

    public String getFullPrettyActiveTimestamp() {
        return DateFunctions.getFullSecondTime(activeTimestamp);
    }

    public String getPrettyRtnTimestamp() {
        return DateFunctions.getTime(rtnTimestamp);
    }

    public String getFullPrettyRtnTimestamp() {
        return DateFunctions.getFullSecondTime(rtnTimestamp);
    }

    public String getFullPrettyAcknowledgedTimestamp() {
        return DateFunctions.getFullSecondTime(acknowledgedTimestamp);
    }

    public boolean isAlarm() {
        return alarmLevel != AlarmLevels.NONE;
    }

    /**
     * This method should only be used by the EventDao for creating and updating.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return rtnApplicable && rtnTimestamp == 0;
    }

    public void returnToNormal(long time, int rtnCause) {
        if (isActive()) {
            rtnTimestamp = time;
            this.rtnCause = rtnCause;
        }
    }

    public boolean isAcknowledged() {
        return acknowledgedTimestamp > 0;
    }

    public boolean isAssignee() {
        return assigneeTimestamp > 0;
    }

    public long getActiveTimestamp() {
        return activeTimestamp;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getId() {
        return id;
    }

    public long getRtnTimestamp() {
        return rtnTimestamp;
    }

    public LocalizableMessage getMessage() {
        return message;
    }

    public LocalizableMessage getShortMessage() {
        return shortMessage;
    }

    public boolean isRtnApplicable() {
        return rtnApplicable;
    }

    public void addEventComment(UserComment comment) {
        eventComments.add(comment);
    }

    public void setEventComments(List<UserComment> eventComments) {
        this.eventComments = eventComments;
    }

    public List<UserComment> getEventComments() {
        return eventComments;
    }

    public int getRtnCause() {
        return rtnCause;
    }

    public List<EventHandlerRT> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<EventHandlerRT> handlers) {
        this.handlers = handlers;
    }

    public boolean isUserNotified() {
        return userNotified;
    }

    public void setUserNotified(boolean userNotified) {
        this.userNotified = userNotified;
    }

    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }

    public long getAcknowledgedTimestamp() {
        return acknowledgedTimestamp;
    }

    public void setAcknowledgedTimestamp(long acknowledgedTimestamp) {
        this.acknowledgedTimestamp = acknowledgedTimestamp;
    }

    public int getAcknowledgedByUserId() {
        return acknowledgedByUserId;
    }

    public void setAcknowledgedByUserId(int acknowledgedByUserId) {
        this.acknowledgedByUserId = acknowledgedByUserId;
    }

    public String getAcknowledgedByUsername() {
        return acknowledgedByUsername;
    }

    public void setAcknowledgedByUsername(String acknowledgedByUsername) {
        this.acknowledgedByUsername = acknowledgedByUsername;
    }

    public int getAlternateAckSource() {
        return alternateAckSource;
    }

    public void setAlternateAckSource(int alternateAckSource) {
        this.alternateAckSource = alternateAckSource;
    }

    public long getAssigneeTimestamp() {
        return assigneeTimestamp;
    }

    public void setAssigneeTimestamp(long assigneeTimestamp) {
        this.assigneeTimestamp = assigneeTimestamp;
    }

    public String getAssigneeUsername() {
        return assigneeUsername;
    }

    public void setAssigneeUsername(String assigneeUsername) {
        this.assigneeUsername = assigneeUsername;
    }

    public Map<String, Object> getContext() {
        return context;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + acknowledgedByUserId;
		result = prime * result + ((acknowledgedByUsername == null) ? 0 : acknowledgedByUsername.hashCode());
		result = prime * result + (int) (acknowledgedTimestamp ^ (acknowledgedTimestamp >>> 32));
		result = prime * result + (int) (activeTimestamp ^ (activeTimestamp >>> 32));
		result = prime * result + alarmLevel;
		result = prime * result + alternateAckSource;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((eventComments == null) ? 0 : eventComments.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((handlers == null) ? 0 : handlers.hashCode());
		result = prime * result + id;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((shortMessage == null) ? 0 : shortMessage.hashCode());
		result = prime * result + (rtnApplicable ? 1231 : 1237);
		result = prime * result + rtnCause;
		result = prime * result + (int) (rtnTimestamp ^ (rtnTimestamp >>> 32));
		result = prime * result + (silenced ? 1231 : 1237);
		result = prime * result + (userNotified ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventInstance other = (EventInstance) obj;
		if (acknowledgedByUserId != other.acknowledgedByUserId)
			return false;
		if (acknowledgedByUsername == null) {
			if (other.acknowledgedByUsername != null)
				return false;
		} else if (!acknowledgedByUsername.equals(other.acknowledgedByUsername))
			return false;
		if (acknowledgedTimestamp != other.acknowledgedTimestamp)
			return false;
		if (activeTimestamp != other.activeTimestamp)
			return false;
		if (alarmLevel != other.alarmLevel)
			return false;
		if (alternateAckSource != other.alternateAckSource)
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (eventComments == null) {
			if (other.eventComments != null)
				return false;
		} else if (!eventComments.equals(other.eventComments))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		if (handlers == null) {
			if (other.handlers != null)
				return false;
		} else if (!handlers.equals(other.handlers))
			return false;
		if (id != other.id)
			return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (shortMessage == null) {
            if (other.shortMessage != null)
                return false;
        } else if (!shortMessage.equals(other.shortMessage))
            return false;
		if (rtnApplicable != other.rtnApplicable)
			return false;
		if (rtnCause != other.rtnCause)
			return false;
		if (rtnTimestamp != other.rtnTimestamp)
			return false;
		if (silenced != other.silenced)
			return false;
		if (userNotified != other.userNotified)
			return false;
		return true;
	}

	public EventInstance copyWithContext(Map<String, Object> context) {
        EventInstance eventInstance = new EventInstance(eventType, activeTimestamp, rtnApplicable, alarmLevel, message, shortMessage, context);
        eventInstance.setId(id);
        eventInstance.setAcknowledgedByUserId(acknowledgedByUserId);
        eventInstance.setAcknowledgedByUsername(acknowledgedByUsername);
        eventInstance.setAcknowledgedTimestamp(acknowledgedTimestamp);
        eventInstance.setAlternateAckSource(alternateAckSource);
        eventInstance.setEventComments(eventComments);
        eventInstance.setHandlers(handlers);
        eventInstance.setSilenced(silenced);
        eventInstance.setUserNotified(userNotified);
        eventInstance.setAssigneeTimestamp(assigneeTimestamp);
        eventInstance.setAssigneeUsername(assigneeUsername);
        return eventInstance;
    }

}
