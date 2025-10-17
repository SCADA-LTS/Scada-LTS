package com.serotonin.mango.rt.dataSource;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.resetUnreliableDataPoint;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.setUnreliable;

public class CyclicGetRunningMetaDataPointsTest extends AbstractDataPointUnreliableUtilsTest {

    @Override
    protected void afterConfig() {
        DataPointVO dataPoint = metaDataPoint120With_V121.getVO();
        MetaPointLocatorVO metaPointLocator120 = new MetaPointLocatorVO();
        metaPointLocator120.setContext(Arrays.asList(new IntValuePair(118,""), new IntValuePair(121,"")));
        dataPoint.setPointLocator(metaPointLocator120);
        runtimeManager.saveDataPoint(dataPoint);
    }

    @Override
    protected void afterTest() {
        DataPointVO dataPoint = metaDataPoint120With_V121.getVO();
        MetaPointLocatorVO metaPointLocator120 = new MetaPointLocatorVO();
        metaPointLocator120.setContext(Arrays.asList(new IntValuePair(121,"")));
        dataPoint.setPointLocator(metaPointLocator120);
        runtimeManager.saveDataPoint(dataPoint);
    }


    @Test
    public void when_getRunningMetaDataPoints_with_unreliable_false_for_all_points_unreliable_false_then_return_list_points_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        resetUnreliableDataPoint(metaDataPoint111);
        resetUnreliableDataPoint(virtualDataPoint121);
        resetUnreliableDataPoint(virtualDataPoint122);
        resetUnreliableDataPoint(virtualDataPoint123);

        //when:
        List<DataPointRT> result = runtimeManager.getRunningMetaDataPointsToSet(virtualDataPoint121.getId());

        //then:
        Assert.assertEquals(1, result.size());
        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

}
