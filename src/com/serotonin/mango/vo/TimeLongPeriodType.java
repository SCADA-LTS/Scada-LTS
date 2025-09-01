package com.serotonin.mango.vo;

import com.serotonin.mango.Common;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TimeLongPeriodType implements TimePeriod {

    DAYS(Common.TimePeriods.DAYS, "common.tp.days") {
        @Override
        public long toMs(long time) {
            return time * 1000 * 60 * 60 * 24;
        }
    },
    WEEKS(Common.TimePeriods.WEEKS, "common.tp.weeks") {
        @Override
        public long toMs(long time) {
            return time * DAYS.toMs(time);
        }
    },
    MONTHS(Common.TimePeriods.MONTHS, "common.tp.months") {
        @Override
        public long toMs(long time) {
            return time * DAYS.toMs(time) * 30;
        }
    },
    YEARS(Common.TimePeriods.YEARS, "common.tp.years") {
        @Override
        public long toMs(long time) {
            return time * DAYS.toMs(time) * 365;
        }
    };

    public static final TimeLongPeriodType DEFAULT = TimeLongPeriodType.DAYS;

    private final int code;
    private final String key;

    TimeLongPeriodType(int code, String key) {
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
        return Stream.of(TimeLongPeriodType.values())
                .collect(Collectors.toMap(Enum::name, TimeLongPeriodType::getCode));
    }

    public static TimeLongPeriodType getType(int code) {
        return Stream.of(TimeLongPeriodType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElse(TimeLongPeriodType.DAYS);
    }

    public static TimeLongPeriodType getType(String name) {
        return Stream.of(TimeLongPeriodType.values())
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findAny()
                .orElse(TimeLongPeriodType.DAYS);
    }
}



