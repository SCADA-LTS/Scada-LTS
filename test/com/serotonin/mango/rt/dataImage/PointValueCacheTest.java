package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import concurrent.ConcurrentTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.cache.PointValueCache;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PointValueCacheTest {

    private PointValueCache subject;

    @Before
    public void setup() {
        subject = new PointValueCache();
    }

    @Test
    public void test_if_last_in_first_out_if_exceeds_maxSize_1() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTime4 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTime5 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        subject.setMaxSize(1);

        //when:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime4, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime5, null, false, false);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(cachedObjects.get(0), pointValueTime5);
    }

    @Test
    public void test_if_keep_limit_for_maxSize_1() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTime4 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTime5 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        int sizeExpected = 1;

        //when:
        subject.setMaxSize(sizeExpected);

        //and:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime4, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime5, null, false, false);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(sizeExpected, result);
    }

    @Test
    public void test_if_not_delete_last_objects_if_exceeds_maxSize_1() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTime4 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTime5 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        int size = 1;

        //when:
        subject.setMaxSize(size);

        //and:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime4, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime5, null, false, false);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertTrue(cachedObjects.contains(pointValueTime5));
    }

    @Test
    public void test_if_all_points_have_been_added_maxSize_3() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        subject.setMaxSize(3);

        //when:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertTrue(cachedObjects.contains(pointValueTime));
        assertTrue(cachedObjects.contains(pointValueTime2));
        assertTrue(cachedObjects.contains(pointValueTime3));
    }

    @Test
    public void test_if_last_in_first_out_maxSize_3() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        subject.setMaxSize(3);

        //when:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(cachedObjects.get(0), pointValueTime3);
        assertEquals(cachedObjects.get(1), pointValueTime2);
        assertEquals(cachedObjects.get(2), pointValueTime);
    }

    @Test
    public void test_if_last_in_first_out_if_exceeds_maxSize_3() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTime4 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTime5 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        subject.setMaxSize(3);

        //when:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime4, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime5, null, false, false);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertEquals(cachedObjects.get(0), pointValueTime5);
        assertEquals(cachedObjects.get(1), pointValueTime4);
        assertEquals(cachedObjects.get(2), pointValueTime3);
    }

    @Test
    public void test_if_keep_limit_for_maxSize_3() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTime4 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTime5 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        int sizeExpected = 3;

        //when:
        subject.setMaxSize(sizeExpected);

        //and:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime4, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime5, null, false, false);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(sizeExpected, result);
    }

    @Test
    public void test_if_not_delete_last_objects_if_exceeds_maxSize_3() {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTime3 = new PointValueTime(MangoValue.stringToValue(String.valueOf(3), 3), 1);
        PointValueTime pointValueTime4 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        PointValueTime pointValueTime5 = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);
        int size = 3;

        //when:
        subject.setMaxSize(size);

        //and:
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime2, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime3, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime4, null, false, false);
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime5, null, false, false);

        //and:
        List<PointValueTime> cachedObjects = subject.getCacheContents();

        //then:
        assertTrue(cachedObjects.contains(pointValueTime3));
        assertTrue(cachedObjects.contains(pointValueTime4));
        assertTrue(cachedObjects.contains(pointValueTime5));
    }

    @Test
    public void test_if_keep_limit_for_maxSize_3_concurrent() throws InterruptedException {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        int size = 3;

        //when:
        subject.setMaxSize(size);

        //and:
        ConcurrentTestUtil.consumer(10000, this::savePointValue, pointValueTime);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(size, result);
    }

    void savePointValue(PointValueTime pointValueTime) {
        subject.savePointValueIntoCacheAndIflogValueIntoDbAsyncOrSync(pointValueTime, null, false, false);
    }

}