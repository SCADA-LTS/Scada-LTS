package utils;

import br.org.scadabr.view.component.*;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.view.*;
import com.serotonin.mango.view.component.*;
import com.serotonin.mango.view.text.MultistateRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.Arrays;
import java.util.Collections;

public final class CopyViewTestUtils {

    private final static String BKG_COLOR = "#ff0000";

    private CopyViewTestUtils() {}

    public static ScriptComponent createScriptComponent() {
        ScriptComponent component = new ScriptComponent();
        setScriptComponent(component);
        return component;
    }

    public static ButtonComponent createButtonComponent() {
        ButtonComponent component = new ButtonComponent();
        component.setWhenOffLabel("whenOffLabel");
        component.setWhenOnLabel("whenOnLabel");
        component.setHeight(567);
        component.setWidth(890);
        setScriptComponent(component);
        return component;
    }

    public static HtmlComponent createHtmlComponent() {
        HtmlComponent component = new HtmlComponent();
        setHtmlComponent(component);
        return component;
    }

    public static ChartComparatorComponent createChartComparatorComponent() {
        ChartComparatorComponent component = new ChartComparatorComponent();
        component.setHeight(43);
        component.setWidth(54);
        setHtmlComponent(component);
        return component;
    }

    public static FlexBuilderComponent createFlexBuilderComponent() {
        FlexBuilderComponent component = new FlexBuilderComponent();
        component.setProjectDefined(true);
        component.setHeight(78);
        component.setWidth(91);
        component.setProjectId(29);
        component.setProjectSource("_ProjectSource_test");
        component.setRuntimeMode(true);
        setHtmlComponent(component);
        return component;
    }

    public static LinkComponent createLinkComponent() {
        LinkComponent component = new LinkComponent();
        component.setLink("_link_test");
        setHtmlComponent(component);
        return component;
    }

    public static ScriptButtonComponent createScriptButtonComponent() {
        ScriptButtonComponent component = new ScriptButtonComponent();
        component.setScriptXid("_ScriptXid_test");
        setHtmlComponent(component);
        return component;
    }

    public static AnalogGraphicComponent createAnalogGraphicComponent() {
        AnalogGraphicComponent component = new AnalogGraphicComponent();
        component.setMax(1234);
        component.setMin(-1233);
        component.setDisplayText(true);
        setImageSetComponent(component);
        return component;
    }

    public static BinaryGraphicComponent createBinaryGraphicComponent() {
        BinaryGraphicComponent component = new BinaryGraphicComponent();
        component.setOneImage(888);
        component.setZeroImage(324);
        setImageSetComponent(component);
        return component;
    }

    public static DynamicGraphicComponent createDynamicGraphicComponent() {
        DynamicGraphicComponent component = new DynamicGraphicComponent();
        component.setMax(111);
        component.setMin(222);
        component.setDisplayText(true);
        component.tsetDynamicImage(DynamicImage.unavailable("333"));
        setPointComponent(component);
        return component;
    }


    public static EnhancedPointComponent createEnhancedPointComponent() {
        EnhancedPointComponent component = new EnhancedPointComponent();
        component.setColor("_Color_test");
        component.setAlias("_Alias_test");
        component.setShowPoints(true);
        component.setStrokeWidth(123.123f);
        component.setLineType(EnhancedPointLineType.SPLINE);
        setSimplePointComponent(component);
        return component;
    }

    public static EnhancedImageChartComponent createEnhancedImageChartComponent() {
        EnhancedImageChartComponent component = new EnhancedImageChartComponent();
        component.setEnhancedImageChartType(EnhancedImageChartType.DYNAMIC);
        component.setDurationPeriods(12);
        component.setDurationType(Common.TimePeriods.WEEKS);
        setCompoundComponent(component);
        return component;
    }

    public static ImageChartComponent createImageChartComponent() {
        ImageChartComponent component = new ImageChartComponent();
        component.setDurationPeriods(12);
        component.setDurationType(Common.TimePeriods.WEEKS);
        component.setHeight(32);
        component.setWidth(34);
        setCompoundComponent(component);
        return component;
    }

