package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.DataPointVO;
import org.mozilla.javascript.NativeObject;

import java.util.Objects;

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

        if(discardLowLimit != null && discardHighLimit != null
                && (discardLowLimit > discardHighLimit || Objects.equals(discardLowLimit, discardHighLimit)))
            throw new IllegalArgumentException("must be discardHighLimit > discardLowLimit");

        return new DataPointDiscardValuesProperties(
                discardExtremeValues == null ? defaultProperties.isDiscardExtremeValues() : discardExtremeValues,
                discardHighLimit == null || discardLowLimit == null ? defaultProperties.getDiscardHighLimit(): discardHighLimit,
                discardHighLimit == null || discardLowLimit == null ? defaultProperties.getDiscardLowLimit() : discardLowLimit);
    }

    @Override
    public void updateDataPoint(DataPointVO dataPoint) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPointDiscardValuesProperties)) return false;
        DataPointDiscardValuesProperties that = (DataPointDiscardValuesProperties) o;
        return isDiscardExtremeValues() == that.isDiscardExtremeValues() && Double.compare(that.getDiscardHighLimit(), getDiscardHighLimit()) == 0 && Double.compare(that.getDiscardLowLimit(), getDiscardLowLimit()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDiscardExtremeValues(), getDiscardHighLimit(), getDiscardLowLimit());
    }

    @Override
    public String toString() {
        return "DataPointDiscardValuesProperties{" +
                "discardExtremeValues=" + discardExtremeValues +
                ", discardHighLimit=" + discardHighLimit +
                ", discardLowLimit=" + discardLowLimit +
                '}';
    }
}