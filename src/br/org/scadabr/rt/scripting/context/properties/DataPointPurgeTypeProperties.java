package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.PurgeType;
import org.mozilla.javascript.NativeObject;

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
    public void update(DataPointVO dataPoint) {
        dataPoint.setPurgeType(purgeType.getId());
        dataPoint.setPurgePeriod(purgePeriod);
    }

    public PurgeType getPurgeType() {
        return purgeType;
    }

    public int getPurgePeriod() {
        return purgePeriod;
    }

}