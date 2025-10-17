package org.scada_lts.monitor.type;

public class DoubleMonitor extends ValueMonitor<Double> {
    private double value;

    public DoubleMonitor(String id, String name) {
        this(id, name, 0.0);
    }

    public DoubleMonitor(String id, String name, double initialValue) {
        super(id, name);
        this.value = initialValue;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addValue(double value) {
        this.value += value;
    }

    public void setValueIfGreater(double value) {
        if (this.value < value) {
            this.value = value;
        }

    }
}
