package org.scada_lts.serorepl.utils;

import java.io.Serializable;

public class DirectoryInfo implements Serializable{

    private static final long serialVersionUID = -4451404084448543201L;
    private int count;
    private long size;



    public DirectoryInfo() {
    }

    public int getCount() {
        return this.count;
    }

    public long getSize() {
        return this.size;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
