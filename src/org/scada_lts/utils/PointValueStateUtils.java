package org.scada_lts.utils;

import com.serotonin.mango.rt.dataImage.PointValueState;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.ObjectUtils;

public final class PointValueStateUtils {

    private PointValueStateUtils() {}

    public static boolean isLoggingTypeIn(DataPointVO vo, int type, int... types) {
        if(vo.getLoggingType() == type)
            return true;
        for (int typ: types) {
            if(vo.getLoggingType() == typ)
                return true;
        }
        return false;
    }

    public static boolean isLogValue(PointValueTime newValue, PointValueState oldState, DataPointVO vo, SetPointSource source) {
        double toleranceOrigin = getToleranceOrigin(newValue, oldState, false);
        if(isLoggingTypeIn(vo, DataPointVO.LoggingTypes.ON_CHANGE, DataPointVO.LoggingTypes.ALL,
                DataPointVO.LoggingTypes.ON_TS_CHANGE)) {
            switch (vo.getLoggingType()) {
                case DataPointVO.LoggingTypes.ON_CHANGE:
                    return isChange(newValue, oldState, toleranceOrigin, vo.getTolerance(), source);
                case DataPointVO.LoggingTypes.ALL:
                    return true;
                case DataPointVO.LoggingTypes.ON_TS_CHANGE:
                    if (oldState.isEmpty())
                        return true;
                    else
                        return newValue.getTime() != oldState.getNewValue().getTime();
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public static double getToleranceOrigin(PointValueTime newValue, PointValueState oldState, boolean logValue) {
        if(oldState.isEmpty() || logValue)
            return newValue.getValue() instanceof NumericValue ? newValue.getDoubleValue() : 0.0;
        else
            return oldState.getToleranceOrigin();

    }

    public static boolean isSaveValue(DataPointVO vo, boolean logValue) {
        if (isLoggingTypeIn(vo, DataPointVO.LoggingTypes.ON_CHANGE, DataPointVO.LoggingTypes.ALL,
                DataPointVO.LoggingTypes.ON_TS_CHANGE)) {
            return logValue;
        }
        return true;
    }

    public static boolean isBackdated(PointValueTime newValue, PointValueState oldState, SetPointSource source) {
        return newValue != null && !oldState.isEmpty()
                && newValue.getTime() < oldState.getNewValue().getTime()
                && !isSetPointHandler(source);
    }

    public static boolean isSetPointHandler(SetPointSource source) {
        return source instanceof SetPointHandlerRT;
    }

    private static boolean isChange(PointValueTime newValue, PointValueState oldState,
                                    double toleranceOrigin, double tolerance, SetPointSource source) {
        if (oldState.isEmpty()) {
            return true;
        } else if (isBackdated(newValue, oldState, source)) {
            return false;
        } else {
            if (newValue.getValue() instanceof NumericValue) {
                double diff = toleranceOrigin - newValue.getDoubleValue();
                if (diff < 0)
                    diff = -diff;
                return diff > tolerance;
            }
            return !ObjectUtils.isEqual(newValue.getValue(), oldState.getNewValue().getValue());
        }
    }
}
