package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.ObjectUtils;

public class PointValueState {

    private final PointValueTime newValue;
    private final PointValueTime oldValue;
    /**
     * This is the value around which tolerance decisions will be made when
     * determining whether to log numeric values.
     */
    private double toleranceOrigin;
    private final boolean logValue;
    private final boolean saveValue;
    private final boolean backdated;

    private PointValueState(PointValueTime newValue, PointValueState oldState, DataPointVO vo) {
        if(oldState != null && newValue != null) {
            this.newValue = newValue;
            this.oldValue = oldState.getNewValue();
            this.toleranceOrigin = oldState.toleranceOrigin;
            this.logValue = isLogValue(vo);
            this.saveValue = isSaveValue(vo, logValue);
            this.backdated = isBackdated(newValue, oldValue);
        } else if (newValue != null) {
            this.newValue = newValue;
            this.oldValue = null;
            this.toleranceOrigin = newValue.getValue() instanceof NumericValue ? newValue.getDoubleValue() : 0.0;
            this.logValue = isLogValue(vo);
            this.saveValue = isSaveValue(vo, logValue);
            this.backdated = false;
        } else {
            this.newValue = null;
            this.oldValue = null;
            this.toleranceOrigin = 0.0;
            this.logValue = isLogValue(vo);
            this.saveValue = isSaveValue(vo, logValue);
            this.backdated = false;
        }
    }

    public static PointValueState newState(PointValueTime newValue, PointValueState oldState, DataPointVO vo) {
        return new PointValueState(newValue, oldState, vo);
    }

    public PointValueTime getNewValue() {
        return newValue;
    }

    public PointValueTime getOldValue() {
        return oldValue;
    }

    public boolean isLogValue() {
        return logValue;
    }

    public boolean isSaveValue() {
        return saveValue;
    }

    public boolean isBackdated() {
        return backdated;
    }

    public static boolean isLoggingTypeIn(DataPointVO vo, int type, int... types) {
        if(vo.getLoggingType() == type)
            return true;
        for (int typ: types) {
            if(vo.getLoggingType() == typ)
                return true;
        }
        return false;
    }

    private boolean isLogValue(DataPointVO vo) {
        if(isLoggingTypeIn(vo, DataPointVO.LoggingTypes.ON_CHANGE, DataPointVO.LoggingTypes.ALL,
                DataPointVO.LoggingTypes.ON_TS_CHANGE)) {
            switch (vo.getLoggingType()) {
                case DataPointVO.LoggingTypes.ON_CHANGE:
                    return updateToleranceOrigin(vo.getTolerance());
                case DataPointVO.LoggingTypes.ALL:
                    return true;
                case DataPointVO.LoggingTypes.ON_TS_CHANGE:
                    if (oldValue == null)
                        return true;
                    else
                        return newValue.getTime() != oldValue.getTime();
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    private boolean updateToleranceOrigin(double tolerance) {
        if(newValue == null)
            return false;

        boolean updated = update(tolerance);

        if(updated && (newValue.getValue() instanceof NumericValue)) {
            this.toleranceOrigin = newValue.getDoubleValue();
        }
        return updated;
    }

    private boolean update(double tolerance) {
        if (oldValue == null) {
            return true;
        } else if (isBackdated()) {
            return false;
        } else {
            if (newValue.getValue() instanceof NumericValue) {
                double diff = toleranceOrigin - newValue.getDoubleValue();
                if (diff < 0)
                    diff = -diff;
                return diff > tolerance;
            }
            return !ObjectUtils.isEqual(newValue.getValue(), oldValue.getValue());
        }
    }

    private static boolean isSaveValue(DataPointVO vo, boolean logValue) {
        if (isLoggingTypeIn(vo, DataPointVO.LoggingTypes.ON_CHANGE, DataPointVO.LoggingTypes.ALL,
                DataPointVO.LoggingTypes.ON_TS_CHANGE)) {
            return logValue;
        }
        return true;
    }

    private static boolean isBackdated(PointValueTime newValue, PointValueTime oldValue) {
        return oldValue != null && newValue.getTime() < oldValue.getTime();
    }
}
