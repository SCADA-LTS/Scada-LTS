package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.resetUnreliableDataPoint;
import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.setUnreliableDataPoint;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.resetUnreliable;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.setUnreliable;

public class GetRunningMetaDataPointsTest extends AbstractDataPointUnreliableUtilsTest {

    @Test
    public void when_getRunningDataSource_virtual_data_source_then_initialized_true() {

        //when:
        DataSourceRT dataSourceRT = runtimeManager.getRunningDataSource(virtualDataSource567.getId());

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());

    }

    @Test
    public void when_getRunningDataSource_meta_data_source_then_initialized_true() {

        //when:
        DataSourceRT dataSourceRT = runtimeManager.getRunningDataSource(metaDataSource123.getId());

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());

    }

    @Test
    public void when_getRunningDataSource_meta_data_source_with_context_then_initialized_true() {

        //when:
        DataSourceRT dataSourceRT = runtimeManager.getRunningDataSource(metaDataSource345.getId());

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());

    }

    @Test
    public void when_getRunningMetaDataPointsToReset_with_unreliable_true_for_all_points_unreliable_false_then_return_list_points_empty() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        resetUnreliableDataPoint(virtualDataPoint121);

        //when:
        List<DataPointRT> result = runtimeManager.getRunningMetaDataPointsToReset(virtualDataPoint121.getId());

        //then:
        Assert.assertEquals(true, result.isEmpty());
    }

    @Test
    public void when_getRunningMetaDataPointsToSet_with_unreliable_false_for_all_points_unreliable_false_then_return_list_points_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        resetUnreliableDataPoint(metaDataPoint111);
        resetUnreliableDataPoint(virtualDataPoint121);
        resetUnreliableDataPoint(virtualDataPoint122);
        resetUnreliableDataPoint(virtualDataPoint123);

        //when:
        List<DataPointRT> result = runtimeManager.getRunningMetaDataPointsToSet(virtualDataPoint121.getId());

        //then:
        Assert.assertEquals(false, result.isEmpty());
        Assert.assertEquals(4, result.size());
        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_getRunningMetaDataPointsToSet_with_unreliable_false_for_all_points_unreliable_true_then_return_list_points_empty() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        setUnreliableDataPoint(virtualDataPoint121);

        //when:
        List<DataPointRT> result = runtimeManager.getRunningMetaDataPointsToSet(virtualDataPoint121.getId());

        //then:
        Assert.assertEquals(true, result.isEmpty());
    }

    @Test
    public void when_getRunningMetaDataPointsToReset_with_unreliable_true_for_all_points_unreliable_true_then_return_list_points_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        setUnreliableDataPoint(virtualDataPoint121);

        //when:
        List<DataPointRT> result = runtimeManager.getRunningMetaDataPointsToReset(virtualDataPoint121.getId());

        //then:
        Assert.assertEquals(2, result.size());
        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }


    @Test
    public void when_getRunningMetaDataPoints_with_unreliable_true_for_all_points_unreliable_true_then_return_list_points_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        setUnreliableDataPoint(virtualDataPoint121);

        //when:
        List<DataPointRT> result = runtimeManager.getRunningMetaDataPoints(virtualDataPoint121.getId(), true);

        //then:
        Assert.assertEquals(4, result.size());
        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }
}
