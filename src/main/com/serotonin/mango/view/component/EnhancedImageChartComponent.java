package com.serotonin.mango.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueFacade;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImplDefinition;

/**
 * Component for Enhanced Image Chart.
 * 
 * @author Jacek Rogoznicki
 */
@JsonRemoteEntity
public class EnhancedImageChartComponent extends CompoundComponent {
    public static ImplDefinition DEFINITION = new ImplDefinition("enhancedImageChart", "ENHANCED_IMAGE_CHART",
            "graphic.enhancedImageChart", null);

    public static final String POINT_1 = "point1";
    public static final String POINT_2 = "point2";
    public static final String POINT_3 = "point3";
    public static final String POINT_4 = "point4";
    public static final String POINT_5 = "point5";
    public static final String POINT_6 = "point6";
    public static final String POINT_7 = "point7";
    public static final String POINT_8 = "point8";
    public static final String POINT_9 = "point9";
    public static final String POINT_10 = "point10";

    private static String[] defaultColors = { "#0000ff", "#00ff00", "#ff0000", "#ff00fa", "#ff7f00", "#7f0000",
            "#7f007f", "#00ffff", "#ffff00", "#000000" };
    private static float defaultStrokeWidth = 2;
    private static EnhancedPointLineType defaultLineType = EnhancedPointLineType.LINE;

    @JsonRemoteProperty
    private int width = 500;
    @JsonRemoteProperty
    private int height = 300;
    private int durationType = Common.TimePeriods.DAYS;
    @JsonRemoteProperty
    private int durationPeriods = 1;

    private EnhancedImageChartType enhancedImageChartType = EnhancedImageChartType.DYNAMIC;

    public EnhancedImageChartComponent() {
        initialize();
    }

    private EnhancedImageChartComponent(EnhancedImageChartComponent enhancedImageChartComponent) {
        super(enhancedImageChartComponent);
        this.width = enhancedImageChartComponent.getWidth();
        this.height = enhancedImageChartComponent.getHeight();
        this.durationType = enhancedImageChartComponent.getDurationType();
        this.durationPeriods = enhancedImageChartComponent.getDurationPeriods();
        this.enhancedImageChartType = enhancedImageChartComponent.getEnhancedImageChartType();
    }

