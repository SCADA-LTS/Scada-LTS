package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.PointValueDaoTestImpl;
import utils.SetPointSourceTestImpl;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(value = Parameterized.class)
public class PointValueCacheParametrizedTest {

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

    public PointValueCacheParametrizedTest(boolean logValue, boolean async, SetPointSource source) {
        this.logValue = logValue;
        this.async = async;
        this.source = source;
    }

    private PointValueCache subject;
    private int dataPointId = 5;

    @Test
    public void test_getCacheContents_check_without_saved_point_defaultSize_1() {

        //given:
        int expectedSize = 0;
        subject = new PointValueCache(dataPointId, 1, new PointValueDaoTestImpl());

        //when:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_getLatestPointValue_check_without_saved_point_defaultSize_1() {

        //given:
        subject = new PointValueCache(dataPointId, 1, new PointValueDaoTestImpl());

        //when:
        PointValueTime result = subject.getLatestPointValue();

        //then:
        assertNull(result);
    }


    @Test
    public void test_getLatestPointValues_check_without_saved_point_defaultSize_1() {

        //given:
        subject = new PointValueCache(dataPointId, 1, new PointValueDaoTestImpl());

        //when:
        List<PointValueTime> result = subject.getLatestPointValues(1);

        //then:
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void test_reset_check_how_reset_works_if_not_save_any_point_defaultSize_1() {

        //given:
        subject = new PointValueCache(dataPointId, 1, new PointValueDaoTestImpl());

        //when:
        subject.reset();

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(0, result);
    }

    @Test
    public void test_reset_check_how_reset_works_if_not_save_any_point_defaultSize_2() {

        //given:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.reset();

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(0, result);
    }

    @Test
    public void test_savePointValue_check_keep_max_value_if_exceed_defaultSize_1() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 1;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_savePointValue_check_keep_max_value_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 2;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_savePointValue_check_keep_all_points_if_not_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getCacheContents();

        //then:
        assertTrue(result.contains(pointValueTimeFirst));
        assertTrue(result.contains(pointValueTimeLast));
    }

    @Test
    public void test_savePointValue_check_keep_last_points_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getCacheContents();

