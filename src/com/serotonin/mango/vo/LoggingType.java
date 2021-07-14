package com.serotonin.mango.vo;

public enum LoggingType {

    ON_CHANGE(DataPointVO.LoggingTypes.ON_CHANGE),
    ALL(DataPointVO.LoggingTypes.ALL),
    NONE(DataPointVO.LoggingTypes.NONE),
    INTERVAL(DataPointVO.LoggingTypes.INTERVAL),
    ON_TS_CHANGE(DataPointVO.LoggingTypes.ON_TS_CHANGE);

    private final int id;

    LoggingType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
