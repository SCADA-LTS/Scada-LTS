package org.scada_lts.cached;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.cache.PointEventDetectorsCache;

import java.util.List;

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

        pointEventDetectorsCache.addEventDetector(dataPointVO.getId(), createPointEventDetectorVO());
        Assert.assertEquals(1, pointEventDetectorsCache.getEventDetectors(dataPointVO).size());
    }
    @Test
    public void updateEventDetectorTest(){

        DataPointVO dataPointVO = createDataPointVO();

        pointEventDetectorsCache.clearCacheUnderDataPoint( dataPointVO.getId() );
        pointEventDetectorsCache.addEventDetector(dataPointVO.getId(), createPointEventDetectorVO());

        PointEventDetectorVO pointEventDetectorVO = pointEventDetectorsCache.getEventDetectors( dataPointVO ).get(0);
        pointEventDetectorVO.setAlias("NewAlias");
        pointEventDetectorsCache.updateEventDetector(dataPointVO.getId(),pointEventDetectorVO);
        pointEventDetectorVO = null;

        PointEventDetectorVO updatedPointEventDetectorVO = pointEventDetectorsCache.getEventDetectors( dataPointVO ).get(0);

        Assert.assertEquals("NewAlias",updatedPointEventDetectorVO.getAlias() );
    }
    @Test
    public void resertCacheTest() {
        int EMPTY_LIST = 0;
        DataPointVO dataPointVO = createDataPointVO();
        pointEventDetectorsCache.addEventDetector(dataPointVO.getId(), createPointEventDetectorVO());
        pointEventDetectorsCache.clearCacheUnderDataPoint( dataPointVO.getId());
        List<PointEventDetectorVO> pointEventDetectorsCaches = pointEventDetectorsCache.getEventDetectorsForDataPointId( dataPointVO.getId() );

        Assert.assertEquals(EMPTY_LIST, pointEventDetectorsCaches.size() );
    }
    @Test
    public void removeEventDetectorTest(){
        int EMPTY_LIST = 0;
        DataPointVO dataPointVO = createDataPointVO();

        pointEventDetectorsCache.clearCacheUnderDataPoint( dataPointVO.getId() );
        pointEventDetectorsCache.addEventDetector(dataPointVO.getId(), createPointEventDetectorVO());
        PointEventDetectorVO pointEventDetectorToRemove = createPointEventDetectorVO();

        pointEventDetectorsCache.removeEventDetector( dataPointVO.getId(), pointEventDetectorToRemove);

        Assert.assertEquals(EMPTY_LIST, pointEventDetectorsCache.getEventDetectors( dataPointVO ).size() );
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
    private PointEventDetectorVO createPointEventDetectorVO(){
        PointEventDetectorVO pointEventDetectorVO = new PointEventDetectorVO();
        pointEventDetectorVO.setXid("XID1");
        pointEventDetectorVO.setId(2);
        return pointEventDetectorVO;
    }
}
