package com.serotonin.mango.vo;

import com.serotonin.mango.Common;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TimePeriodType implements TimePeriod {

    MILLISECONDS(Common.TimePeriods.MILLISECONDS, "common.tp.milliseconds"),
    SECONDS(Common.TimePeriods.SECONDS, "common.tp.seconds") {
        @Override
        public long toMs(long time) {
            return time * 1000;
        }
    },
    MINUTES(Common.TimePeriods.MINUTES, "common.tp.minutes") {
        @Override
        public long toMs(long time) {
            return time * 1000 * 60;
        }
    },
    HOURS(Common.TimePeriods.HOURS, "common.tp.hours") {
        @Override
        public long toMs(long time) {
            return time * 1000 * 60 * 60;
        }
    };

    public static final TimePeriodType DEFAULT = TimePeriodType.SECONDS;

    private final int code;
    private final String key;

    TimePeriodType(int code, String key) {
        this.code = code;
        this.key = key;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
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



