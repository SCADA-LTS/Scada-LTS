package br.org.scadabr.rt.scripting.context.properties;

import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataPointDiscardValuesPropertiesTest {

    @Test
    public void when_defaultProperties_then_DiscardHighLimit() {

        //when:
        DataPointDiscardValuesProperties defaultProperties = DataPointDiscardValuesProperties.defaultProperties();

        //then:
        assertEquals(1.7976931348623157E308, defaultProperties.getDiscardHighLimit(), 0.0001);
    }

    @Test
    public void when_defaultProperties_then_DiscardLowLimit() {
        //when:
        DataPointDiscardValuesProperties defaultProperties = DataPointDiscardValuesProperties.defaultProperties();

        //then:
        assertEquals(-1.7976931348623157E308, defaultProperties.getDiscardLowLimit(), 0.0001);
    }

    @Test
    public void when_defaultProperties_then_discardExtremeValues_false() {
        //when:
        DataPointDiscardValuesProperties defaultProperties = DataPointDiscardValuesProperties.defaultProperties();

        //then:
        assertEquals(false, defaultProperties.isDiscardExtremeValues());
    }

    @Test
    public void when_byNativeObject_with_discardExtremeValues_true() {
        //given:
        DataPointDiscardValuesProperties expected = new DataPointDiscardValuesProperties(true, 33.33, -22.22);

        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("discardLowLimit"))).thenReturn(-22.22);
        when(nativeObject.get(eq("discardHighLimit"))).thenReturn(33.33);
        when(nativeObject.get(eq("discardExtremeValues"))).thenReturn(true);

        //when:
        DataPointDiscardValuesProperties result = DataPointDiscardValuesProperties.byNativeObject(nativeObject);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_byNativeObject_with_discardExtremeValues_false() {
        //given:
        DataPointDiscardValuesProperties expected = new DataPointDiscardValuesProperties(false, 33.33, -22.22);

        NativeObject nativeObject = mock(NativeObject.class);
        when(nativeObject.get(eq("discardLowLimit"))).thenReturn(-22.22);
        when(nativeObject.get(eq("discardHighLimit"))).thenReturn(33.33);
        when(nativeObject.get(eq("discardExtremeValues"))).thenReturn(false);

        //when:
        DataPointDiscardValuesProperties result = DataPointDiscardValuesProperties.byNativeObject(nativeObject);

        //then:
        assertEquals(expected, result);
    }
}