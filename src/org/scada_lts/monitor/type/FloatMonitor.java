package org.scada_lts.monitor.type;

public class FloatMonitor extends ValueMonitor<Float> {
    private float value;

    public FloatMonitor(String id, String name) {
        this(id, name, 0.0F);
    }

    public FloatMonitor(String id, String name, float initialValue) {
        super(id, name);
        this.value = initialValue;
    }

    public Float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void addValue(float value) {
        this.value += value;
    }

    public void setValueIfGreater(float value) {
        if (this.value < value) {
            this.value = value;
        }

    }
}
