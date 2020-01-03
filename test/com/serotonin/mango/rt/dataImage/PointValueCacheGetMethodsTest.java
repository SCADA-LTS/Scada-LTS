package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PointValueCacheGetMethodsTest {

    private static PointValueTime pointValueTimeFirstSaved;
    private static PointValueTime pointValueTimeSecondSaved;
    private static PointValueTime pointValueTimeThirdSaved;
    private static PointValueTime pointValueTimeFourthSaved;
    private static PointValueTime pointValueTimeLastSaved;
    private static int dataPointId;
    private static IPointValueDao dao;

    private PointValueCache pointValueCacheSubject;

    @BeforeClass
    public static void setupGeneral() {
        pointValueTimeFirstSaved = new PointValueTime(MangoValue.stringToValue("1", 3), System.currentTimeMillis());
        pointValueTimeSecondSaved = new PointValueTime(MangoValue.stringToValue("2", 3), System.currentTimeMillis());
        pointValueTimeThirdSaved = new PointValueTime(MangoValue.stringToValue("3", 3), System.currentTimeMillis());
        pointValueTimeFourthSaved = new PointValueTime(MangoValue.stringToValue("4", 3), System.currentTimeMillis());
        pointValueTimeLastSaved = new PointValueTime(MangoValue.stringToValue("5", 3), System.currentTimeMillis());
        PointValueTime[] databaseContent = {pointValueTimeLastSaved, pointValueTimeFourthSaved,
                pointValueTimeThirdSaved, pointValueTimeSecondSaved, pointValueTimeFirstSaved};

        dataPointId = 1;
        dao = Mockito.mock(IPointValueDao.class);

        List<PointValueTime> points3 = Stream.of(databaseContent).limit(3).collect(Collectors.toList());
        when(dao.getLatestPointValues(eq(dataPointId), eq(3))).thenReturn(points3);

        List<PointValueTime> points4 = Stream.of(databaseContent).limit(4).collect(Collectors.toList());
        when(dao.getLatestPointValues(eq(dataPointId), eq(4))).thenReturn(points4);

        when(dao.getLatestPointValue(eq(dataPointId))).thenReturn(pointValueTimeLastSaved);
    }

    @Test
    public void test_getLatestPointValues_if_limit_equals_defaultSize_then_keep_limit() {

        //given:
        int limit = 3;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(limit, points.size());
    }
    
    @Test
    public void test_getLatestPointValues_if_limit_equals_defaultSize_then_keep_last_points() {

        //given:
        int limit = 3;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));
    }

    @Test
    public void test_getLatestPointValues_if_limit_equals_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 3;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValues_if_limit_equals_defaultSize_then_Last_In_First_Out() {

        //given:
        int limit = 3;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));
    }

    @Test
    public void test_getLatestPointValues_if_limit_greater_than_defaultSize_then_keep_limit() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(limit, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_limit_greater_than_defaultSize_then_keep_last_points() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));
        assertTrue(points.contains(pointValueTimeSecondSaved));
        
    }

    @Test
    public void test_getLatestPointValues_if_limit_greater_than_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValues_if_limit_greater_than_defaultSize_then_Last_In_First_Out() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));
        assertEquals(pointValueTimeSecondSaved, points.get(3));
    }

    @Test
    public void test_getLatestPointValues_if_limit_less_than_defaultSize_then_keep_limit() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(limit, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_limit_less_than_defaultSize_then_keep_last_points() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        
    }

    @Test
    public void test_getLatestPointValues_if_limit_less_than_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeThirdSaved));
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValues_if_limit_less_than_defaultSize_then_Last_In_First_Out() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
    }

    @Test
    public void test_getLatestPointValues_if_limit_1_less_than_defaultSize_then_keep_limit() {

        //given:
        int limit = 1;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(limit, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_limit_1_less_than_defaultSize_then_keep_last_points() {

        //given:
        int limit = 1;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));

    }

    @Test
    public void test_getLatestPointValues_if_limit_1_less_than_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 1;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeFourthSaved));
        assertFalse(points.contains(pointValueTimeThirdSaved));
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValue() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        PointValueTime point = pointValueCacheSubject.getLatestPointValue();

        //then:
        assertNotNull(point);
        assertEquals(pointValueTimeLastSaved, point);

    }

    @Test
    public void test_getCacheContents_then_keep_limit() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(defaultSize, points.size());

    }

    @Test
    public void test_getCacheContents_then_keep_last_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));
        
    }

    @Test
    public void test_getCacheContents_then_not_keep_first_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));
        
    }

    @Test
    public void test_getCacheContents_then_Last_In_First_Out() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));
        
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_equals_defaultSize_then_keep_limit() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(defaultSize, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_equals_defaultSize_then_keep_last_points() {

        //given:
        int limit = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, limit, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_equals_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, limit, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_equals_defaultSize_then_Last_In_First_Out() {

        //given:
        int limit = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, limit, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_greater_than_defaultSize_then_keep_limit() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(limit, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_greater_than_defaultSize_then_keep_last_points() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));
        assertTrue(points.contains(pointValueTimeSecondSaved));

    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_greater_than_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_greater_than_defaultSize_then_Last_In_First_Out() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));
        assertEquals(pointValueTimeSecondSaved, points.get(3));
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_less_than_defaultSize_then_keep_limit() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(limit, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_less_than_defaultSize_then_keep_last_points() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));

    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_less_than_defaultSize_then_not_keep_first_points() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertFalse(points.contains(pointValueTimeThirdSaved));
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_less_than_defaultSize_then_Last_In_First_Out() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
    }

    @Test
    public void test_getLatestPointValue_if_invoke_reset() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        PointValueTime point = pointValueCacheSubject.getLatestPointValue();

        //then:
        assertNotNull(point);
        assertEquals(pointValueTimeLastSaved, point);
    }

    @Test
    public void test_getCacheContents_if_invoke_reset_then_keep_limit() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(defaultSize, points.size());

    }

    @Test
    public void test_getCacheContents_if_invoke_reset_then_keep_last_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));

    }

    @Test
    public void test_getCacheContents_if_invoke_reset_then_not_keep_first_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertFalse(points.contains(pointValueTimeSecondSaved));
        assertFalse(points.contains(pointValueTimeFirstSaved));

    }

    @Test
    public void test_getCacheContents_if_invoke_reset_then_Last_In_First_Out() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));

    }

}
