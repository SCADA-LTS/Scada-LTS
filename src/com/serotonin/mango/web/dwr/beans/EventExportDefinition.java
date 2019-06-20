package com.serotonin.mango.web.dwr.beans;


public class EventExportDefinition {
    private final int eventId;
    private final int eventSourceType;
    private final String status;
    private final int alarmLevel;
    private final String[] keywords;
    private final long dateFrom;
    private final long dateTo;
    private final int userId;

    public EventExportDefinition(int eventId, int eventSourceType, String status, int alarmLevel, String[] keywords,
            long dateFrom, long dateTo, int userId) {
        this.eventId = eventId;
        this.eventSourceType = eventSourceType;
        this.status = status;
        this.alarmLevel = alarmLevel;
        this.keywords = keywords;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getEventSourceType() {
        return eventSourceType;
    }

    public String getStatus() {
        return status;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public long getDateFrom() {
        return dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    public int getUserId() {
        return userId;
    }
}
