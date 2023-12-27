package org.scada_lts.web.mvc.api.json;


import java.util.ArrayList;
import java.util.List;

public class ThreadInfoList<T> {

    private final int size;
    private final List<T> threadInfoList;

    public ThreadInfoList(List<T> threadInfoList) {
        this.threadInfoList = threadInfoList;
        this.size = threadInfoList.size();
    }

    public ThreadInfoList(int size) {
        this.threadInfoList = new ArrayList<>();
        this.size = size;
    }

    public List<T> getThreadInfoList() {
        return threadInfoList;
    }

    public int getSize() {
        return size;
    }
}

