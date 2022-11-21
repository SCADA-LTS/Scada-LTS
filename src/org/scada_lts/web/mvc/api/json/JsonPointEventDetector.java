package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonPointEventDetector implements Serializable {
    private int id;
    private String xid;
    private String alias;
    private int detectorType;
    private int alarmLevel;
    private double limit;
    private int duration;
    private int durationType;
    private boolean binaryState;
    private int multistateState;
    private int changeCount;
    private String alphanumericState;
    private double weight;

    public JsonPointEventDetector(int id, String xid, String alias, int detectorType, int alarmLevel, double limit, int duration, int durationType, boolean binaryState, int multistateState, int changeCount, String alphanumericState, double weight) {
        this.id = id;
        this.xid = xid;
        this.alias = alias;
        this.detectorType = detectorType;
        this.alarmLevel = alarmLevel;
        this.limit = limit;
        this.duration = duration;
        this.durationType = durationType;
        this.binaryState = binaryState;
        this.multistateState = multistateState;
        this.changeCount = changeCount;
        this.alphanumericState = alphanumericState;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(int detectorType) {
        this.detectorType = detectorType;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDurationType() {
        return durationType;
    }

    public void setDurationType(int durationType) {
        this.durationType = durationType;
    }

    public boolean isBinaryState() {
        return binaryState;
    }

    public void setBinaryState(boolean binaryState) {
        this.binaryState = binaryState;
    }

    public int getMultistateState() {
        return multistateState;
    }

    public void setMultistateState(int multistateState) {
        this.multistateState = multistateState;
    }

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }

    public String getAlphanumericState() {
        return alphanumericState;
    }

    public void setAlphanumericState(String alphanumericState) {
        this.alphanumericState = alphanumericState;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
