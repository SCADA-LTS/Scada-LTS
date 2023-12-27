package com.serotonin.mango.rt.dataImage;

import java.util.List;

interface IDataPointCache {
    PointValueCache getPointValueCache();
    List<PointValueTime> getLatestPointValuesUsedForJunitTest(int limit);
    void addCollectionIntoCache(PointValueTime pvt);
    PointValueTime getPointValueAt(long time);
    void resetValues();
}