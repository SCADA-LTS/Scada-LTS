package org.scada_lts.serorepl.monitor;


public abstract class ValMonitor<T> {

    private final String id;
    private final String name;

    public ValMonitor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public int intValue() {
        throw new RuntimeException("Method not implemented");
    }

    public long longValue() {
        throw new RuntimeException("Method not implemented");
    }

    public float floatValue() {
        throw new RuntimeException("Method not implemented");
    }

    public double doubleValue() {
        throw new RuntimeException("Method not implemented");
    }

    public String stringValue() {
        throw new RuntimeException("Method not implemented");
    }


}
