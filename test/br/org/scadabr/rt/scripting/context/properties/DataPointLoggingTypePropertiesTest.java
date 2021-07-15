package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.IntervalLoggingPeriodType;
import com.serotonin.mango.vo.IntervalLoggingType;
import com.serotonin.mango.vo.LoggingType;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class DataPointLoggingTypePropertiesTest {

    @Test
    public void when_defaultProperties_then_IntervalLoggingType_INSTANT() {

        //when:
        DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();

        //then:
        assertEquals(IntervalLoggingType.INSTANT, defaultProperties.getIntervalLoggingType());
    }

    @Test
    public void when_defaultProperties_then_LoggingType_ON_CHANGE() {
        //when:
        DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();

        //then:
        assertEquals(LoggingType.ON_CHANGE, defaultProperties.getLoggingType());
    }

    @Test
    public void when_defaultProperties_then_IntervalLoggingPeriodType_MINUTES() {
        //when:
        DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();

        //then:
        assertEquals(IntervalLoggingPeriodType.MINUTES, defaultProperties.getIntervalPeriodType());
    }

    @Test
    public void when_defaultProperties_then_tolerance_0() {
        //when:
        DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();

        //then:
        assertEquals( 0.0, defaultProperties.getTolerance(),0.001);
    }

    @Test
    public void when_defaultProperties_then_intervalPeriod_1() {
        //when:
        DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();

        //then:
        assertEquals( 15, defaultProperties.getIntervalPeriod());
    }

    @Test
    public void when_onChange_then_LoggingType_ON_CHANGE() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.onChange(0.333);

        //then:
        assertEquals(LoggingType.ON_CHANGE, result.getLoggingType());
    }

    @Test
    public void when_onChange_then_tolerance() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.onChange(0.333);

        //then:
        assertEquals(0.333, result.getTolerance(), 0.001);
    }

    @Test
    public void when_all_then_LoggingType_ALL() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.all();

        //then:
        assertEquals(LoggingType.ALL, result.getLoggingType());
    }

    @Test
    public void when_onTsChange_then_LoggingType_ON_TS_CHANGE() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.onTsChange();

        //then:
        assertEquals(LoggingType.ON_TS_CHANGE, result.getLoggingType());
    }

    @Test
    public void when_none_then_LoggingType_NONE() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.none();

        //then:
        assertEquals(LoggingType.NONE, result.getLoggingType());
    }

    @Test
    public void when_interval_then_LoggingType_INTERVAL() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS,
                12, IntervalLoggingType.AVERAGE);

        //then:
        assertEquals(LoggingType.INTERVAL, result.getLoggingType());
    }

    @Test
    public void when_interval_then_IntervalLoggingPeriodType_DAYS() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS,
                12, IntervalLoggingType.AVERAGE);

        //then:
        assertEquals(IntervalLoggingPeriodType.DAYS, result.getIntervalPeriodType());
    }

    @Test
    public void when_interval_then_IntervalLoggingType_AVERAGE() {

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS,
                12, IntervalLoggingType.AVERAGE);

        //then:
        assertEquals(IntervalLoggingType.AVERAGE, result.getIntervalLoggingType());
    }

    @Test
    public void when_byNativeObject_with_LoggingType_INTERVAL() {
        //given:
        DataPointLoggingTypeProperties expected = DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS,
                22, IntervalLoggingType.MINIMUM);

        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("intervalPeriodType"))).thenReturn("weeks");
        when(nativeObject.get(eq("intervalPeriod"))).thenReturn(22.0);
        when(nativeObject.get(eq("intervalLoggingType"))).thenReturn("minimum");
        when(nativeObject.get(eq("loggingType"))).thenReturn("interval");

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.byNativeObject(nativeObject);

        //then:
        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_byNativeObject_with_bad_intervalPeriodType_then_exception() {
        //given:
        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("intervalPeriodType"))).thenReturn("weeks2");
        when(nativeObject.get(eq("intervalPeriod"))).thenReturn(22.0);
        when(nativeObject.get(eq("intervalLoggingType"))).thenReturn("minimum");
        when(nativeObject.get(eq("loggingType"))).thenReturn("interval");

        //when:
        DataPointLoggingTypeProperties.byNativeObject(nativeObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_byNativeObject_with_bad_period_then_exception() {
        //given:
        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("intervalPeriodType"))).thenReturn("weeks");
        when(nativeObject.get(eq("intervalPeriod"))).thenReturn(-1.0);
        when(nativeObject.get(eq("intervalLoggingType"))).thenReturn("minimum");
        when(nativeObject.get(eq("loggingType"))).thenReturn("interval");

        //when:
        DataPointLoggingTypeProperties.byNativeObject(nativeObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_byNativeObject_with_bad_intervalLoggingType_then_exception() {
        //given:
        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("intervalPeriodType"))).thenReturn("weeks");
        when(nativeObject.get(eq("intervalPeriod"))).thenReturn(22.0);
        when(nativeObject.get(eq("intervalLoggingType"))).thenReturn("minimum2");
        when(nativeObject.get(eq("loggingType"))).thenReturn("interval");

        //when:
        DataPointLoggingTypeProperties.byNativeObject(nativeObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_byNativeObject_with_bad_loggingType_then_exception() {
        //given:
        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("intervalPeriodType"))).thenReturn("weeks");
        when(nativeObject.get(eq("intervalPeriod"))).thenReturn(22.0);
        when(nativeObject.get(eq("intervalLoggingType"))).thenReturn("minimum");
        when(nativeObject.get(eq("loggingType"))).thenReturn("interval2");

        //when:
        DataPointLoggingTypeProperties.byNativeObject(nativeObject);
    }

    @Test
    public void when_byNativeObject_with_loggingType_inTervAL() {
        //given:
        DataPointLoggingTypeProperties expected = DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS,
                22, IntervalLoggingType.MINIMUM);

        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("intervalPeriodType"))).thenReturn("weeks");
        when(nativeObject.get(eq("intervalPeriod"))).thenReturn(22.0);
        when(nativeObject.get(eq("intervalLoggingType"))).thenReturn("minimum");
        when(nativeObject.get(eq("loggingType"))).thenReturn("inTervAl");

        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.byNativeObject(nativeObject);

        //then:
        assertEquals(expected, result);
    }
}