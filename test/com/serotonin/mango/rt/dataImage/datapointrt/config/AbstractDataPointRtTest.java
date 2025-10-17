package com.serotonin.mango.rt.dataImage.datapointrt.config;


import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.util.ObjectUtils;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public abstract class AbstractDataPointRtTest extends ConfigDataPointRtTest {

    public AbstractDataPointRtTest(DataPointSyncMode sync, Object value1, Object value2, Object value3, int dataTypeId, String dataType, String startValue) {
        super(sync, value1, value2, value3, dataTypeId, dataType, startValue);
    }

    private DataPointRT dataPointRT;

    @Before
    public void config() {
        dataPointRT = start();
    }

    @After
    public void clean() {
        clear();
    }

    @Test
    public void when_setPointValue() {

        //given:
        PointValueTime oldValue = getOldValue();
        PointValueTime newValue = getNewValue();
        PointValueTime newValue2 = getNewValue2();
        List<PointValueTime> pointValuesExpected = getPointValuesExpected();

        //when:
        dataPointRT.setPointValue(oldValue, null);
        dataPointRT.setPointValue(newValue, null);
        dataPointRT.setPointValue(newValue2, null);

        //and when:
        List<PointValueTime> pointValues = dataPointRT.getLatestPointValues(getDefaultCacheSize());

        //then:
        assertFalse("isEqual", ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue()));
        assertTrue("not isEqual", ObjectUtils.isEqual(newValue.getValue(), newValue.getValue()));
        assertEquals(pointValuesExpected, pointValues);
    }

    @Test
    public void when_setPointValue_by_User() {

        //given:
        PointValueTime oldValue = getOldValueWithUser();
        PointValueTime newValue = getNewValueWithUser();
        PointValueTime newValue2 = getNewValueWithUser2();
        List<PointValueTime> pointValuesExpected = getPointValuesWithUserExpected();

        //when:
        dataPointRT.setPointValue(oldValue, getUser());
        dataPointRT.setPointValue(newValue, getUser());
        dataPointRT.setPointValue(newValue2, getUser());

        //and when:
        List<PointValueTime> pointValues = dataPointRT.getLatestPointValues(getDefaultCacheSize());

        //then:
        assertFalse("isEqual", ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue()));
        assertTrue("not isEqual", ObjectUtils.isEqual(newValue.getValue(), newValue.getValue()));
        assertEquals(pointValuesExpected, pointValues);
    }

    @Test
    public void when_setPointValue_then_size() {

        //given:
        PointValueTime oldValue = getOldValue();
        PointValueTime newValue = getNewValue();
        PointValueTime newValue2 = getNewValue2();
        List<PointValueTime> pointValuesExpected = getPointValuesExpected();

        //when:
        dataPointRT.setPointValue(oldValue, null);
        dataPointRT.setPointValue(newValue, null);
        dataPointRT.setPointValue(newValue2, null);

        //and when:
        List<PointValueTime> pointValues = dataPointRT.getLatestPointValues(getDefaultCacheSize());

        //then:
        assertFalse("isEqual", ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue()));
        assertTrue("not isEqual", ObjectUtils.isEqual(newValue.getValue(), newValue.getValue()));
        assertEquals(pointValuesExpected.size(), pointValues.size());
    }

    @Test
    public void when_setPointValue_by_User_then_size() {

        //given:
        PointValueTime oldValue = getOldValueWithUser();
        PointValueTime newValue = getNewValueWithUser();
        PointValueTime newValue2 = getNewValueWithUser2();
        List<PointValueTime> pointValuesExpected = getPointValuesWithUserExpected();

        //when:
        dataPointRT.setPointValue(oldValue, getUser());
        dataPointRT.setPointValue(newValue, getUser());
        dataPointRT.setPointValue(newValue2, getUser());

        //and when:
        List<PointValueTime> pointValues = dataPointRT.getLatestPointValues(getDefaultCacheSize());

        //then:
        assertFalse("isEqual", ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue()));
        assertTrue("not isEqual", ObjectUtils.isEqual(newValue.getValue(), newValue.getValue()));
        assertEquals(pointValuesExpected.size(), pointValues.size());
    }

    @Test
    public void when_setPointValue_by_User_then_verify_create_times() {

        //given:
        PointValueTime oldValue = getOldValueWithUser();
        PointValueTime newValue = getNewValueWithUser();
        PointValueTime newValue2 = getNewValueWithUser2();
        List<PointValueTime> pointValuesExpected = getPointValuesWithUserDaoTestExpected();

        //when:
        dataPointRT.setPointValue(oldValue, getUser());
        dataPointRT.setPointValue(newValue, getUser());
        dataPointRT.setPointValue(newValue2, getUser());

        //then:
        verify(getPointValueDAOMock(), times(pointValuesExpected.size()))
                .create(eq(dataPointRT.getId()), eq(getDataTypeId()), anyDouble(), anyLong());

    }

    @Test
    public void when_setPointValue_then_applyBounds_times() {

        //given:
        PointValueTime oldValue = getOldValue();
        PointValueTime newValue = getNewValue();
        PointValueTime newValue2 = getNewValue2();
        List<PointValueTime> pointValuesExpected = getPointValuesDaoTestExpected();

        //when:
        dataPointRT.setPointValue(oldValue, null);
        dataPointRT.setPointValue(newValue, null);
        dataPointRT.setPointValue(newValue2, null);

        //then:
        verify(getPointValueDAOMock(), times(pointValuesExpected.size())).applyBounds(anyDouble());
    }
}
