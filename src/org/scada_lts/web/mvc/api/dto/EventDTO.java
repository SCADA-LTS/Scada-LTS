package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.vo.UserComment;

import java.util.List;

public class EventDTO {

    private int id;
    private int typeId;
    private int typeRef1;
    private int typeRef2;
    private long activeTs;
    private boolean rtnApplicable;
    private long rtnTs;
    private int rtnCause;
    private int alarmLevel;
    private String message;
    private long ackTs;
    private String username;
    private int ackAlternateSource;
    private List<UserComment> userComments;

    public EventDTO() {
    }

    public EventDTO(int id, int typeId, int typeRef1, int typeRef2, long activeTs, boolean rtnApplicable, long rtnTs, int rtnCause, int alarmLevel, String message, long ackTs, String username, int ackAlternateSource) {
        this.id = id;
        this.typeId = typeId;
        this.typeRef1 = typeRef1;
        this.typeRef2 = typeRef2;
        this.activeTs = activeTs;
        this.rtnApplicable = rtnApplicable;
        this.rtnTs = rtnTs;
        this.rtnCause = rtnCause;
        this.alarmLevel = alarmLevel;
        this.message = message;
        this.ackTs = ackTs;
        this.username = username;
        this.ackAlternateSource = ackAlternateSource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeRef1() {
        return typeRef1;
    }

    public void setTypeRef1(int typeRef1) {
        this.typeRef1 = typeRef1;
    }

    public int getTypeRef2() {
        return typeRef2;
    }

    public void setTypeRef2(int typeRef2) {
        this.typeRef2 = typeRef2;
    }

    public long getActiveTs() {
        return activeTs;
    }

    public void setActiveTs(long activeTs) {
        this.activeTs = activeTs;
    }

    public boolean isRtnApplicable() {
        return rtnApplicable;
    }

    public void setRtnApplicable(boolean rtnApplicable) {
        this.rtnApplicable = rtnApplicable;
    }

    public long getRtnTs() {
        return rtnTs;
    }

    public void setRtnTs(long rtnTs) {
        this.rtnTs = rtnTs;
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

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getAckTs() {
        return ackTs;
    }

    public void setAckTs(long ackTs) {
        this.ackTs = ackTs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAckAlternateSource() {
        return ackAlternateSource;
    }

    public void setAckAlternateSource(int ackAlternateSource) {
        this.ackAlternateSource = ackAlternateSource;
    }

    public List<UserComment> getUserComments() {
        return userComments;
    }

    public void setUserComments(List<UserComment> userComments) {
        this.userComments = userComments;
    }
}
