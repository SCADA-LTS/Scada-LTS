package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.PointValueDao;
import java.util.LinkedList;

/**
 *Responsibility :
 *
 * Because in previous one version on application in PointValueCache were
 * a lot of places in code where was invoked dao, so this dao has been
 * moved to separated class who only cooperates with dao for cache that's why that is private.
 *
 * @author Maateusz Hyski
 **/
class BridgeBetweenPointValueCacheAndPointValueDao {

    private PointValueDao pointValueDao = new PointValueDao();
    private int dataPointId;

    BridgeBetweenPointValueCacheAndPointValueDao(int dataPointId) {
        this.dataPointId = dataPointId;
    }


    PointValueTime savePointValue(PointValueTime pointValueTime, SetPointSource source, boolean async) {

        if (async)
            pointValueDao.savePointValueAsync(dataPointId, pointValueTime, source);
        else
            pointValueTime = pointValueDao.savePointValueSync(dataPointId, pointValueTime, source);


        return pointValueTime;
    }

    void logPointValueAsync(PointValueTime pointValue, SetPointSource source) {
        // Save the new value and get a point value time back that has the id and annotations set, as appropriate.
        pointValueDao.savePointValueAsync(dataPointId, pointValue, source);
    }

    PointValueTime getLatestPointValueFromDao() {
        return pointValueDao.getLatestPointValue(dataPointId);
    }

    LinkedList<PointValueTime> getDefinedLimitRowsOfLatestPointValues(int limit) {
        LinkedList<PointValueTime> pointValueTimes = new LinkedList<PointValueTime>();
        for(PointValueTime pointValueTime: pointValueDao.getLatestPointValues(dataPointId,limit)){
            pointValueTimes.addFirst(pointValueTime);
        }
        return pointValueTimes;
    }
}
