package org.scada_lts.utils;

import com.serotonin.mango.rt.dataImage.PointLinkSetPointSource;
import com.serotonin.mango.rt.dataImage.PointValueState;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.RestApiSource;
import com.serotonin.mango.vo.User;
import com.serotonin.util.ObjectUtils;

public final class PointValueStateUtils {
    private static final double EPS = 1e-9;

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
                    if (newValue.getValue() instanceof NumericValue && vo.isToleranceAsPercentage()) {
                        double p = Math.max(0.0, vo.getTolerance()) / 100.0;
                        double cur = newValue.getDoubleValue();
                        return exceedsPercentSymmetric(toleranceOrigin, cur, p);
                    } else {
                        return isChange(newValue, oldState, toleranceOrigin, vo.getTolerance(), source);
                    }
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
                && !isSetPoint(source);
    }

    public static boolean isSetPoint(SetPointSource source) {
        return source instanceof SetPointHandlerRT || source instanceof User
                || source instanceof PointLinkSetPointSource || source instanceof RestApiSource;
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

    private static boolean exceedsPercentSymmetric(double last, double cur, double p) {
        if (p <= 0) {
            return Math.abs(cur - last) > 0.0;
        }
        if (Math.abs(last) < EPS || Math.abs(cur) < EPS) {
            return Math.abs(cur - last) > 0.0;
        }
        double ratio = Math.abs(cur / last);
        double up   = 1.0 + p;
        double down = 1.0 / (1.0 + p);
        return ratio > up || ratio < down;
    }

    public static boolean exceedsTolerance(DataPointVO vo, double last, double cur) {
        if (vo.isToleranceAsPercentage()) {
            double p = Math.max(0.0, vo.getTolerance()) / 100.0;
            return exceedsPercentSymmetric(last, cur, p);
        } else {
            return Math.abs(cur - last) > Math.max(0.0, vo.getTolerance());
        }
    }
}
