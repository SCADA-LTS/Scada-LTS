package com.serotonin.mango.vo;

import java.util.stream.Stream;

public enum PurgeType {

    DAYS(DataPointVO.PurgeTypes.DAYS),
    WEEKS(DataPointVO.PurgeTypes.WEEKS),
    MONTHS(DataPointVO.PurgeTypes.MONTHS),
    YEARS(DataPointVO.PurgeTypes.YEARS);

    private final int code;

    PurgeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PurgeType getType(int code) {
        return Stream.of(PurgeType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(PurgeType.class.getSimpleName() + " for code " + code + " does not exist"));
    }
}
