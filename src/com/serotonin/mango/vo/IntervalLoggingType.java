package com.serotonin.mango.vo;

import java.util.stream.Stream;

public enum IntervalLoggingType {

    INSTANT(DataPointVO.IntervalLoggingTypes.INSTANT),
    MAXIMUM(DataPointVO.IntervalLoggingTypes.MAXIMUM),
    MINIMUM(DataPointVO.IntervalLoggingTypes.MINIMUM),
    AVERAGE(DataPointVO.IntervalLoggingTypes.AVERAGE);

    private final int code;

    IntervalLoggingType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static IntervalLoggingType getType(int code) {
        return Stream.of(IntervalLoggingType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(IntervalLoggingType.class.getSimpleName() + " for code " + code + " does not exist"));
    }
}
