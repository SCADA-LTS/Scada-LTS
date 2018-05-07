package org.scada_lts.serorepl.monitor;

import org.scada_lts.serorepl.exceptions.NotImplementedException;

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
        throw new NotImplementedException();
    }

    public long longValue() {
        throw new NotImplementedException();
    }

    public float floatValue() {
        throw new NotImplementedException();
    }

    public double doubleValue() {
        throw new NotImplementedException();
    }

    public String stringValue() {
        throw new NotImplementedException();
    }


}
