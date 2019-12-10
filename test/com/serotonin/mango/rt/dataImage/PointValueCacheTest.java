package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PointValueCacheTest {

    private List<PointValueTime> pointValueTimesList = new ArrayList<PointValueTime>();
    private PointValueCache pointValueCache;
    private int MAX_SIZE_OF_POINTVALUECACHE = 2;
    private int COUNTER_OF_OBJECTS_IN_POINTVALUECACHE;

    @Test
    public void checkElasticFunctionalityOfPointValueCacheBoxWithConcreteSizeTest ( ){

        pointValueCache = new PointValueCache();
        pointValueCache.setMaxSize( MAX_SIZE_OF_POINTVALUECACHE );

        for(int pointValue = 1;pointValue <4;pointValue++) {
            addNewPointValueTimeIntoCacheAndLocalList(new PointValueTime(MangoValue.stringToValue(String.valueOf(pointValue), 3), new Long(1)));
            doCacheContainsAllInsertedPointValueTimeObjects();

        }
        Assert.assertEquals(MAX_SIZE_OF_POINTVALUECACHE,COUNTER_OF_OBJECTS_IN_POINTVALUECACHE);

    }

    private void addNewPointValueTimeIntoCacheAndLocalList(PointValueTime pointValueTime){

        pointValueCache.savePointValueIntoCacheAndIntoDbAsyncOrSyncIflogValue(pointValueTime, null, Boolean.FALSE, Boolean.FALSE);

        pointValueTimesList.add(pointValueTime);

    }

    private void doCacheContainsAllInsertedPointValueTimeObjects(){

        COUNTER_OF_OBJECTS_IN_POINTVALUECACHE = 0;
        for(PointValueTime pointValueTime : pointValueTimesList) {

            if (pointValueCache.getCacheContents().contains(pointValueTime)) {
                COUNTER_OF_OBJECTS_IN_POINTVALUECACHE++;
            }
        }
    }
}