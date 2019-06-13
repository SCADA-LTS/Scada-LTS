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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectoryInfo that = (DirectoryInfo) o;

        if (count != that.count) return false;
        return size == that.size;
    }

    @Override
    public int hashCode() {
        int result = count;
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DirectoryInfo{" +
                "count=" + count +
                ", size=" + size +
                '}';
    }
}
