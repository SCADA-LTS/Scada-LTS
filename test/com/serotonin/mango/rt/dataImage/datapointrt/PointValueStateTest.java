package com.serotonin.mango.rt.dataImage.datapointrt;

import com.serotonin.mango.rt.dataImage.PointValueState;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.*;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class PointValueStateTest {

    private final double delta = 0.00001;

    @Test(expected = IllegalArgumentException.class)
    public void when_newState_with_newValue_is_null_then_IllegalArgumentException() {

        //given:
        PointValueTime newValue = null;
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        PointValueState oldState = PointValueState.newState(new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis()),
                null, dataPointVO);

        //when:
        PointValueState.newState(newValue, oldState, dataPointVO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_newState_with_dataPointVO_is_null_then_IllegalArgumentException() {

        //given:
        PointValueTime newValue = new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        DataPointVO dataPointNull = null;
        PointValueState oldState = PointValueState.newState(new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis()), null, dataPointVO);

        //when:
        PointValueState.newState(newValue, oldState, dataPointNull);
    }

    @Test
    public void when_newState_with_oldState_is_null_then_newValue() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertEquals(expectedValue, result.getNewValue());
    }

    @Test
    public void when_newState_with_oldState_is_null_then_toleranceOrigin_no_zero() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertNotEquals(0.0, expectedValue.getDoubleValue(), delta);
        assertEquals(expectedValue.getDoubleValue(), result.getToleranceOrigin(), delta);
    }

    @Test
    public void when_newState_with_oldState_is_null_and_logging_all_then_toleranceOrigin_no_zero() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ALL);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertNotEquals(0.0, expectedValue.getDoubleValue(), delta);
        assertEquals(expectedValue.getDoubleValue(), result.getToleranceOrigin(), delta);
    }

    @Test
    public void when_newState_with_oldState_is_null_and_logging_interval_then_toleranceOrigin_no_zero() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue(1.0), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.INTERVAL);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertNotEquals(0.0, expectedValue.getDoubleValue(), delta);
        assertEquals(expectedValue.getDoubleValue(), result.getToleranceOrigin(), delta);
    }

    @Test
    public void when_newState_with_oldState_is_null_and_alphanumeric_then_toleranceOrigin_zero() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue("abc"), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertTrue(expectedValue.getValue() instanceof AlphanumericValue);
        assertEquals(0.0, result.getToleranceOrigin(), delta);
    }

    @Test
    public void when_newState_with_oldState_is_null_and_binary_then_toleranceOrigin_zero() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue(false), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertTrue(expectedValue.getValue() instanceof BinaryValue);
        assertEquals(0.0, result.getToleranceOrigin(), delta);
    }

    @Test
    public void when_newState_with_oldState_is_null_and_multistate_then_toleranceOrigin_zero() {

        //given:
        PointValueTime expectedValue = new PointValueTime(MangoValue.objectToValue(1), System.currentTimeMillis());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        PointValueState oldState = null;

        //when:
        PointValueState result = PointValueState.newState(expectedValue, oldState, dataPointVO);

        //then:
        assertTrue(expectedValue.getValue() instanceof MultistateValue);
        assertEquals(0.0, result.getToleranceOrigin(), delta);
    }
}