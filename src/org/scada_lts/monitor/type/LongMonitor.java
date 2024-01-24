package org.scada_lts.monitor.type;

public class LongMonitor extends ValueMonitor<Long> {
    private long value;

    public LongMonitor(String id, String name) {
        this(id, name, 0L);
    }

    public LongMonitor(String id, String name, long initialValue) {
        super(id, name);
        this.value = initialValue;
    }

    public Long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void addValue(long value) {
        this.value += value;
    }

    public void setValueIfGreater(long value) {
        if (this.value < value) {
            this.value = value;
        }

    }
}
