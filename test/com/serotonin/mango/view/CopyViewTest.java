package com.serotonin.mango.view;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static utils.CopyViewTestUtils.*;

public class CopyViewTest {

    private View expected;

    @Before
    public void config() {
        View view = new View();
        view.setViewComponents(Arrays.asList(
                createAnalogGraphicComponent(),
                createBinaryGraphicComponent(),
                createChartComparatorComponent(),
                createEnhancedImageChartComponent(),
                createButtonComponent(),
                createHtmlComponent(),
                createDynamicGraphicComponent(),
                createWirelessTempHumSensor(),
                createThumbnailComponent(),
                createSimplePointComponent(),
                createSimpleCompoundComponent(),
                createSimpleImageComponent(),
                createFlexBuilderComponent(),
                createMultistateGraphicComponent(),
                createLinkComponent(),
                createScriptButtonComponent(),
                createEnhancedPointComponent(),
                createScriptComponent(),
                createImageChartComponent()

        ));
        view.setId(123);
        view.setXid("VW_TEST");
        view.setViewUsers(Arrays.asList(new ShareUser(321, 1), new ShareUser(456, 2)));
        view.setName("_Name_test");
        view.setModificationTime(12335145614L);
        view.setAnonymousAccess(2);
        view.setHeight(78);
        view.setWidth(91);
        view.setBackgroundFilename("_BackgroundFilename_test");
        view.setResolution(ResolutionView.R1440x900);
        view.setUserId(23);
        this.expected = view;
    }

    @Test
    public void when_copy_then_equals_true() {
        View result = expected.copy();
        assertEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_ViewComponents_list_then_equals_false() {
        View result = expected.copy();
        result.getViewComponents().add(createDynamicGraphicComponent());
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_ViewUsers_list_then_equals_false() {
        View result = expected.copy();
        result.getViewUsers().add(new ShareUser(3, 0));
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_userId_then_equals_false() {
        View result = expected.copy();
        result.setUserId(43);
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_then_other_reference() {
        View result = expected.copy();
        assertNotSame(expected, result);
    }
}