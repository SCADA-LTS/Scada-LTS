package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.PurgeType;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class DataPointPurgeTypePropertiesTest {

    @Test
    public void when_defaultProperties_then_period_1() {

        //when:
        DataPointPurgeTypeProperties defaultProperties = DataPointPurgeTypeProperties.defaultProperties();

        //then:
        assertEquals(1, defaultProperties.getPurgePeriod());
    }

    @Test
    public void when_defaultProperties_then_PurgeType_YEARS() {

        //when:
        DataPointPurgeTypeProperties defaultProperties = DataPointPurgeTypeProperties.defaultProperties();

        //then:
        assertEquals(PurgeType.YEARS, defaultProperties.getPurgeType());
    }

    @Test
    public void when_byNativeObject_then_PurgeType_DAYS_period_123() {

        //given:
        DataPointPurgeTypeProperties expected = new DataPointPurgeTypeProperties(PurgeType.DAYS, 123);

        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("purgeType"))).thenReturn("days");
        when(nativeObject.get(eq("purgePeriod"))).thenReturn(123.0);

        //when:
        DataPointPurgeTypeProperties result = DataPointPurgeTypeProperties.byNativeObject(nativeObject);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_byNativeObject_then_PurgeType_DAYS_period_321() {

        //given:
        DataPointPurgeTypeProperties expected = new DataPointPurgeTypeProperties(PurgeType.DAYS, 321);

        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("purgeType"))).thenReturn("days");
        when(nativeObject.get(eq("purgePeriod"))).thenReturn(321.0);

        //when:
        DataPointPurgeTypeProperties result = DataPointPurgeTypeProperties.byNativeObject(nativeObject);

        //then:
        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_byNativeObject_with_bad_period_then_expcetion() {

        //given:
        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("purgeType"))).thenReturn("days");
        when(nativeObject.get(eq("purgePeriod"))).thenReturn(-1.0);

        //when:
        DataPointPurgeTypeProperties.byNativeObject(nativeObject);
    }
}