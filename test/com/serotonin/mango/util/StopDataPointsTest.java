package com.serotonin.mango.util;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.DAO;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.*;

import static utils.StartStopDataPointsTestUtils.startPoints;
import static utils.StartStopDataPointsTestUtils.stopPoints;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class, DAO.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class StopDataPointsTest extends AbstractStartStopDataPointsUtilsTest {

    @Test
    public void when_stopPoints_for_points_then_points_disabled() {

        //given:
        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(true, runningDataPoints.isEmpty());
        for(DataPointVO dataPointRT: getDataPoints()) {
            Assert.assertEquals(false, dataPointRT.isEnabled());
        }
    }


    @Test
    public void when_stopPoints_for_five_meta_points_then_five_points_disabled() {

        //given:
        getDataPoint1().setPointLocator(getMetaPointLocator1());
        getDataPoint1().setEnabled(true);
        getDataPoint2().setPointLocator(getMetaPointLocator2());
        getDataPoint2().setEnabled(true);
        getDataPoint3().setPointLocator(getMetaPointLocator3());
        getDataPoint3().setEnabled(true);
        getDataPoint4().setPointLocator(getMetaPointLocator4());
        getDataPoint4().setEnabled(true);
        getDataPoint5().setPointLocator(getMetaPointLocator5());
        getDataPoint5().setEnabled(true);

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(true, runningDataPoints.isEmpty());
        for(DataPointVO dataPointRT: getDataPoints()) {
            Assert.assertEquals(false, dataPointRT.isEnabled());
        }
    }


    @Test
    public void when_stopPoints_with_three_meta_points_then_three_points_disabled() {

        //given:
        getDataPoint1().setPointLocator(getMetaPointLocator1());
        getDataPoint2().setPointLocator(getMetaPointLocator2());
        getDataPoint3().setPointLocator(getMetaPointLocator3());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        for(DataPointVO dataPointRT: getDataPoints()) {
            Assert.assertEquals(false, dataPointRT.isEnabled());
        }
    }

    @Test
    public void when_stopPoints_for_meta_point_with_context_then_not_running_point() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_stopPoints_for_meta_point_with_context_with_one_point_enabled_then_not_running_point() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_stopPoints_for_meta_point_disabled_then_not_running_point() {

        //given:
        getDataPoint1().setPointLocator(getMetaPointLocator1());
        getDataPoint1().setEnabled(false);

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_stopPoints_for_point_disabled_then_not_running_point() {

        //given:
        getDataPoint1().setEnabled(false);

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_stopPoints_for_meta_point_with_context_with_two_points_enabled_then_not_running_point() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, ""), new IntValuePair(3, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_stopPoints_for_two_meta_points_with_context_with_same_other_point_then_not_running_two_meta_points() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        getMetaPointLocator2().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint2().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //when:
        StartStopDataPointsUtils.stopPoints(runningDataPoints.values(), stopPoints(runningDataPoints), runningDataPoints::get);

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint2().getId()));
    }
}