    public static MultistateGraphicComponent createMultistateGraphicComponent() {
        MultistateGraphicComponent component = new MultistateGraphicComponent();
        component.setDefaultImage(71);
        component.setImageStateList(Arrays.asList(new IntValuePair(72, "_value72_test"), new IntValuePair(73, "_value73_test")));
        setImageSetComponent(component);
        return component;
    }

    public static SimpleCompoundComponent createSimpleCompoundComponent() {
        SimpleCompoundComponent component = new SimpleCompoundComponent();
        component.setBackgroundColour("_BackgroundColour_test");
        setCompoundComponent(component);
        return component;
    }

    public static SimpleImageComponent createSimpleImageComponent() {
        SimpleImageComponent component = new SimpleImageComponent();
        setPointComponent(component);
        return component;
    }

    public static SimplePointComponent createSimplePointComponent() {
        SimplePointComponent component = new SimplePointComponent();
        setSimplePointComponent(component);
        return component;
    }

    public static ThumbnailComponent createThumbnailComponent() {
        ThumbnailComponent component = new ThumbnailComponent();
        component.setScalePercent(56);
        setPointComponent(component);
        return component;
    }

    public static WirelessTempHumSensor createWirelessTempHumSensor() {
        WirelessTempHumSensor component = new WirelessTempHumSensor();
        setCompoundComponent(component);
        return component;
    }

    private static CompoundChild createCompoundChild(String id, ViewComponent viewComponent) {
        return new CompoundChild(id, new LocalizableMessage("description"), viewComponent, new int[]{1, 2});
    }

    private static DataPointVO createDataPointVO(int id, String xid) {
        PointValueTime value = new PointValueTime(new BinaryValue(false), 0);
        TextRenderer textRenderer = new MultistateRenderer();
        PointLocatorVO locatorFromContext = new VirtualPointLocatorVO();
        ((VirtualPointLocatorVO) locatorFromContext).setDataTypeId(value.getValue().getDataType());
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setPointLocator(locatorFromContext);
        dataPointVO.setEventDetectors(Collections.emptyList());
        dataPointVO.setId(id);
        dataPointVO.setName("_dataPointName_test");
        dataPointVO.setXid(xid);
        dataPointVO.setTextRenderer(textRenderer);
        return dataPointVO;
    }

    private static ScriptComponent setScriptComponent(ScriptComponent component) {
        component.setScript("_Script_test");
        setPointComponent(component);
        return component;
    }

    private static void setPointComponent(PointComponent component) {
        component.setBkgdColorOverride(BKG_COLOR);
        component.setDisplayControls(true);
        component.tsetDataPoint(createDataPointVO(3, "DP_TEST"));
        component.setSettableOverride(true);
        component.setNameOverride("_NameOverride_test");
        setViewComponent(component);
    }

    private static void setViewComponent(ViewComponent viewComponent) {
        viewComponent.setIndex(123);
        viewComponent.setLocation(456,789);
        viewComponent.setIdSuffix("_IdSuffix_test");
        viewComponent.setStyle("_Style_test");
        viewComponent.setZ(21);
    }

    private static void setCompoundComponent(CompoundComponent viewComponent) {
        String childId1 = "_childid1_test";
        String childId2 = "_childid2_test";
        viewComponent.setChildren(Arrays.asList(createCompoundChild(childId1, createHtmlComponent()), createCompoundChild(childId2, createThumbnailComponent())));
        viewComponent.setDataPoint(childId1, createDataPointVO(1, "DP_TEST_1"));
        viewComponent.setDataPoint(childId2, createDataPointVO(2, "DP_TEST_2"));
        viewComponent.setName("_Name_test");
        setViewComponent(viewComponent);
    }

    private static void setSimplePointComponent(SimplePointComponent viewComponent) {
        viewComponent.setDisplayPointName(true);
        viewComponent.setStyleAttribute("_StyleAttribute_test");
        setPointComponent(viewComponent);
    }

    private static void setImageSetComponent(ImageSetComponent viewComponent) {
        viewComponent.setDisplayText(true);
        viewComponent.tsetImageSet(ImageSet.unavailable("453"));
        setPointComponent(viewComponent);
    }

    private static void setHtmlComponent(HtmlComponent viewComponent) {
        viewComponent.setContent("_content_test");
        setViewComponent(viewComponent);
    }
}
