package org.scada_lts.web.ws.model;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.web.i18n.LocalizableMessage;

public class WsEventMessage {

    public enum EventAction {
        CREATE, UPDATE, DELETE, RESET
    }

    private final EventInstance instance;
    private final String action;

    private WsEventMessage(EventInstance instance, EventAction action) {
        this.instance = instance;
        this.action = action.name();
    }

    public static WsEventMessage reset() {
        return new WsEventMessage(EventInstance.emptySystemNoneEvent(-1), EventAction.RESET);
    }

    public static WsEventMessage update(EventInstance eventInstance) {
        return new WsEventMessage(eventInstance, EventAction.UPDATE);
    }

    public static WsEventMessage delete(EventInstance eventInstance) {
        return new WsEventMessage(eventInstance, EventAction.DELETE);
    }

    public static WsEventMessage create(EventInstance eventInstance) {
        return new WsEventMessage(eventInstance, EventAction.CREATE);
    }

    public String getRtnMessage() {
        return getString(instance.getRtnMessage());
    }

    public String getAckMessage() {
        return getString(instance.getAckMessage());
    }

    public LocalizableMessage getAssigneeMessage() {
        return instance.getAssigneeMessage();
    }

    public String getExportAckMessage() {
        return getString(instance.getExportAckMessage());
    }

    public String getPrettyActiveTimestamp() {
        return instance.getPrettyActiveTimestamp();
    }

    public String getFullPrettyActiveTimestamp() {
        return instance.getFullPrettyActiveTimestamp();
    }

    public String getPrettyRtnTimestamp() {
        return instance.getPrettyRtnTimestamp();
    }

    public String getFullPrettyRtnTimestamp() {
        return instance.getFullPrettyRtnTimestamp();
    }

    public String getFullPrettyAcknowledgedTimestamp() {
        return instance.getFullPrettyAcknowledgedTimestamp();
    }

    public boolean isAlarm() {
        return instance.isAlarm();
    }

    public boolean isActive() {
        return instance.isActive();
    }

    public boolean isAcknowledged() {
        return instance.isAcknowledged();
    }

    public long getActiveTimestamp() {
        return instance.getActiveTimestamp();
    }

    public long getActiveTs() {
        return instance.getActiveTimestamp();
    }

    public int getAlarmLevel() {
        return instance.getAlarmLevel();
    }

    public EventType getEventType() {
        return instance.getEventType();
    }

    public int getId() {
        return instance.getId();
    }

    public long getRtnTimestamp() {
        return instance.getRtnTimestamp();
    }

    public long getRtnTs() {
        return instance.getRtnTimestamp();
    }

    public String getMessage() {
        return getString(instance.getMessage());
    }

    public String getShortMessage() {
        return getString(instance.getShortMessage());
    }

    public boolean isRtnApplicable() {
        return instance.isRtnApplicable();
    }

    public int getRtnCause() {
        return instance.getRtnCause();
    }

    public boolean isUserNotified() {
        return instance.isUserNotified();
    }

    public boolean isSilenced() {
        return instance.isSilenced();
    }

    public long getAcknowledgedTimestamp() {
        return instance.getAcknowledgedTimestamp();
    }

    public long getAckTs() {
        return instance.getAcknowledgedTimestamp();
    }

    public int getAcknowledgedByUserId() {
        return instance.getAcknowledgedByUserId();
    }

    public String getAcknowledgedByUsername() {
        return instance.getAcknowledgedByUsername();
    }

    public int getAlternateAckSource() {
        return instance.getAlternateAckSource();
    }

    public boolean isDeleted() {
        return getActiveTimestamp() == 0;
    }

    public String getAction() {
        return action;
    }

    public long getAssigneeTimestamp() {
        return instance.getAssigneeTimestamp();
    }

    public String getAssigneeUsername() {
        return instance.getAssigneeUsername();
    }

    public boolean isAssignee() {
        return instance.isAssignee();
    }
      
    public int getTypeId() {
        return instance.getEventType().getEventSourceId();
    }

    private static String getString(LocalizableMessage message) {
        if (message == null)
            return "";
        return message.getLocalizedMessage(Common.getBundle());
    }
}
