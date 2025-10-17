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
    public static Collection data() {
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
        //when:
        ViewComponent result = expected.copy();

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Index_then_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setIndex(99);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Z_then_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setZ(88);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Style_then_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setStyle("new style");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_IdSuffix_then_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setIdSuffix("new IdSuffix");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Location_then_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setLocation(1,2);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Xthen_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setX(3);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Y_then_equals_false() {
        //when:
        ViewComponent result = expected.copy();

        //and:
        result.setY(4);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_then_other_reference() {
        //when:
        ViewComponent result = expected.copy();

        //then:
        assertNotSame(expected, result);
    }
}