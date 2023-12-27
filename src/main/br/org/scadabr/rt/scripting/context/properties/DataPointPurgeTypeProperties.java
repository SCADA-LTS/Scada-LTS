package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.PurgeType;
import org.mozilla.javascript.NativeObject;

import java.util.Objects;

public class DataPointPurgeTypeProperties implements DataPointUpdate {

    private final PurgeType purgeType;
    private final int purgePeriod;

    public DataPointPurgeTypeProperties(PurgeType purgeType, int purgePeriod) {
        this.purgeType = purgeType;
        this.purgePeriod = purgePeriod;
    }

    public static DataPointPurgeTypeProperties defaultProperties() {
        return new DataPointPurgeTypeProperties(PurgeType.YEARS,1);
    }

    public static DataPointPurgeTypeProperties byNativeObject(NativeObject nativeObject) {
        DataPointPurgeTypeProperties defaultProperties = DataPointPurgeTypeProperties.defaultProperties();

        String purgeType = (String)nativeObject.get("purgeType");
        Double purgePeriod = (Double)nativeObject.get("purgePeriod");

        if(purgePeriod != null && purgePeriod < 0) {
            throw new IllegalArgumentException("purgePeriod must be > 0");
        }

        return new DataPointPurgeTypeProperties(purgeType == null ? defaultProperties.getPurgeType() : PurgeType.valueOf(purgeType.toUpperCase()),
                purgePeriod == null ? defaultProperties.getPurgePeriod() : purgePeriod.intValue());
    }

    @Override
    public void updateDataPoint(DataPointVO dataPoint) {
        dataPoint.setPurgeType(purgeType.getCode());
        dataPoint.setPurgePeriod(purgePeriod);
    }

    public PurgeType getPurgeType() {
        return purgeType;
    }

    public int getPurgePeriod() {
        return purgePeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPointPurgeTypeProperties)) return false;
        DataPointPurgeTypeProperties that = (DataPointPurgeTypeProperties) o;
        return getPurgePeriod() == that.getPurgePeriod() && getPurgeType() == that.getPurgeType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPurgeType(), getPurgePeriod());
    }

    @Override
    public String toString() {
        return "DataPointPurgeTypeProperties{" +
                "purgeType=" + purgeType +
                ", purgePeriod=" + purgePeriod +
                '}';
    }
}