package com.serotonin.mango.vo;

public enum IntervalLoggingType {

    INSTANT(DataPointVO.IntervalLoggingTypes.INSTANT),
    MAXIMUM(DataPointVO.IntervalLoggingTypes.MAXIMUM),
    MINIMUM(DataPointVO.IntervalLoggingTypes.MINIMUM),
    AVERAGE(DataPointVO.IntervalLoggingTypes.AVERAGE);

    private final int id;

    IntervalLoggingType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
