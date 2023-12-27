package org.scada_lts.serorepl.monitor;

public class IntMonitor extends ValMonitor<Integer>{

    private static final int DEFAULT_INITIAL_VALUE = 0;
    protected int value;

    public IntMonitor(String id, String name){
        this(id, name, DEFAULT_INITIAL_VALUE);
    }

    public IntMonitor(String id, String name, int value) {
        super(id, name);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
