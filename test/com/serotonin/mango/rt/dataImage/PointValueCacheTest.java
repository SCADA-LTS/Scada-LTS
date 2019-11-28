package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PointValueCacheTest {

    private static final Log LOG = LogFactory.getLog(PointValueCacheTest.class);
    private List<PointValueTime> pointValueTimesList = new ArrayList<PointValueTime>();
    private PointValueCache pointValueCache;
    private int MAX_SIZE_OF_POINTVALUECACHE = 2;
    private int COUNTER_OF_OBJECTS_IN_POINTVALUECACHE;

    @Test
    public void checkElasticFunctionalityOfPointValueCacheBoxWithConcreteSizeTest ( ){

        pointValueCache = new PointValueCache();
        pointValueCache.setMaxSize( MAX_SIZE_OF_POINTVALUECACHE );
        LOG.info("Size of pointValueCache has been set to 2");

        for(int pointValue = 1;pointValue <4;pointValue++) {
            addNewPointValueTimeIntoCacheAndLocalList(new PointValueTime(MangoValue.stringToValue(String.valueOf(pointValue), 3), new Long(1)));

            doCacheContainsAllInsertedPointValueTimeObjects();

        }
        Assert.assertEquals(MAX_SIZE_OF_POINTVALUECACHE,COUNTER_OF_OBJECTS_IN_POINTVALUECACHE);

    }

    private void addNewPointValueTimeIntoCacheAndLocalList(PointValueTime pointValueTime){

        LOG.info("--- Add new PointValueTime Object into PointValueTimeCache ----");
        pointValueCache.savePointValueIntoCacheAndIntoDbAsyncOrSyncIflogValue(pointValueTime, null, Boolean.FALSE, Boolean.FALSE);

        pointValueTimesList.add(pointValueTime);

    }

    private void doCacheContainsAllInsertedPointValueTimeObjects(){

        LOG.info("--- Start of comparision ----");
        LOG.info("All present Scada pointValueCache ");
        pointValueCache.getCacheContents().forEach(System.out::println);
        LOG.info("End present Scada pointValueCache ");

        LOG.info("Comparing Scada pointValueCache with local list depending on the set Scada PointValueCache ");
        COUNTER_OF_OBJECTS_IN_POINTVALUECACHE = 0;
        for(PointValueTime pointValueTime : pointValueTimesList) {

            if (pointValueCache.getCacheContents().contains(pointValueTime)) {
                LOG.info(pointValueTime +" exist in Scada PointValueCache");
                COUNTER_OF_OBJECTS_IN_POINTVALUECACHE++;
            } else {
                LOG.info(pointValueTime +" do not exist in Scada PointValueCache");
            }
        }
        LOG.info("--- End of comparison ----");
    }
}