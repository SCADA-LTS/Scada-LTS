package com.serotonin.mango.view.component;

import com.serotonin.web.i18n.LocalizableMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static utils.CopyViewTestUtils.*;
import static utils.CopyViewTestUtils.createDynamicGraphicComponent;

@RunWith(Parameterized.class)
public class CopyCompoundChildTest {

    @Parameterized.Parameters(name= "{index}: viewComponent: {0}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                {new CompoundChild("ID_1", new LocalizableMessage("description_1"), createScriptComponent(), new int[]{1})},
                {new CompoundChild("ID_2", new LocalizableMessage("description_2"), createButtonComponent(), new int[]{2})},
                {new CompoundChild("ID_3", new LocalizableMessage("description_3"), createHtmlComponent(), new int[]{3})},
                {new CompoundChild("ID_4", new LocalizableMessage("description_4"), createAnalogGraphicComponent(), new int[]{4})},
                {new CompoundChild("ID_5", new LocalizableMessage("description_5"), createEnhancedPointComponent(), new int[]{5, 6})},
                {new CompoundChild("ID_6", new LocalizableMessage("description_6"), createBinaryGraphicComponent(), new int[]{7})},
                {new CompoundChild("ID_7", new LocalizableMessage("description_7"), createDynamicGraphicComponent(), new int[]{8})},
                {new CompoundChild("ID_8", new LocalizableMessage("description_8"), createFlexBuilderComponent(), new int[]{9, 10, 11})},
                {new CompoundChild("ID_9", new LocalizableMessage("description_9"), createLinkComponent(), new int[]{12})},
                {new CompoundChild("ID_10", new LocalizableMessage("description_10"), createImageChartComponent(), new int[]{13})},
                {new CompoundChild("ID_11", new LocalizableMessage("description_11"), createSimpleImageComponent(), new int[]{14, 15})},
                {new CompoundChild("ID_12", new LocalizableMessage("description_12"), createChartComparatorComponent(), new int[]{16})},
                {new CompoundChild("ID_13", new LocalizableMessage("description_13"), createEnhancedImageChartComponent(), new int[]{17})},
                {new CompoundChild("ID_14", new LocalizableMessage("description_14"), createMultistateGraphicComponent(), new int[]{18})},
                {new CompoundChild("ID_15", new LocalizableMessage("description_15"), createSimpleCompoundComponent(), new int[]{19})},
                {new CompoundChild("ID_16", new LocalizableMessage("description_16"), createScriptButtonComponent(), new int[]{20})},
                {new CompoundChild("ID_17", new LocalizableMessage("description_17"), createThumbnailComponent(), new int[]{21})},
                {new CompoundChild("ID_18", new LocalizableMessage("description_18"), createWirelessTempHumSensor(), new int[]{22, 23, 34, 25})},
                {new CompoundChild("ID_19", new LocalizableMessage("description_19"), createSimplePointComponent(), new int[]{26})},
        });
    }

    private final CompoundChild expected;

    public CopyCompoundChildTest(CompoundChild expected) {
        this.expected = expected;
    }

    @Test
    public void when_copy_then_equals_true() {
        CompoundChild result = expected.copy();
        assertEquals(expected, result);
    }

    @Test
    public void when_copy_and_modified_ViewComponent_setZ_then_equals_false() {
        CompoundChild result = expected.copy();
        result.getViewComponent().setZ(12467);
        assertNotEquals(expected, result);
    }

    @Test
    public void when_copy_then_other_reference() {
        CompoundChild result = expected.copy();
        assertNotSame(expected, result);
    }
}