package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.DataPointVO;
import org.mozilla.javascript.NativeObject;

public class DataPointDiscardValuesProperties implements DataPointUpdate {

    private final boolean discardExtremeValues;
    private final double discardHighLimit;
    private final double discardLowLimit;

    public DataPointDiscardValuesProperties(boolean discardExtremeValues, double discardHighLimit, double discardLowLimit) {
        this.discardExtremeValues = discardExtremeValues;
        this.discardHighLimit = discardHighLimit;
        this.discardLowLimit = discardLowLimit;
    }

    public static DataPointDiscardValuesProperties defaultProperties() {
        return new DataPointDiscardValuesProperties(false, 1.7976931348623157E308, -1.7976931348623157E308);
    }

    public static DataPointDiscardValuesProperties byNativeObject(NativeObject nativeObject) {
        DataPointDiscardValuesProperties defaultProperties = DataPointDiscardValuesProperties.defaultProperties();

        Boolean discardExtremeValues = (Boolean)nativeObject.get("discardExtremeValues");
        Double discardHighLimit = (Double)nativeObject.get("discardHighLimit");
        Double discardLowLimit = (Double)nativeObject.get("discardLowLimit");

        return new DataPointDiscardValuesProperties(
                discardExtremeValues == null ? defaultProperties.isDiscardExtremeValues() : discardExtremeValues,
                discardHighLimit == null ? defaultProperties.getDiscardHighLimit(): discardHighLimit,
                discardLowLimit == null ? defaultProperties.getDiscardLowLimit() : discardLowLimit);
    }

    @Override
    public void update(DataPointVO dataPoint) {
        dataPoint.setDiscardExtremeValues(discardExtremeValues);
        dataPoint.setDiscardHighLimit(discardHighLimit);
        dataPoint.setDiscardLowLimit(discardLowLimit);
    }

    public boolean isDiscardExtremeValues() {
        return discardExtremeValues;
    }

    public double getDiscardHighLimit() {
        return discardHighLimit;
    }

    public double getDiscardLowLimit() {
        return discardLowLimit;
    }
}