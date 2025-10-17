package org.scada_lts.monitor.type;

public class IntegerMonitor extends ValueMonitor<Integer> {
    protected int value;

    public IntegerMonitor(String id, String name) {
        this(id, name, 0);
    }

    public IntegerMonitor(String id, String name, int initialValue) {
        super(id, name);
        this.value = initialValue;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    public void setValueIfGreater(int value) {
        if (this.value < value) {
            this.value = value;
        }

    }
}
