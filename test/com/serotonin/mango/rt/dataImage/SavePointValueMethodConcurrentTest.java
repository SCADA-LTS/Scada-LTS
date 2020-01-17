package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import utils.SetPointSourceTestImpl;
import utils.concurrent.TestConcurrentUtil;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(value = Parameterized.class)
public class SavePointValueMethodConcurrentTest {

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

    public SavePointValueMethodConcurrentTest(boolean logValue, boolean async, SetPointSource source) {

        this.logValue = logValue;
        this.async = async;
        this.source = source;

    }

    private static final int NUMBER_OF_LAUNCHES_SIMULTANEOUSLY = 4;
    private static final int dataPointId = 123;

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
    public void test_savePointValue_then_keep_limit() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        TestConcurrentUtil.consumer(NUMBER_OF_LAUNCHES_SIMULTANEOUSLY, this::savePointValue, pointValueTimeLastSaved);

        //and:
        int size = pointValueCacheSubject.getCacheContents().size();

        //then:
        assertEquals(defaultSize, size);

    }

    @Test
    public void test_savePointValue_if_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        TestConcurrentUtil.consumer(NUMBER_OF_LAUNCHES_SIMULTANEOUSLY, this::savePointValue, pointValueTimeLastSaved);

        //then:
        verify(dao, times(logValue && async ? NUMBER_OF_LAUNCHES_SIMULTANEOUSLY : 0)).savePointValueAsync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));
    }

    @Test
    public void test_savePointValue_if_non_async() {

        //given:
        int defaultSize = 3;
        pointValueCacheSubject = new PointValueCache(dataPointId, defaultSize, dao);

        //when:
        TestConcurrentUtil.consumer(NUMBER_OF_LAUNCHES_SIMULTANEOUSLY, this::savePointValue, pointValueTimeLastSaved);

        //then:
        verify(dao, times(logValue && !async ? NUMBER_OF_LAUNCHES_SIMULTANEOUSLY : 0)).savePointValueSync(eq(dataPointId), eq(pointValueTimeLastSaved), eq(source));

    }

    private void savePointValue(PointValueTime point) {
        pointValueCacheSubject.savePointValueIntoDaoAndCacheUpdate(point, source, logValue, async);
    }

}
