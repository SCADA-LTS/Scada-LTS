package com.serotonin.mango.rt.dataImage;

import java.util.stream.Stream;

public enum DataPointSyncMode {

    LOW("none", DataPointRT.class),
    MEDIUM("partial", DataPointNonSyncRT.class),
    HIGH("all", DataPointSynchronizedRT.class);

    private String name;
    private Class<?> type;

    DataPointSyncMode(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name.toUpperCase();
    }

    public Class<?> getType() {
        return type;
    }

    public static DataPointSyncMode typeOf(String value) {
        if(value == null)
            return DataPointSyncMode.LOW;
        if("true".equalsIgnoreCase(value))
            return DataPointSyncMode.HIGH;
        if("false".equalsIgnoreCase(value))
            return DataPointSyncMode.LOW;
        return Stream.of(DataPointSyncMode.values())
                .filter(type -> type.name().equalsIgnoreCase(value) ||
                        type.getName().equalsIgnoreCase(value))
                .findAny()
                .orElse(DataPointSyncMode.LOW);
    }

    public static String getName(String name) {
        return typeOf(name).getName();
    }
}