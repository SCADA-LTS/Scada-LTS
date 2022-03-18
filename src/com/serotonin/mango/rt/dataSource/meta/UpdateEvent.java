package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

public enum UpdateEvent {

    CONTEXT_UPDATE(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE),
    CONTEXT_CHANGE(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE),
    CRON_PATTERN(MetaPointLocatorVO.UPDATE_EVENT_CRON),
    START_OF_MINUTE(Common.TimePeriods.MINUTES),
    START_OF_HOUR(Common.TimePeriods.HOURS),
    START_OF_DAY(Common.TimePeriods.DAYS),
    START_OF_WEEK(Common.TimePeriods.WEEKS),
    START_OF_MONTH(Common.TimePeriods.MONTHS),
    START_OF_YEAR(Common.TimePeriods.YEARS);

    private final int code;

    UpdateEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
