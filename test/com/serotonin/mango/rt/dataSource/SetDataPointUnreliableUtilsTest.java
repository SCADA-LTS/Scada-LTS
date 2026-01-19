package com.serotonin.mango.rt.dataSource;

import org.junit.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.util.ArrayList;
import java.util.List;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.resetUnreliable;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.setUnreliable;

public class SetDataPointUnreliableUtilsTest extends AbstractDataPointUnreliableUtilsTest {


    ///

    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_then_unreliable_true2___() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(virtualDataPoint123);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint123);
        result.add(metaDataPoint116With_M117_V121);
        result.add(metaDataPoint117With_M118_V121_V123);


        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_then_unreliable_false2___() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(virtualDataPoint123);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.add(virtualDataPoint122);
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);


        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    ///


    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoints(virtualDataPointsWith_121_122_123);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint112With_M111_V121);
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
    public void when_setUnreliableDataPoints_with_virtual_points_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoints(virtualDataPointsWith_121_122_123);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoints(metaDataPointsWith_111);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_meta_points_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoints(metaDataPointsWith_111);

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
    public void when_setUnreliableDataPoints_with_meta_points_with_context_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoints(metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContextWith_112_116_117_118_119_120) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_meta_points_with_context_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoints(metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(virtualDataPoint121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.addAll(metaDataPointsWithContextWith_112_116_117_118_119_120);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(virtualDataPoint121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint122);
        result.add(virtualDataPoint123);
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_then_unreliable_true2() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(virtualDataPoint122);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint122);
        result.add(metaDataPoint116With_M117_V121);
        result.add(metaDataPoint117With_M118_V121_V123);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_then_unreliable_false2() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(virtualDataPoint122);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(virtualDataPoint121);
        result.add(metaDataPoint112With_M111_V121);
        result.add(metaDataPoint111);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(metaDataPoint111);

        //then:
        List<DataPointRT> result = new ArrayList<>();
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(metaDataPoint111);

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
    public void when_setUnreliableDataPoint_with_meta_point_with_context_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(metaDataPoint116With_M117_V121);

        //then:
        Assert.assertEquals(true, metaDataPoint116With_M117_V121.isUnreliable());

    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_with_context_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(metaDataPoint116With_M117_V121);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);
        result.add(metaDataPoint117With_M118_V121_V123);
        result.add(metaDataPoint118With_M119);
        result.add(metaDataPoint119With_M120_V122);
        result.add(metaDataPoint120With_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_from_context_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(metaDataPoint120With_V121);

        //then:
        List<DataPointRT> result = new ArrayList<>();
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
    public void when_setUnreliableDataPoint_with_meta_point_from_context_meta_points_then_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //when:
        setUnreliableDataPoint(metaDataPoint120With_V121);

        //then:
        List<DataPointRT> result = new ArrayList<>(virtualDataPointsWith_121_122_123);
        result.add(metaDataPoint111);
        result.add(metaDataPoint112With_M111_V121);

        for(DataPointRT dataPoint: result) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliable_for_all_then_unreliable_true1() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

        //when:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliable_for_all_then_unreliable_true2() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

        //when:
        setUnreliable(metaDataPointsWith_111, virtualDataPointsWith_121_122_123, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliable_for_all_then_unreliable_true3() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

        //when:
        setUnreliable(metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120, virtualDataPointsWith_121_122_123);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliable_for_all_then_unreliable_true4() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

        //when:
        setUnreliable(metaDataPointsWithContextWith_112_116_117_118_119_120, virtualDataPointsWith_121_122_123, metaDataPointsWith_111);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliable_for_all_then_unreliable_true5() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

        //when:
        setUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWithContextWith_112_116_117_118_119_120, metaDataPointsWith_111);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliable_for_all_then_unreliable_true6() {

        //given:
        resetUnreliable(virtualDataPointsWith_121_122_123, metaDataPointsWith_111, metaDataPointsWithContextWith_112_116_117_118_119_120);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }

        //when:
        setUnreliable(metaDataPointsWithContextWith_112_116_117_118_119_120, metaDataPointsWith_111, virtualDataPointsWith_121_122_123);

        //then:
        for(DataPointRT dataPoint: allDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }
}