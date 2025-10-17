package com.serotonin.mango.view;


import br.org.scadabr.vo.permission.ViewAccess;
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
        view.setAnonymousAccess(ViewAccess.SET);
        view.setHeight(78);
        view.setWidth(91);
        view.setBackgroundFilename("_BackgroundFilename_test");
        view.setResolution(ResolutionView.R1440x900);
        view.setUserId(23);
        this.expected = view;
    }

    @Test
    public void when_copy_then_equals_true() {
        //when:
        View result = expected.copy();

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_ViewComponents_list_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().add(createDynamicGraphicComponent());

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_Index_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setIndex(321);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_Y_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setY(321);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_X_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setX(321);

        //then:
        assertNotEquals(expected, result);
    }


    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_Location_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setLocation(1, 2);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_Style_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setStyle("new style");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_IdSuffix_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setIdSuffix("new IdSuffix");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewComponents_get_0_and_modified_Z_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewComponents().get(0).setZ(7);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_ViewUsers_list_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewUsers().add(new ShareUser(3, 0));

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewUsers_get_0_and_modified_AccessType_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewUsers().get(0).setAccessType(2);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_ViewUsers_get_0_and_modified_UserId_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.getViewUsers().get(0).setUserId(2);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_UserId_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setUserId(43);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Resolution_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setResolution(ResolutionView.R1600x1200);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Width_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setWidth(43);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Height_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setHeight(23);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_ModificationTime_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setModificationTime(23121245346L);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Name_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setName("new name");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_AnonymousAccess_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setAnonymousAccess(ViewAccess.READ);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Xid_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setXid("new Xid");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_BackgroundFilename_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setBackgroundFilename("new BackgroundFilename");

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_Id_then_equals_false() {
        //when:
        View result = expected.copy();

        //and:
        result.setId(9);

        //then:
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_then_other_reference() {
        //when:
        View result = expected.copy();

        //then:
        assertNotSame(expected, result);
    }
}