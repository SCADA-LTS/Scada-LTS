package com.serotonin.timer;


public class SimulationTimeSource implements TimeSource {
    private long time;

    public long currentTimeMillis() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
