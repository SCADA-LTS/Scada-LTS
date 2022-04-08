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

    private PointValueState(PointValueTime newValue, PointValueState oldState, DataPointVO vo) {
        if(oldState != null && newValue != null) {
            this.newValue = newValue;
            this.oldValue = oldState.getNewValue();
            this.logValue = PointValueStateUtils.isLogValue(newValue, oldState, vo);
            this.toleranceOrigin = PointValueStateUtils.getToleranceOrigin(newValue, oldState, logValue);
            this.saveValue = PointValueStateUtils.isSaveValue(vo, logValue);
            this.backdated = PointValueStateUtils.isBackdated(newValue, oldState);
        } else if (newValue != null) {
            this.newValue = newValue;
            this.oldValue = null;
            this.logValue = PointValueStateUtils.isLogValue(newValue, null, vo);
            this.toleranceOrigin = PointValueStateUtils.getToleranceOrigin(newValue, null, logValue);
            this.saveValue = PointValueStateUtils.isSaveValue(vo, logValue);
            this.backdated = false;
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
        if(vo == null)
            throw new IllegalArgumentException(PointValueState.class.getName() + " - DataPointVO class object cannot be null.");
        if(newValue == null)
            throw new IllegalArgumentException(PointValueState.class.getName() + " - An object of class PointValueTime representing newValue cannot be null.");
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

    public double getToleranceOrigin() {
        return toleranceOrigin;
    }
}
