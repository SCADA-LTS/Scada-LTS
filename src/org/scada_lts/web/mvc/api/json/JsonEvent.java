package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.List;
import java.util.Map;

public class JsonEvent {

    public interface RtnCauses {
        int RETURN_TO_NORMAL = 1;
        int SOURCE_DISABLED = 4;
    }

    public interface AlternateAcknowledgementSources {
        int DELETED_USER = 1;
        int MAINTENANCE_MODE = 2;
    }

    private int id = Common.NEW_ID;
    private final EventType eventType;
    private final long activeTimestamp;
    private final boolean rtnApplicable;
    private long rtnTimestamp;
    private int rtnCause;
    private final int alarmLevel;
    private final LocalizableMessage message;
    private final LocalizableMessage shortMessage;
    private List<UserComment> eventComments;
    private long acknowledgedTimestamp;
    private int acknowledgedByUserId;
    private String acknowledgedByUsername;
    private int alternateAckSource;
    private boolean userNotified;
    private boolean silenced;


    public JsonEvent(int id, EventType eventType, long activeTimestamp, boolean rtnApplicable, long rtnTimestamp, int rtnCause, int alarmLevel, LocalizableMessage message, LocalizableMessage shortMessage, List<UserComment> eventComments, long acknowledgedTimestamp, int acknowledgedByUserId, String acknowledgedByUsername, int alternateAckSource, boolean userNotified, boolean silenced) {
        this.id = id;
        this.eventType = eventType;
        this.activeTimestamp = activeTimestamp;
        this.rtnApplicable = rtnApplicable;
        this.rtnTimestamp = rtnTimestamp;
        this.rtnCause = rtnCause;
        this.alarmLevel = alarmLevel;
        this.message = message;
        this.shortMessage = shortMessage;
        this.eventComments = eventComments;
        this.acknowledgedTimestamp = acknowledgedTimestamp;
        this.acknowledgedByUserId = acknowledgedByUserId;
        this.acknowledgedByUsername = acknowledgedByUsername;
        this.alternateAckSource = alternateAckSource;
        this.userNotified = userNotified;
        this.silenced = silenced;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getActiveTimestamp() {
        return activeTimestamp;
    }

    public boolean isRtnApplicable() {
        return rtnApplicable;
    }

    public long getRtnTimestamp() {
        return rtnTimestamp;
    }

    public void setRtnTimestamp(long rtnTimestamp) {
        this.rtnTimestamp = rtnTimestamp;
    }

    public int getRtnCause() {
        return rtnCause;
    }

    public void setRtnCause(int rtnCause) {
        this.rtnCause = rtnCause;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public LocalizableMessage getMessage() {
        return message;
    }

    public LocalizableMessage getShortMessage() {
        return shortMessage;
    }

    public List<UserComment> getEventComments() {
        return eventComments;
    }

    public void setEventComments(List<UserComment> eventComments) {
        this.eventComments = eventComments;
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


}
