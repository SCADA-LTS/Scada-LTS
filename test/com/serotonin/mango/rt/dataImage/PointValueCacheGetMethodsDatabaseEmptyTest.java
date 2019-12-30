package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.IPointValueDao;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PointValueCacheGetMethodsDatabaseEmptyTest {

    private static IPointValueDao dao;
    private static int dataPointId;
    private PointValueCache pointValueCacheSubject;

    @BeforeClass
    public static void setup() {
        dataPointId = 1;
        dao = Mockito.mock(IPointValueDao.class);
        when(dao.getLatestPointValues(eq(dataPointId), anyInt())).thenReturn(Collections.emptyList());
        when(dao.getLatestPointValue(eq(dataPointId))).thenReturn(null);
    }

    @Test
    public void test_getLatestPointValues_if_limit_equals_defaultSize_then_empty() {

        //given:
        int limit = 3;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(0, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_limit_greater_than_defaultSize_then_empty() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(0, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_limit_less_than_defaultSize_then_empty() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(0, points.size());
    }

    @Test
    public void test_getLatestPointValue() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        PointValueTime point = pointValueCacheSubject.getLatestPointValue();

        //then:
        assertNull(point);
    }

    @Test
    public void test_getCacheContents_then_empty() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(0, points.size());

    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_equals_defaultSize_then_empty() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(0, points.size());
    }


    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_greater_than_defaultSize_then_empty() {

        //given:
        int limit = 4;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(0, points.size());
    }

    @Test
    public void test_getLatestPointValues_if_invoke_reset_and_limit_less_than_defaultSize_then_empty() {

        //given:
        int limit = 2;
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getLatestPointValues(limit);

        //then:
        assertEquals(0, points.size());
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
        assertNull(point);
    }

    @Test
    public void test_getCacheContents_if_invoke_reset_then_empty() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.reset();

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(0, points.size());

    }

}
