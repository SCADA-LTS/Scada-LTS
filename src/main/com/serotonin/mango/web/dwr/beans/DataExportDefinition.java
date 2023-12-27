package com.serotonin.mango.web.dwr.beans;

import org.joda.time.DateTime;

public class DataExportDefinition {
    private final int[] pointIds;
    private final DateTime from;
    private final DateTime to;

    public DataExportDefinition(int[] pointIds, DateTime from, DateTime to) {
        this.pointIds = pointIds;
        this.from = from;
        this.to = to;
    }

    public int[] getPointIds() {
        return pointIds;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }
}
