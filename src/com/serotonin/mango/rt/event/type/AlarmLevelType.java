package com.serotonin.mango.rt.event.type;

import com.serotonin.mango.rt.event.AlarmLevels;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AlarmLevelType {

    NONE(AlarmLevels.NONE),
    INFORMATION(AlarmLevels.INFORMATION),
    URGENT(AlarmLevels.URGENT),
    CRITICAL(AlarmLevels.CRITICAL),
    LIFE_SAFETY(AlarmLevels.LIFE_SAFETY);
    private final int code;
    AlarmLevelType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static List<AlarmLevelType> getAlarmLevels() {
        return Stream.of(AlarmLevelType.values()).collect(Collectors.toList());
    }

    public static List<AlarmLevelType> getAlarmLevelsWithoutNone() {
        return Stream.of(AlarmLevelType.values()).filter(a -> a != AlarmLevelType.NONE).collect(Collectors.toList());
    }
}
