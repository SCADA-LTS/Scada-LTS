package com.serotonin.mango.vo;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import java.util.stream.Stream;

public enum UpdateEventType {

    CONTEXT_CHANGE(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE),
    CONTEXT_UPDATE(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE),
    START_OF_CRON(MetaPointLocatorVO.UPDATE_EVENT_CRON),
    START_OF_MINUTE(Common.TimePeriods.MINUTES),
    START_OF_HOUR(Common.TimePeriods.HOURS),
    START_OF_DAY(Common.TimePeriods.DAYS),
    START_OF_WEEK(Common.TimePeriods.WEEKS),
    START_OF_MONTH(Common.TimePeriods.MONTHS),
    START_OF_YEAR(Common.TimePeriods.YEARS);

    private final int code;

    UpdateEventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static UpdateEventType byCode(int code) {
        return Stream.of(UpdateEventType.values())
                .filter(a -> a.getCode() == code)
                .findAny()
                .orElse(UpdateEventType.CONTEXT_CHANGE);
    }
}
