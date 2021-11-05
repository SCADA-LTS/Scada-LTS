package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.Common;

public class JsonEventSearch {

    private int alarmLevel = Common.NEW_ID;
    private String keywords;
    private String status;
    private int eventSourceType;
    private String datapoint;
    private int limit;
    private int offset;
    private String[] sortBy;
    private boolean[] sortDesc;
    private long lastLogin;
    private int receiveAlarmEmails;
    private boolean receiveOwnAuditEvents;
    private String theme;
    private boolean hideMenu;
    private int userProfile;

    public JsonEventSearch() {
    }

    public JsonEventSearch(int alarmLevel, String keywords, String status, int eventSourceType, String datapoint, int limit, int offset, String[] sortBy, boolean[] sortDesc, long lastLogin, int receiveAlarmEmails, boolean receiveOwnAuditEvents, String theme, boolean hideMenu, int userProfile) {
        this.alarmLevel = alarmLevel;
        this.keywords = keywords;
        this.status = status;
        this.eventSourceType = eventSourceType;
        this.datapoint = datapoint;
        this.limit = limit;
        this.offset = offset;
        this.sortBy = sortBy;
        this.sortDesc = sortDesc;
        this.lastLogin = lastLogin;
        this.receiveAlarmEmails = receiveAlarmEmails;
        this.receiveOwnAuditEvents = receiveOwnAuditEvents;
        this.theme = theme;
        this.hideMenu = hideMenu;
        this.userProfile = userProfile;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getStatus() {
        return status;
    }

    public int getEventSourceType() {
        return eventSourceType;
    }

    public String getDatapoint() {
        return datapoint;
    }

    public String[] getSortBy() {
        return sortBy;
    }

    public boolean[] getSortDesc() {
        return sortDesc;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public int getReceiveAlarmEmails() {
        return receiveAlarmEmails;
    }

    public boolean isReceiveOwnAuditEvents() {
        return receiveOwnAuditEvents;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isHideMenu() {
        return hideMenu;
    }

    public int getUserProfile() {
        return userProfile;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEventSourceType(int eventSourceType) {
        this.eventSourceType = eventSourceType;
    }

    public void setDatapoint(String datapoint) {
        this.datapoint = datapoint;
    }

    public void setSortBy(String[] sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortDesc(boolean[] sortDesc) {
        this.sortDesc = sortDesc;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setReceiveAlarmEmails(int receiveAlarmEmails) {
        this.receiveAlarmEmails = receiveAlarmEmails;
    }

    public void setReceiveOwnAuditEvents(boolean receiveOwnAuditEvents) {
        this.receiveOwnAuditEvents = receiveOwnAuditEvents;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setHideMenu(boolean hideMenu) {
        this.hideMenu = hideMenu;
    }

    public void setUserProfile(int userProfile) {
        this.userProfile = userProfile;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
