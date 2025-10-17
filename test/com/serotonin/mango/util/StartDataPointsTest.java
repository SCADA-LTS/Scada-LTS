package com.serotonin.mango.util;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class, DAO.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})

public class StartDataPointsTest extends AbstractStartStopDataPointsUtilsTest {

    @Test
    public void when_startPoints_for_three_points_enabled_then_three_points_running() {

        //given:
        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertEquals(3, runningDataPoints.size());
    }

    @Test
    public void when_startPoints_for_three_meta_points_enabled_then_running_three_points() {

        //given:
        getDataPoint1().setPointLocator(getMetaPointLocator1());
        getDataPoint2().setPointLocator(getMetaPointLocator2());
        getDataPoint3().setPointLocator(getMetaPointLocator3());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertEquals(3, runningDataPoints.size());
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint1().getId()));
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint2().getId()));
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint3().getId()));
    }

    @Test
    public void when_startPoints_for_meta_point_with_context_then_running_point() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_startPoints_for_meta_point_with_context_with_one_point_enabled_then_running_point() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_startPoints_for_meta_point_disabled_then_not_running_point() {

        //given:
        getDataPoint1().setPointLocator(getMetaPointLocator1());
        getDataPoint1().setEnabled(false);

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_startPoints_for_point_disabled_then_not_running_point() {

        //given:
        getDataPoint1().setEnabled(false);

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_startPoints_for_meta_point_with_context_with_two_points_enabled_then_running_point() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, ""), new IntValuePair(3, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint1().getId()));
    }

    @Test
    public void when_startPoints_for_two_meta_points_with_context_with_same_other_point_then_running_two_meta_points() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        getMetaPointLocator2().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint2().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());

        //then:
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint1().getId()));
        Assert.assertNotEquals(null, runningDataPoints.get(getDataPoint2().getId()));
    }

    @Test(expected = IllegalStateException.class)
    public void when_startPoints_for_meta_point_with_context_with_point_disabled_then_exception() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(4, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());
    }

    @Test(expected = IllegalStateException.class)
    public void when_startPoints_with_context_meta_point_not_exists_then_exception() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(23, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());
    }

    @Test(expected = IllegalStateException.class)
    public void when_startPoints_with_context_dependency_to_self_then_exception() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(1, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());
    }

    @Test(expected = IllegalStateException.class)
    public void when_startPoints_with_context_cyclical_dependency_two_points_then_exception() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        getMetaPointLocator3().setContext(List.of(new IntValuePair(1, "")));
        getDataPoint3().setPointLocator(getMetaPointLocator3());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());
    }

    @Test(expected = IllegalStateException.class)
    public void when_startPoints_with_context_cyclical_dependency_three_points_then_exception() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        getMetaPointLocator2().setContext(List.of(new IntValuePair(1, "")));
        getDataPoint2().setPointLocator(getMetaPointLocator2());

        getMetaPointLocator3().setContext(List.of(new IntValuePair(2, "")));
        getDataPoint3().setPointLocator(getMetaPointLocator3());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());
    }


    @Test(expected = IllegalStateException.class)
    public void when_startPoints_with_context_cyclical_dependency_five_points_then_exception() {

        //given:
        getMetaPointLocator1().setContext(List.of(new IntValuePair(2, "")));
        getDataPoint1().setPointLocator(getMetaPointLocator1());

        getMetaPointLocator2().setContext(List.of(new IntValuePair(3, "")));
        getDataPoint2().setPointLocator(getMetaPointLocator2());

        getMetaPointLocator3().setContext(List.of(new IntValuePair(4, "")));
        getDataPoint3().setPointLocator(getMetaPointLocator3());

        getMetaPointLocator4().setContext(List.of(new IntValuePair(5, "")));
        getDataPoint4().setPointLocator(getMetaPointLocator4());

        getMetaPointLocator5().setContext(List.of(new IntValuePair(1, "")));
        getDataPoint5().setPointLocator(getMetaPointLocator5());

        Map<Integer, DataPointRT> runningDataPoints = new HashMap<>();

        //when:
        StartStopDataPointsUtils.startPoints(getDataPointServiceMock(), startPoints(runningDataPoints), runningDataPoints::get,
                a -> getDataSourceRtMock());
    }

}