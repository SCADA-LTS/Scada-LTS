package org.scada_lts.utils;

import java.util.List;

public class SQLPageWithTotal<T> {
    private List<T> rows;
    private int total;

    public SQLPageWithTotal(List<T> rows, int total) {
        this.rows = rows;
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}