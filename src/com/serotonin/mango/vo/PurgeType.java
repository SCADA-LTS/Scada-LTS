package com.serotonin.mango.vo;

public enum PurgeType {

    DAYS(DataPointVO.PurgeTypes.DAYS),
    WEEKS(DataPointVO.PurgeTypes.WEEKS),
    MONTHS(DataPointVO.PurgeTypes.MONTHS),
    YEARS(DataPointVO.PurgeTypes.YEARS);

    private final int id;

    PurgeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
