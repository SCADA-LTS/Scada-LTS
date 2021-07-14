package com.serotonin.mango.vo;

import com.serotonin.mango.Common;

public enum IntervalLoggingPeriodType {

    SECONDS(Common.TimePeriods.SECONDS),
    MINUTES(Common.TimePeriods.MINUTES),
    HOURS(Common.TimePeriods.HOURS),
    DAYS(Common.TimePeriods.DAYS),
    WEEKS(Common.TimePeriods.WEEKS),
    MONTHS(Common.TimePeriods.MONTHS),
    YEARS(Common.TimePeriods.YEARS);

    private final int id;

    IntervalLoggingPeriodType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
