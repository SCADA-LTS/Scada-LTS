package org.scada_lts.dao.pointvalues;

public class DataPointSimpleValue {
    int pointId;
    double value;
    long timestamp;

    DataPointSimpleValue(int pointId, double value, long timestamp) {
        this.pointId = pointId;
        this.value = value;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}