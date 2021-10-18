package org.scada_lts.dao;

import java.util.List;

public class SQLPageWithTotal<T> {
    private List<T> page;
    private int total;

    public SQLPageWithTotal() {}

    public SQLPageWithTotal(List<T> page, int total) {
        this.page = page;
        this.total = total;
    }

    public List<T> getPage() {
        return page;
    }

    public void setPage(List<T> page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
