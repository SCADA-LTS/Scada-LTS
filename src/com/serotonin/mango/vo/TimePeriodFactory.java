package com.serotonin.mango.vo;

public class TimePeriodFactory {

    public static TimePeriod getByCode(int code) {
        for (TimePeriodType tpt : TimePeriodType.values()) {
            if (tpt.getCode() == code)
                return tpt;
        }

        for (TimeLongPeriodType tlpt : TimeLongPeriodType.values()) {
            if (tlpt.getCode() == code)
                return tlpt;
        }

        return TimeLongPeriodType.DEFAULT;
    }

    public static TimePeriod getByName(String name) {
        if (name == null) return TimePeriodType.DEFAULT;

        for (TimePeriodType tpt : TimePeriodType.values()) {
            if (tpt.name().equalsIgnoreCase(name))
                return tpt;
        }

        for (TimeLongPeriodType tlpt : TimeLongPeriodType.values()) {
            if (tlpt.name().equalsIgnoreCase(name))
                return tlpt;
        }

        return TimeLongPeriodType.DEFAULT;
    }
}
