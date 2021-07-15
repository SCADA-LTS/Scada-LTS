package com.serotonin.mango.vo;

import com.serotonin.mango.Common;

import java.util.stream.Stream;

public enum IntervalLoggingPeriodType {

    SECONDS(Common.TimePeriods.SECONDS),
    MINUTES(Common.TimePeriods.MINUTES),
    HOURS(Common.TimePeriods.HOURS),
    DAYS(Common.TimePeriods.DAYS),
    WEEKS(Common.TimePeriods.WEEKS),
    MONTHS(Common.TimePeriods.MONTHS),
    YEARS(Common.TimePeriods.YEARS);

    private final int code;

    IntervalLoggingPeriodType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static IntervalLoggingPeriodType getType(int code) {
        return Stream.of(IntervalLoggingPeriodType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(IntervalLoggingPeriodType.class.getSimpleName() + "for code " + code + " does not exist"));
    }
}
