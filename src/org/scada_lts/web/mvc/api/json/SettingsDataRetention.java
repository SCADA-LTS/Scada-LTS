package org.scada_lts.web.mvc.api.json;

public class SettingsDataRetention {

    private boolean groveLogging;
    private int eventPurgePeriodType;
    private int eventPurgePeriods;
    private int reportPurgePeriodType;
    private int reportPurgePeriods;
    private int futureDateLimitPeriodType;
    private int futureDateLimitPeriods;
    private int valuesLimitForPurge;

    public SettingsDataRetention() {
    }

    public SettingsDataRetention(boolean groveLogging, int eventPurgePeriodType, int eventPurgePeriods, int reportPurgePeriodType, int reportPurgePeriods, int futureDateLimitPeriodType, int futureDateLimitPeriods, int valuesLimitForPurge) {
        this.groveLogging = groveLogging;
        this.eventPurgePeriodType = eventPurgePeriodType;
        this.eventPurgePeriods = eventPurgePeriods;
        this.reportPurgePeriodType = reportPurgePeriodType;
        this.reportPurgePeriods = reportPurgePeriods;
        this.futureDateLimitPeriodType = futureDateLimitPeriodType;
        this.futureDateLimitPeriods = futureDateLimitPeriods;
        this.valuesLimitForPurge = valuesLimitForPurge;
    }

    public boolean isGroveLogging() {
        return groveLogging;
    }

    public void setGroveLogging(boolean groveLogging) {
        this.groveLogging = groveLogging;
    }

    public int getEventPurgePeriodType() {
        return eventPurgePeriodType;
    }

    public void setEventPurgePeriodType(int eventPurgePeriodType) {
        this.eventPurgePeriodType = eventPurgePeriodType;
    }

    public int getEventPurgePeriods() {
        return eventPurgePeriods;
    }

    public void setEventPurgePeriods(int eventPurgePeriods) {
        this.eventPurgePeriods = eventPurgePeriods;
    }

    public int getReportPurgePeriodType() {
        return reportPurgePeriodType;
    }

    public void setReportPurgePeriodType(int reportPurgePeriodType) {
        this.reportPurgePeriodType = reportPurgePeriodType;
    }

    public int getReportPurgePeriods() {
        return reportPurgePeriods;
    }

    public void setReportPurgePeriods(int reportPurgePeriods) {
        this.reportPurgePeriods = reportPurgePeriods;
    }

    public int getFutureDateLimitPeriodType() {
        return futureDateLimitPeriodType;
    }

    public void setFutureDateLimitPeriodType(int futureDateLimitPeriodType) {
        this.futureDateLimitPeriodType = futureDateLimitPeriodType;
    }

    public int getFutureDateLimitPeriods() {
        return futureDateLimitPeriods;
    }

    public void setFutureDateLimitPeriods(int futureDateLimitPeriods) {
        this.futureDateLimitPeriods = futureDateLimitPeriods;
    }

    public int getValuesLimitForPurge() {
        return valuesLimitForPurge;
    }

    public void setValuesLimitForPurge(int valuesLimitForPurge) {
        this.valuesLimitForPurge = valuesLimitForPurge;
    }
}
