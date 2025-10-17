package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;
import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.resetUnreliableDataPoints;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.resetUnreliable;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.setUnreliable;

public class ResetDataPointUnreliableUtilsTest extends AbstractDataPointUnreliableUtilsTest {

    @Test
    public void when_resetUnreliableDataPoints_with_virtual_points_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoints(virtualDataPointsWith_121_122_123);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint116With_M117_V121);
        result.add(metaDataPoint117With_M118_V121_V123);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_virtual_points_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoints(virtualDataPointsWith_121_122_123);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_meta_points_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);
        resetUnreliableDataPoint(virtualDataPoint121);

        //when:
        resetUnreliableDataPoints(metaDataPointsWith_111);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_meta_points_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoints(metaDataPointsWith_111);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint116With_M117_V121);
        result.add(metaDataPoint117With_M118_V121_V123);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_meta_points_with_context_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoints(metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContextWith_112_116_117_118_119_120) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_meta_points_with_context_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoints(metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }


    @Test
    public void when_resetUnreliableDataPoint_with_virtual_point_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(virtualDataPoint121);
        resetUnreliableDataPoint(virtualDataPoint122);
        resetUnreliableDataPoint(virtualDataPoint123);
        resetUnreliableDataPoint(metaDataPoint111);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.addAll(metaDataPointsWithContextWith_112_116_117_118_119_120);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_resetUnreliableDataPoint_with_virtual_point_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(virtualDataPoint121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint122);
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_resetUnreliableDataPoint_with_virtual_point_then_unreliable_false2() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(virtualDataPoint122);
        resetUnreliableDataPoint(virtualDataPoint121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint122);
        result.add(virtualDataPoint121);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_resetUnreliableDataPoint_with_virtual_point_then_unreliable_true2() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(virtualDataPoint122);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.add(metaDataPoint112With_M111_V121);
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(metaDataPoint111);
        resetUnreliableDataPoint(virtualDataPoint121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(metaDataPoint111);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.add(virtualDataPoint122);
        result.add(metaDataPoint116With_M117_V121);
        result.add(metaDataPoint117With_M118_V121_V123);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_with_context_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(metaDataPoint116With_M117_V121);

        //then:
        Assert.assertEquals(false, metaDataPoint116With_M117_V121.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_with_context_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(metaDataPoint116With_M117_V121);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);
        result.add(metaDataPoint117With_M118_V121_V123);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_from_context_meta_points_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(virtualDataPoint121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_from_context_meta_points_then_unreliable_true() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        resetUnreliableDataPoint(metaDataPoint120With_V121);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_for_all_then_unreliable_false1() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

        //when:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_for_all_then_unreliable_false2() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

        //when:
        resetUnreliable(metaDataPointsWith_111, virtualDataPointsWith_121_122_123, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_for_all_then_unreliable_false3() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

        //when:
        resetUnreliable(metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120, virtualDataPointsWith_121_122_123);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_for_all_then_unreliable_false4() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

        //when:
        resetUnreliable(metaDataPointsWithContextWith_112_116_117_118_119_120, virtualDataPointsWith_121_122_123, metaDataPointsWith_111);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_for_all_then_unreliable_false5() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

        //when:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWithContextWith_112_116_117_118_119_120, metaDataPointsWith_111);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_for_all_then_unreliable_false6() {

        //given:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

        //when:
        resetUnreliable(metaDataPointsWithContextWith_112_116_117_118_119_120, metaDataPointsWith_111, virtualDataPointsWith_121_122_123);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }
}