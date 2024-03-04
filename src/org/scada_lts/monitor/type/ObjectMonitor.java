package org.scada_lts.monitor.type;

public class ObjectMonitor<T> extends ValueMonitor<T> {
    private T value;

    public ObjectMonitor(String id, String name) {
        this(id, name, null);
    }

    public ObjectMonitor(String id, String name, T initialValue) {
        super(id, name);
        this.value = initialValue;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String stringValue() {
        return this.value == null ? null : this.value.toString();
    }
}
