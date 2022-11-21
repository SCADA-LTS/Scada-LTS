package com.serotonin.mango.vo;

import java.util.stream.Stream;

public enum LoggingType {

    ON_CHANGE(DataPointVO.LoggingTypes.ON_CHANGE),
    ALL(DataPointVO.LoggingTypes.ALL),
    NONE(DataPointVO.LoggingTypes.NONE),
    INTERVAL(DataPointVO.LoggingTypes.INTERVAL),
    ON_TS_CHANGE(DataPointVO.LoggingTypes.ON_TS_CHANGE);

    private final int code;

    LoggingType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static LoggingType getType(int code) {
        return Stream.of(LoggingType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(LoggingType.class.getSimpleName() + " for code " + code + " does not exist"));
    }
}
