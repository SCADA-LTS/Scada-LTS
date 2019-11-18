package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.dataImage.exceptions_level.ExceptionsArea;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class PointValueCacheCooperateWithPointValueDao {

    private PointValueDao pointValueDao;

    PointValueTime savePointValue(
            PointValueTime pointValueTime,
            SetPointSource pointSource,
            boolean async,
            int dataPointId){

        if (async)
            getPointValueDao().savePointValueAsync(dataPointId, pointValueTime, pointSource);
        else
            pointValueTime = getPointValueDao().savePointValueSync(dataPointId, pointValueTime, pointSource);

        return pointValueTime;
    }

    void logPointValueAsync(PointValueTime pointValue, SetPointSource source, int dataPointId) {
        // Save the new value and get a point value time back that has the id and annotations set, as appropriate.
        getPointValueDao().savePointValueAsync(dataPointId, pointValue, source);

    }

    PointValueTime getLatestPointValueFromDao(int dataPointId) {

        return getPointValueDao().getLatestPointValue( dataPointId );

    }

    LinkedList<PointValueTime> getDefinedLimitRowsOfLatestPointValues(int dataPointId, int limit) {

        LinkedList<PointValueTime> pointValueTimes = new LinkedList<PointValueTime>();

        for(PointValueTime pointValueTime: getLatestPointValuesByDataPointIdAndLimit(dataPointId,limit)){
            pointValueTimes.addFirst(pointValueTime);
        }

        return pointValueTimes;
    }

    private List<PointValueTime> getLatestPointValuesByDataPointIdAndLimit(int dataPointId,int limit) {

        List<PointValueTime> pointValueTimes = getPointValueDao().getLatestPointValues(dataPointId,limit);

        return (new ExceptionsArea().doListIsNull( pointValueTimes ))
                ?Collections.emptyList()
                :pointValueTimes;
    }

    private PointValueDao getPointValueDao() {

        if(pointValueDao == null) {
            pointValueDao = new PointValueDao();
        }

        return pointValueDao;

    }
}