        //then:
        assertTrue(result.contains(pointValueTimeLast));
        assertTrue(result.contains(pointValueTimeSecond));
    }

    @Test
    public void test_savePointValue_check_not_keep_first_point_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getCacheContents();

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
    }

    @Test
    public void test_savePointValue_check_Last_In_First_Out_sequences_if_exceed_defaultSize_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId,3, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, cachedObjects.get(0));
        assertEquals(pointValueTimeThird, cachedObjects.get(1));
        assertEquals(pointValueTimeSecond, cachedObjects.get(2));
    }

    @Test
    public void test_savePointValue_check_Last_In_First_Out_sequences_if_not_exceed_defaultSize_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId,3, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, cachedObjects.get(0));
        assertEquals(pointValueTimeSecond, cachedObjects.get(1));
        assertEquals(pointValueTimeFirst, cachedObjects.get(2));
    }

    @Test
    public void test_getLatestPointValues_check_Last_In_First_Out_sequences_if_not_exceed_defaultSize_and_limit_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);

        //and:
        int defaultSize = 3;
        subject = new PointValueCache(dataPointId,defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValues = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, latestPointValues.get(0));
        assertEquals(pointValueTimeSecond, latestPointValues.get(1));
        assertEquals(pointValueTimeFirst, latestPointValues.get(2));
    }


    @Test
    public void test_getLatestPointValues_check_keep_max_value_if_exceed_defaultSize_and_limit_1() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 1;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        int result = subject.getLatestPointValues(expectedSize).size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_getLatestPointValues_check_keep_max_value_if_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 2;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        int result = subject.getLatestPointValues(expectedSize).size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_getLatestPointValues_check_keep_all_points_if_not_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(defaultSize);

        //then:
        assertTrue(result.contains(pointValueTimeFirst));
        assertTrue(result.contains(pointValueTimeLast));
    }

    @Test
    public void test_getLatestPointValues_check_keep_last_points_if_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(defaultSize);

        //then:
        assertTrue(result.contains(pointValueTimeLast));
        assertTrue(result.contains(pointValueTimeSecond));
    }

    @Test
    public void test_getLatestPointValues_check_not_keep_first_point_if_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(defaultSize);

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
    }

    @Test
    public void test_getLatestPointValues_check_not_keep_first_point_if_exceed_defaultSize_greater_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        int limit = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
        assertFalse(result.contains(pointValueTimeSecond));
        assertTrue(result.contains(pointValueTimeThird));
        assertTrue(result.contains(pointValueTimeLast));
    }

    @Test
    public void test_getLatestPointValues_check_not_keep_first_point_if_not_exceed_defaultSize_greater_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        int limit = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
        assertTrue(result.contains(pointValueTimeSecond));
        assertTrue(result.contains(pointValueTimeLast));
    }


    @Test
    public void test_getLatestPointValues_check_not_keep_first_point_if_exceed_defaultSize_less_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        int limit = 3;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertEquals(logValue, result.contains(pointValueTimeFirst));
        assertEquals(logValue, result.contains(pointValueTimeSecond));
        assertEquals(logValue, result.contains(pointValueTimeLast));
    }

    @Test
    public void test_getLatestPointValues_check_keep_first_point_if_not_exceed_defaultSize_less_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);

        //and:
        int defaultSize = 2;
        int limit = 3;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertEquals(logValue, result.contains(pointValueTimeFirst));
        assertEquals(logValue, result.contains(pointValueTimeLast));
    }

    @Test
    public void test_sgetLatestPointValues_check_Last_In_First_Out_sequences_if_exceed_defaultSize_and_limit_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        subject = new PointValueCache(dataPointId,defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> cachedObjects = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, cachedObjects.get(0));
        assertEquals(pointValueTimeThird, cachedObjects.get(1));
        assertEquals(pointValueTimeSecond, cachedObjects.get(2));
    }

    @Test
    public void test_getLatestPointValue_check_keep_point_if_not_exceed_defaultSize() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);

        //and:
        int defaultSize = 1;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeFirst, latestPointValue);
    }

    @Test
    public void test_getLatestPointValue_check_keep_point_if_not_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue);
    }

    @Test
    public void test_getLatestPointValue_check_keep_last_point_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);

        //and:
        int defaultSize = 1;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue);
    }

    @Test
    public void test_getLatestPointValue_check_keep_last_point_if_exceed_defaultSize_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue);
    }

    @Test
    public void test_reset_if_savePointValue_check_keep_max_value_if_exceed_defaultSize_1() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 1;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.reset();

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_reset_if_savePointValue_check_keep_max_value_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 2;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_reset_if_savePointValue_check_keep_all_points_if_not_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getCacheContents();

        //then:
        assertTrue(result.contains(pointValueTimeFirst));
        assertTrue(result.contains(pointValueTimeLast));
    }

    @Test
    public void test_reset_if_savePointValue_check_keep_last_points_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getCacheContents();

        //then:
        assertTrue(result.contains(pointValueTimeLast));
        assertTrue(result.contains(pointValueTimeSecond));
    }

    @Test
    public void test_reset_if_savePointValue_check_not_keep_first_point_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId, 2, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getCacheContents();

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
    }

    @Test
    public void test_reset_if_savePointValue_check_Last_In_First_Out_sequences_if_exceed_defaultSize_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId,3, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, cachedObjects.get(0));
        assertEquals(pointValueTimeThird, cachedObjects.get(1));
        assertEquals(pointValueTimeSecond, cachedObjects.get(2));
    }

    @Test
    public void test_reset_if_savePointValue_check_Last_In_First_Out_sequences_if_not_exceed_defaultSize_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);

        //and:
        subject = new PointValueCache(dataPointId,3, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, cachedObjects.get(0));
        assertEquals(pointValueTimeSecond, cachedObjects.get(1));
        assertEquals(pointValueTimeFirst, cachedObjects.get(2));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_Last_In_First_Out_sequences_if_not_exceed_defaultSize_and_limit_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);

        //and:
        int defaultSize = 3;
        subject = new PointValueCache(dataPointId,defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> latestPointValues = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, latestPointValues.get(0));
        assertEquals(pointValueTimeSecond, latestPointValues.get(1));
        assertEquals(pointValueTimeFirst, latestPointValues.get(2));
    }


    @Test
    public void test_reset_if_getLatestPointValues_check_keep_max_value_if_exceed_defaultSize_and_limit_1() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 1;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        int result = subject.getLatestPointValues(expectedSize).size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_keep_max_value_if_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int expectedSize = 2;
        subject = new PointValueCache(dataPointId, expectedSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        int result = subject.getLatestPointValues(expectedSize).size();

        //then:
        assertEquals(expectedSize, result);
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_keep_all_points_if_not_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(defaultSize);

        //then:
        assertTrue(result.contains(pointValueTimeFirst));
        assertTrue(result.contains(pointValueTimeLast));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_keep_last_points_if_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(defaultSize);

        //then:
        assertTrue(result.contains(pointValueTimeLast));
        assertTrue(result.contains(pointValueTimeSecond));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_not_keep_first_point_if_exceed_defaultSize_and_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(defaultSize);

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_not_keep_first_point_if_exceed_defaultSize_greater_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        int limit = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_not_keep_first_point_if_not_exceed_defaultSize_greater_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        int limit = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertFalse(result.contains(pointValueTimeFirst));
    }


    @Test
    public void test_reset_if_getLatestPointValues_check_keep_first_point_if_exceed_defaultSize_less_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        int limit = 3;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertEquals(logValue, result.contains(pointValueTimeLast));
        assertEquals(logValue, result.contains(pointValueTimeSecond));
        assertEquals(logValue, result.contains(pointValueTimeFirst));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_keep_first_point_if_not_exceed_defaultSize_less_than_limit_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);

        //and:
        int defaultSize = 2;
        int limit = 3;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> result = subject.getLatestPointValues(limit);

        //then:
        assertEquals(logValue, result.contains(pointValueTimeFirst));
        assertEquals(logValue, result.contains(pointValueTimeLast));
    }

    @Test
    public void test_reset_if_sgetLatestPointValues_check_Last_In_First_Out_sequences_if_exceed_defaultSize_and_limit_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        subject = new PointValueCache(dataPointId,defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        List<PointValueTime> cachedObjects = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, cachedObjects.get(0));
        assertEquals(pointValueTimeThird, cachedObjects.get(1));
        assertEquals(pointValueTimeSecond, cachedObjects.get(2));
    }

    @Test
    public void test_reset_if_getLatestPointValue_check_keep_point_if_not_exceed_defaultSize() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);

        //and:
        int defaultSize = 1;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);

        //and:
        subject.reset();

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeFirst, latestPointValue);
    }

    @Test
    public void test_reset_if_getLatestPointValue_check_keep_point_if_not_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue);
    }

    @Test
    public void test_reset_if_getLatestPointValue_check_keep_last_point_if_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);

        //and:
        int defaultSize = 1;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue);
    }

    @Test
    public void test_reset_if_getLatestPointValue_check_keep_last_point_if_exceed_defaultSize_3() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 3;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        subject.reset();

        //and:
        PointValueTime latestPointValue = subject.getLatestPointValue();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue);
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_how_reset_works_if_before_and_after_invoke_reset_exceed_defaultSize_2 () {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFourth = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFifth = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFifth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeFifth, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_how_reset_works_if_before_and_after_invoke_reset_not_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeThird, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_how_reset_works_if_before_not_exceed_defaultSize_and_after_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFourth = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeFourth, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_how_reset_works_if_before_exceed_defaultSize_and_after_not_exceed_defaultSize_2() {
        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFourth = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getLatestPointValues(defaultSize);

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeFourth, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getCacheContents_check_how_reset_works_if_before_and_after_invoke_reset_exceed_defaultSize_2 () {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFourth = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFifth = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFifth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeFifth, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getCacheContents_check_how_reset_works_if_before_and_after_invoke_reset_not_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeThird, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getCacheContents_check_how_reset_works_if_before_not_exceed_defaultSize_and_after_exceed_defaultSize_2() {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFourth = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeFourth, latestPointValue.get(1));
    }

    @Test
    public void test_reset_if_getCacheContents_check_how_reset_works_if_before_exceed_defaultSize_and_after_not_exceed_defaultSize_2() {
        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeThird = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeFourth = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int defaultSize = 2;
        subject = new PointValueCache(dataPointId, defaultSize, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeThird, source, logValue, async);

        //and:
        subject.reset();

        //and:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFourth, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        //and:
        List<PointValueTime> latestPointValue = subject.getCacheContents();

        //then:
        assertEquals(pointValueTimeLast, latestPointValue.get(0));
        assertEquals(pointValueTimeFourth, latestPointValue.get(1));
    }
}
