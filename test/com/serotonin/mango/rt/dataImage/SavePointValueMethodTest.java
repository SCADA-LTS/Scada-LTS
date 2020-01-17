package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import utils.SetPointSourceTestImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(value = Parameterized.class)
public class SavePointValueMethodTest {

    @Parameterized.Parameters( name = "{index}: args: logValue: {0}, async: {1}, source {2}" )
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        true, true, new SetPointSourceTestImpl()
                },
                {
                        true, false, new SetPointSourceTestImpl()
                },
                {
                        false, true, new SetPointSourceTestImpl()
                },
                {
                        false, false, new SetPointSourceTestImpl()
                }
        });
    }

    private boolean logValue;
    private boolean async;
    private SetPointSource source;

    public SavePointValueMethodTest(boolean logValue, boolean async, SetPointSource source) {

        this.logValue = logValue;
        this.async = async;
        this.source = source;

    }

    private static int dataPointId = 123;

    private PointValueTime pointValueTimeFirstSaved;
    private PointValueTime pointValueTimeSecondSaved;
    private PointValueTime pointValueTimeThirdSaved;
    private PointValueTime pointValueTimeFourthSaved;
    private PointValueTime pointValueTimeLastSaved;

    private IPointValueDao dao;
    private PointValueCache pointValueCacheSubject;


    @Before
    public void setup() {

        pointValueTimeFirstSaved = new PointValueTime(MangoValue.stringToValue("1", 3), System.currentTimeMillis());
        pointValueTimeSecondSaved = new PointValueTime(MangoValue.stringToValue("2", 3), System.currentTimeMillis());
        pointValueTimeThirdSaved = new PointValueTime(MangoValue.stringToValue("3", 3), System.currentTimeMillis());
        pointValueTimeFourthSaved = new PointValueTime(MangoValue.stringToValue("4", 3), System.currentTimeMillis());
        pointValueTimeLastSaved = new PointValueTime(MangoValue.stringToValue("5", 3), System.currentTimeMillis());

        dao = Mockito.mock(IPointValueDao.class);

        when(dao.savePointValueSync(eq(dataPointId),eq(pointValueTimeLastSaved),eq(source)))
                .thenReturn(pointValueTimeLastSaved);

        when(dao.savePointValueSync(eq(dataPointId),eq(pointValueTimeFourthSaved),eq(source)))
                .thenReturn(pointValueTimeFourthSaved);

        when(dao.savePointValueSync(eq(dataPointId),eq(pointValueTimeThirdSaved),eq(source)))
                .thenReturn(pointValueTimeThirdSaved);

        when(dao.savePointValueSync(eq(dataPointId),eq(pointValueTimeSecondSaved),eq(source)))
                .thenReturn(pointValueTimeSecondSaved);

        when(dao.savePointValueSync(eq(dataPointId),eq(pointValueTimeFirstSaved),eq(source)))
                .thenReturn(pointValueTimeFirstSaved);

        when(dao.getLatestPointValue(eq(dataPointId))).thenReturn(pointValueTimeLastSaved);

    }

    @Test
    public void test_savePointValue_if_saved_number_points_equals_defaultSize_then_keep_limit() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        int size = pointValueCacheSubject.getCacheContents().size();

        //then:
        assertEquals(defaultSize, size);

    }

    @Test
    public void test_savePointValue_if_saved_number_points_equals_defaultSize_then_keep_last_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeSecondSaved));
        assertTrue(points.contains(pointValueTimeFirstSaved));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_equals_defaultSize_then_Last_In_First_Out() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeSecondSaved, points.get(1));
        assertEquals(pointValueTimeFirstSaved, points.get(2));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_equals_defaultSize_and_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //then:
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeFirstSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeSecondSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_equals_defaultSize_and_non_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //then:
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeFirstSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeSecondSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_greater_than_defaultSize_then_keep_limit() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThirdSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourthSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        int size = pointValueCacheSubject.getCacheContents().size();

        //then:
        assertEquals(defaultSize, size);

    }

    @Test
    public void test_savePointValue_if_saved_number_points_greater_than_defaultSize_then_keep_last_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThirdSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourthSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFourthSaved));
        assertTrue(points.contains(pointValueTimeThirdSaved));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_greater_than_defaultSize_then_not_keep_first_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThirdSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourthSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertFalse(points.contains(pointValueTimeFirstSaved));
        assertFalse(points.contains(pointValueTimeSecondSaved));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_greater_than_defaultSize_then_Last_In_First_Out() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThirdSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourthSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFourthSaved, points.get(1));
        assertEquals(pointValueTimeThirdSaved, points.get(2));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_greater_than_defaultSize_and_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThirdSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourthSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //then:
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeFirstSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeSecondSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeThirdSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeFourthSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_greater_than_defaultSize_and_non_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecondSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThirdSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourthSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //then:
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeFirstSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeSecondSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeThirdSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeFourthSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }

    @Test
    public void test_savePointValue_if_saved_2_points_less_than_defaultSize_then_cacheSize_2() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        int size = pointValueCacheSubject.getCacheContents().size();

        //then:
        assertEquals(2, size);

    }

    @Test
    public void test_savePointValue_if_saved_number_points_less_than_defaultSize_then_keep_last_points() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertTrue(points.contains(pointValueTimeLastSaved));
        assertTrue(points.contains(pointValueTimeFirstSaved));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_less_than_defaultSize_then_Last_In_First_Out() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //and:
        List<PointValueTime> points = pointValueCacheSubject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLastSaved, points.get(0));
        assertEquals(pointValueTimeFirstSaved, points.get(1));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_less_than_defaultSize_and_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //then:
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeFirstSaved), eq(source));
        verify(dao, times(logValue && async ? 1 : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }

    @Test
    public void test_savePointValue_if_saved_number_points_less_than_defaultSize_and_non_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirstSaved, source, logValue, async);
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLastSaved, source, logValue, async);

        //then:
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeFirstSaved), eq(source));
        verify(dao, times(logValue && !async ? 1 : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }
}
