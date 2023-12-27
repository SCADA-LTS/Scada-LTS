package com.serotonin.mango.vo;

import com.serotonin.mango.DataTypes;

import java.util.stream.Stream;

public enum PointDataType {

    UNKNOWN(DataTypes.UNKNOWN),
    BINARY(DataTypes.BINARY),
    MULTISTATE(DataTypes.MULTISTATE),
    NUMERIC(DataTypes.NUMERIC),
    ALPHANUMERIC(DataTypes.ALPHANUMERIC),
    IMAGE(DataTypes.IMAGE);

    private final int code;

    PointDataType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PointDataType byCode(int code) {
        return Stream.of(PointDataType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElse(PointDataType.UNKNOWN);
    }
}
