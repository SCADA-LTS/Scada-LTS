package com.serotonin.mango.view.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static utils.CopyViewTestUtils.*;

@RunWith(Parameterized.class)
public class CopyViewComponentTest {

    @Parameterized.Parameters(name= "{index}: viewComponent: {0}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                {createScriptComponent()},
                {createButtonComponent()},
                {createHtmlComponent()},
                {createAnalogGraphicComponent()},
                {createEnhancedPointComponent()},
                {createBinaryGraphicComponent()},
                {createDynamicGraphicComponent()},
                {createFlexBuilderComponent()},
                {createLinkComponent()},
                {createImageChartComponent()},
                {createSimpleImageComponent()},
                {createChartComparatorComponent()},
                {createEnhancedImageChartComponent()},
                {createMultistateGraphicComponent()},
                {createSimpleCompoundComponent()},
                {createScriptButtonComponent()},
                {createThumbnailComponent()},
                {createWirelessTempHumSensor()},
                {createSimplePointComponent()},
        });
    }

    private final ViewComponent expected;

    public CopyViewComponentTest(ViewComponent expected) {
        this.expected = expected;
    }

    @Test
    public void when_copy_then_equals_true() {
        ViewComponent result = expected.copy();
        assertEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_then_equals_false() {
        ViewComponent result = expected.copy();
        result.setIndex(99);
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_then_other_reference() {
        ViewComponent result = expected.copy();
        assertNotSame(expected, result);
    }
}