    @Override
    protected void initialize() {
        addChild(POINT_1, "graphic.enhancedImageChart." + POINT_1, new EnhancedPointComponent(defaultColors[0],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_2, "graphic.enhancedImageChart." + POINT_2, new EnhancedPointComponent(defaultColors[1],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_3, "graphic.enhancedImageChart." + POINT_3, new EnhancedPointComponent(defaultColors[2],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_4, "graphic.enhancedImageChart." + POINT_4, new EnhancedPointComponent(defaultColors[3],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_5, "graphic.enhancedImageChart." + POINT_5, new EnhancedPointComponent(defaultColors[4],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_6, "graphic.enhancedImageChart." + POINT_6, new EnhancedPointComponent(defaultColors[5],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_7, "graphic.enhancedImageChart." + POINT_7, new EnhancedPointComponent(defaultColors[6],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_8, "graphic.enhancedImageChart." + POINT_8, new EnhancedPointComponent(defaultColors[7],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_9, "graphic.enhancedImageChart." + POINT_9, new EnhancedPointComponent(defaultColors[8],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
        addChild(POINT_10, "graphic.enhancedImageChart." + POINT_10, new EnhancedPointComponent(defaultColors[9],
                defaultStrokeWidth, defaultLineType), new int[] { DataTypes.NUMERIC });
    }

    @Override
    public boolean hasInfo() {
        return true;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDurationType() {
        return durationType;
    }

    public void setDurationType(int durationType) {
        this.durationType = durationType;
    }

    public int getDurationPeriods() {
        return durationPeriods;
    }

    public void setDurationPeriods(int durationPeriods) {
        this.durationPeriods = durationPeriods;
    }

    public EnhancedImageChartType getEnhancedImageChartType() {
        return enhancedImageChartType;
    }

    public void setEnhancedImageChartType(EnhancedImageChartType enhancedImageChartType) {
        this.enhancedImageChartType = enhancedImageChartType;
    }

    @Override
    public ImplDefinition definition() {
        return DEFINITION;
    }

    @Override
    public String getStaticContent() {
        return null;
    }

    @Override
    public boolean isDisplayImageChart() {
        return false;
    }

    @Override
    public String getImageChartData(ResourceBundle bundle) {
        return null;
    }

    public String generateImageChartData() {
        return generateImageChartData(false);
    }

    @Override
    public ViewComponent copy() {
        return new EnhancedImageChartComponent(this);
    }

    public String generateImageChartData(boolean getDataForStatic) {
        if (EnhancedImageChartType.STATIC.equals(enhancedImageChartType) && !getDataForStatic) {
            return "";
        }

        long to = System.currentTimeMillis();
        long from = to - Common.getMillis(durationType, durationPeriods);

        Map<Long, Number[]> data = new TreeMap<Long, Number[]>();

        List<CompoundChild> children = getChildComponents();
        int childCount = nonEmptyChildrenCount();
        int j = 0;
        for (CompoundChild child : children) {
            int dataPointId = ((EnhancedPointComponent) child.getViewComponent()).getDataPointId();
            if (dataPointId != 0) {
                PointValueFacade pointValueFacade = new PointValueFacade(dataPointId);
                List<PointValueTime> pointData = pointValueFacade.getPointValuesBetween(from, to);
                for (PointValueTime val : pointData) {
                    if (data.get(val.getTime()) == null) {
                        data.put(val.getTime(), new Number[childCount]);
                    }
                    data.get(val.getTime())[j] = val.getValue().numberValue();
                }
                j++;
            }
        }

        StringBuilder csv = new StringBuilder();

        for (Long time : data.keySet()) {
            csv.append(time);
            for (Number val : data.get(time)) {
                csv.append(",").append(val == null ? "" : val);
            }
            csv.append("\n");
        }

        return csv.toString().trim();
    }

    private int nonEmptyChildrenCount() {
        List<CompoundChild> children = getChildComponents();
        int i;
        int count = 0;
        for (CompoundChild child : children) {
            if (((EnhancedPointComponent) child.getViewComponent()).tgetDataPoint() != null) {
                count++;
            }
        }
        return count;
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(width);
        out.writeInt(height);
        out.writeInt(durationType);
        out.writeInt(durationPeriods);
        out.writeInt(enhancedImageChartType.ordinal());

        List<CompoundChild> children = getChildComponents();
        out.writeInt(children.size());

        for (CompoundChild child : children) {
            ((EnhancedPointComponent) child.getViewComponent()).writeObject(out);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be
        // elegantly handled.
        if (ver == 1) {
            width = in.readInt();
            height = in.readInt();
            durationType = in.readInt();
            durationPeriods = in.readInt();
            enhancedImageChartType = EnhancedImageChartType.values()[in.readInt()];

            int len = in.readInt();
            List<CompoundChild> children = getChildComponents();
            for (int i = 0; i < len; i++) {
                ((EnhancedPointComponent) children.get(i).getViewComponent()).readObject(in);
            }
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        String text = json.getString("durationType");
        if (text == null)
            throw new LocalizableJsonException("emport.error.chart.missing", "durationType",
                    Common.TIME_PERIOD_CODES.getCodeList());

        durationType = Common.TIME_PERIOD_CODES.getId(text);
        if (durationType == -1)
            throw new LocalizableJsonException("emport.error.chart.invalid", "durationType", text,
                    Common.TIME_PERIOD_CODES.getCodeList());
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);

        map.put("durationType", Common.TIME_PERIOD_CODES.getCode(durationType));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnhancedImageChartComponent)) return false;
        if (!super.equals(o)) return false;
        EnhancedImageChartComponent that = (EnhancedImageChartComponent) o;
        return getWidth() == that.getWidth() && getHeight() == that.getHeight() && getDurationType() == that.getDurationType() && getDurationPeriods() == that.getDurationPeriods() && getEnhancedImageChartType() == that.getEnhancedImageChartType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWidth(), getHeight(), getDurationType(), getDurationPeriods(), getEnhancedImageChartType());
    }

    @Override
    public String toString() {
        return "EnhancedImageChartComponent{" +
                "width=" + width +
                ", height=" + height +
                ", durationType=" + durationType +
                ", durationPeriods=" + durationPeriods +
                ", enhancedImageChartType=" + enhancedImageChartType +
                "} " + super.toString();
    }
}
