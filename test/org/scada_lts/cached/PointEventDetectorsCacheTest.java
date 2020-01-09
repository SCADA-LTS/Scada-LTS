package org.scada_lts.cached;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.cache.PointEventDetectorsCache;

@RunWith(JUnit4.class)
public class PointEventDetectorsCacheTest {

    private PointEventDetectorsCache pointEventDetectorsCache;

    @Before
    public void initialize() {
        pointEventDetectorsCache = new PointEventDetectorsCache( null );
    }
    @Test
    public void cacheIsEmptyTest(){
        DataPointVO dataPointVO = createDataPointVO();

        Assert.assertEquals(0, pointEventDetectorsCache.getEventDetectors(dataPointVO).size());
    }

    @Test
    public void addNewEventDetectorTest(){
        DataPointVO dataPointVO = createDataPointVO();

        addEventDetector(dataPointVO, createPointEventDetectorVO() );
        Assert.assertEquals(1, pointEventDetectorsCache.getEventDetectors(dataPointVO).size());
    }
    @Test
    public void updateEventDetectorTest(){

        DataPointVO dataPointVO = createDataPointVO();

        pointEventDetectorsCache.clearCacheUnderDataPoint( dataPointVO.getId() );
        addEventDetector( dataPointVO ,createPointEventDetectorVO() );

        PointEventDetectorVO pointEventDetectorVO = pointEventDetectorsCache.getEventDetectors( dataPointVO ).get(0);
        pointEventDetectorVO.setAlias("NewAlias");
        pointEventDetectorsCache.updateEventDetector(dataPointVO.getId(),pointEventDetectorVO);
        pointEventDetectorVO = null;

        PointEventDetectorVO updatedPointEventDetectorVO = pointEventDetectorsCache.getEventDetectors( dataPointVO ).get(0);

        Assert.assertEquals("NewAlias",updatedPointEventDetectorVO.getAlias() );
    }
    @Test
    public void resertCacheTest() {

        DataPointVO dataPointVO = createDataPointVO();
        addEventDetector( dataPointVO, createPointEventDetectorVO() );
    }
    @Test
    public void removeEventDetectorTest(){

        DataPointVO dataPointVO = createDataPointVO();

        pointEventDetectorsCache.clearCacheUnderDataPoint( dataPointVO.getId() );
        addEventDetector( dataPointVO, createPointEventDetectorVO() );

        PointEventDetectorVO pointEventDetectorToRemove = createPointEventDetectorVO();

        pointEventDetectorsCache.removeEventDetector( dataPointVO.getId(), pointEventDetectorToRemove);

        Assert.assertEquals(0, pointEventDetectorsCache.getEventDetectors( dataPointVO ).size() );
    }


    @After
    public void finalize() {
        pointEventDetectorsCache = null;
    }

    private DataPointVO createDataPointVO() {
        DataPointVO dataPointVO = new DataPointVO();
        dataPointVO.setId(2);
        return dataPointVO;
    }
    private void addEventDetector( DataPointVO dataPointVO, PointEventDetectorVO newPointEventDetectorVO  ){
        pointEventDetectorsCache.addEventDetector(dataPointVO.getId(), newPointEventDetectorVO);
    }
    private PointEventDetectorVO createPointEventDetectorVO(){
        PointEventDetectorVO pointEventDetectorVO = new PointEventDetectorVO();
        pointEventDetectorVO.setXid("XID1");
        pointEventDetectorVO.setId(2);
        return pointEventDetectorVO;
    }
}
