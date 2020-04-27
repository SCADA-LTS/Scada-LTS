package org.scada_lts.dao.storungsAndAlarms;

class PointValues_ {

    private int id;

    private String dataPointId,dataType,pointValue,ts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(String dataPointId) {
        this.dataPointId = dataPointId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getPointValue() {
        return pointValue;
    }

    public void setPointValue(String pointValue) {
        this.pointValue = pointValue;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
