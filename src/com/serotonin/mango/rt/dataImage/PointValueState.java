package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.utils.PointValueStateUtils;

public class PointValueState {

    private final PointValueTime newValue;
    private final PointValueTime oldValue;
    /**
     * This is the value around which tolerance decisions will be made when
     * determining whether to log numeric values.
     */
    private final double toleranceOrigin;
    private final boolean logValue;
    private final boolean saveValue;
    private final boolean backdated;
    private static final PointValueState EMPTY = new PointValueState(null, null, null, null);

    private PointValueState(PointValueTime newValue, PointValueState oldState, DataPointVO vo, SetPointSource source) {
        if(oldState != null && newValue != null) {
            this.newValue = newValue;
            this.oldValue = oldState.getNewValue();
            this.logValue = PointValueStateUtils.isLogValue(newValue, oldState, vo, source);
            this.toleranceOrigin = PointValueStateUtils.getToleranceOrigin(newValue, oldState, logValue);
            this.saveValue = PointValueStateUtils.isSaveValue(vo, logValue);
            this.backdated = PointValueStateUtils.isBackdated(newValue, oldState, source);
        } else {
            this.newValue = null;
            this.oldValue = null;
            this.toleranceOrigin = 0.0;
            this.logValue = false;
            this.saveValue = false;
            this.backdated = false;
        }
    }

    public static PointValueState newState(PointValueTime newValue, PointValueState oldState, DataPointVO vo) {
        return newState(newValue, oldState, vo, null);
    }

    public static PointValueState newState(PointValueTime newValue, PointValueState oldState, DataPointVO vo, SetPointSource source) {
        if(vo == null)
            throw new IllegalArgumentException(PointValueState.class.getName() + " - DataPointVO class object cannot be null.");
        if(newValue != null && oldState == null)
            throw new IllegalArgumentException(PointValueState.class.getName() + " - An object of class PointValueTime representing newValue is not null then oldState cannot be null.");
        if(newValue == null)
            return empty();
        return new PointValueState(newValue, oldState, vo, source);
    }

    public static PointValueState empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return this == EMPTY;
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

    public double getToleranceOrigin() {
        return toleranceOrigin;
    }
}
