package br.org.scadabr.rt.scripting.context.properties;


import com.serotonin.mango.vo.IntervalLoggingPeriodType;
import com.serotonin.mango.vo.IntervalLoggingType;
import com.serotonin.mango.vo.PurgeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(Parameterized.class)
public class DataPointLoggingTypeNativeObjectTest {

    @Parameterized.Parameters(name= "{index}: properties: {0}")
    public static Object[] data() {
        return new Object[][] {
                {DataPointLoggingTypeProperties.onChange(0.555)},
                {DataPointLoggingTypeProperties.onChange(0.9)},
                {DataPointLoggingTypeProperties.all()},
                {DataPointLoggingTypeProperties.none()},
                {DataPointLoggingTypeProperties.onTsChange()},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.SECONDS, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MINUTES, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.HOURS, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MONTHS, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.YEARS, 14, IntervalLoggingType.AVERAGE)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.SECONDS, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MINUTES, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.HOURS, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MONTHS, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.YEARS, 14, IntervalLoggingType.MINIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.SECONDS, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MINUTES, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.HOURS, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MONTHS, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.YEARS, 14, IntervalLoggingType.MAXIMUM)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.SECONDS, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MINUTES, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.HOURS, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MONTHS, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.YEARS, 14, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.SECONDS, 3, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MINUTES, 3, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.HOURS, 3, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 3, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.WEEKS, 3, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.MONTHS, 3, IntervalLoggingType.INSTANT)},
                {DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.YEARS, 3, IntervalLoggingType.INSTANT)}
        };
    }

    private final DataPointUpdate expected;
    private final NativeObject nativeObjectMock;

    public DataPointLoggingTypeNativeObjectTest(DataPointLoggingTypeProperties dataPointUpdate) {
        this.expected = dataPointUpdate;
        this.nativeObjectMock = mock(NativeObject.class);
        when(nativeObjectMock.get(eq("intervalPeriodType"))).thenReturn(dataPointUpdate.getIntervalPeriodType().name());
        when(nativeObjectMock.get(eq("intervalPeriod"))).thenReturn((double)dataPointUpdate.getIntervalPeriod());
        when(nativeObjectMock.get(eq("intervalLoggingType"))).thenReturn(dataPointUpdate.getIntervalLoggingType().name());
        when(nativeObjectMock.get(eq("loggingType"))).thenReturn(dataPointUpdate.getLoggingType().name());
        when(nativeObjectMock.get(eq("tolerance"))).thenReturn(dataPointUpdate.getTolerance());
    }

    @Test
    public void when_byNativeObject_for_PurgeType() {
        //when:
        DataPointLoggingTypeProperties result = DataPointLoggingTypeProperties.byNativeObject(nativeObjectMock);

        //then:
        assertEquals(expected, result);
    }
}
