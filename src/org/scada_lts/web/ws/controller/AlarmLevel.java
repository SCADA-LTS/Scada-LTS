package org.scada_lts.web.ws.controller;

import java.io.Serializable;

public class AlarmLevel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String   name = "";
    private boolean  isTerminated = false;
    private int      level;
    
    public AlarmLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
