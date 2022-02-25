package com.serotonin.mango.vo;

import com.serotonin.mango.Common;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TimePeriodType {

    MILLISECONDS(Common.TimePeriods.MILLISECONDS),
    SECONDS(Common.TimePeriods.SECONDS) {
        @Override
        public long toMs(long time) {
            return time * 1000;
        }
    },
    MINUTES(Common.TimePeriods.MINUTES) {
        @Override
        public long toMs(long time) {
            return time * 1000 * 60;
        }
    },
    HOURS(Common.TimePeriods.HOURS) {
        @Override
        public long toMs(long time) {
            return time * 1000 * 60 * 60;
        }
    };

    private final int code;

    TimePeriodType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public long toMs(long value) {
        return value;
    }

    public static Map<String, Integer> codes() {
        return Stream.of(TimePeriodType.values())
                .collect(Collectors.toMap(Enum::name, TimePeriodType::getCode));
    }

    public static TimePeriodType getType(int code) {
        return Stream.of(TimePeriodType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElse(TimePeriodType.SECONDS);
    }

    public static TimePeriodType getType(String name) {
        return Stream.of(TimePeriodType.values())
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findAny()
                .orElse(TimePeriodType.SECONDS);
    }
}



