package org.scada_lts.dao.storungsAndAlarms;

class PointValuesStorungsAndAlarms {

    private int id;

    private String
            pointId,
            pointXid,
            pointType,
            pointName,
            triggerName,
            inactiveTime,
            acknowledgeTime,
            lastpointValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getPointXid() {
        return pointXid;
    }

    public void setPointXid(String pointXid) {
        this.pointXid = pointXid;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getInactiveTime() {
        return inactiveTime;
    }

    public void setInactiveTime(String inactiveTime) {
        this.inactiveTime = inactiveTime;
    }

    public String getAcknowledgeTime() {
        return acknowledgeTime;
    }

    public void setAcknowledgeTime(String acknowledgeTime) {
        this.acknowledgeTime = acknowledgeTime;
    }

    public String getLastpointValue() {
        return lastpointValue;
    }

    public void setLastpointValue(String lastpointValue) {
        this.lastpointValue = lastpointValue;
    }

    @Override
    public String toString() {
        return "PointValuesStorungsAndAlarms{" +
                "id=" + id +
                ", pointId='" + pointId + '\'' +
                ", pointXid='" + pointXid + '\'' +
                ", pointType='" + pointType + '\'' +
                ", pointName='" + pointName + '\'' +
                ", triggerName='" + triggerName + '\'' +
                ", inactiveTime='" + inactiveTime + '\'' +
                ", acknowledgeTime='" + acknowledgeTime + '\'' +
                ", lastpointValue='" + lastpointValue + '\'' +
                '}';
    }
}
