package org.scada_lts.monitor.type;


public abstract class ValueMonitor<T> {
    private static final long serialVersionUID = 3992668527962817285L;
    private final String id;
    private final String name;

    public ValueMonitor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        throw new UnsupportedOperationException();
    }
}