package org.scada_lts.web.ws.model;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.EventHandlerRT;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.List;

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

    public LocalizableMessage getRtnMessage() {
        return instance.getRtnMessage();
    }

    public LocalizableMessage getAckMessage() {
        return instance.getAckMessage();
    }

    public LocalizableMessage getExportAckMessage() {
        return instance.getExportAckMessage();
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

    public LocalizableMessage getMessage() {
        return instance.getMessage();
    }

    public LocalizableMessage getShortMessage() {
        return instance.getShortMessage();
    }

    public boolean isRtnApplicable() {
        return instance.isRtnApplicable();
    }

    public List<UserComment> getEventComments() {
        return instance.getEventComments();
    }

    public int getRtnCause() {
        return instance.getRtnCause();
    }

    public List<EventHandlerRT> getHandlers() {
        return instance.getHandlers();
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
}
