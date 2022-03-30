package com.serotonin.mango.rt.dataImage;

import java.util.stream.Stream;

public enum DataPointSyncMode {
    LOW, MEDIUM, HIGH;

    public static DataPointSyncMode typeOf(String value) {
        if("true".equals(value))
            return DataPointSyncMode.HIGH;
        if("false".equals(value))
            return DataPointSyncMode.LOW;
        return Stream.of(DataPointSyncMode.values())
                .filter(type -> value != null && type.name().equals(value.toUpperCase()))
                .findAny()
                .orElse(DataPointSyncMode.LOW);
    }
}