package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scadalts.test.concurrent.TestConcurrent;
import org.junit.Test;
import utils.PointValueDaoTestImpl;
import utils.SetPointSourceTestImpl;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class PointValueCacheConcurrentTest {

    @Parameterized.Parameters
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

    public PointValueCacheConcurrentTest(boolean logValue, boolean async, SetPointSource source) {
        this.logValue = logValue;
        this.async = async;
        this.source = source;
    }

    private PointValueCache subject;
    private int dataPointId = 5;

    @Test
    public void test_savePointValue_check_keep_limit_if_exceed_defaultSize_3_concurrent() throws InterruptedException {

        //given:
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);

        //and:
        int sizeExpected = 3;
        subject = new PointValueCache(dataPointId, sizeExpected, new PointValueDaoTestImpl());

        //when:
        TestConcurrent.consumer(10000, this::savePointValue, pointValueTime);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(sizeExpected, result);
    }

    @Test
    public void test_reset_if_getCacheContents_check_keep_limit_if_exceed_defaultSize_3_concurrent() throws InterruptedException {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int sizeExpected = 3;
        subject = new PointValueCache(dataPointId, sizeExpected, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        TestConcurrent.supplierVoid(1000, subject::reset);

        //and:
        int result = subject.getCacheContents().size();

        //then:
        assertEquals(sizeExpected, result);
    }

    @Test
    public void test_reset_if_getLatestPointValues_check_keep_limit_if_exceed_defaultSize_3_concurrent() throws InterruptedException {

        //given:
        PointValueTime pointValueTimeFirst = new PointValueTime(MangoValue.stringToValue(String.valueOf(1), 3), 1);
        PointValueTime pointValueTimeSecond = new PointValueTime(MangoValue.stringToValue(String.valueOf(2), 3), 1);
        PointValueTime pointValueTimeLast = new PointValueTime(MangoValue.stringToValue(String.valueOf(4), 3), 1);

        //and:
        int sizeExpected = 3;
        subject = new PointValueCache(dataPointId, sizeExpected, new PointValueDaoTestImpl());

        //when:
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeFirst, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeSecond, source, logValue, async);
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTimeLast, source, logValue, async);

        TestConcurrent.supplierVoid(1000, subject::reset);

        //and:
        int result = subject.getLatestPointValues(sizeExpected).size();

        //then:
        assertEquals(sizeExpected, result);
    }

    void savePointValue(PointValueTime pointValueTime) {
        subject.savePointValueIntoDaoAndCacheUpdate(pointValueTime, source, logValue, async);
    }
}
