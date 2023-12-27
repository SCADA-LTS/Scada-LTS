package org.scada_lts.dao.model.multichangehistory;

import java.util.Objects;

/**
 * @author  grzegorz.bylica@abilit.eu on 16.10.2019
 */
public class MultiChangeHistoryValues {

    private int id;
    private int userId;
    private String userName;
    private String viewAndCmpIdentyfication;
    private String interpretedState;
    private long timeStamp;
    private int valueId;
    private String value;
    private int dataPointId;
    private String xidPoint;

    public MultiChangeHistoryValues() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getViewAndCmpIdentyfication() {
        return viewAndCmpIdentyfication;
    }

    public void setViewAndCmpIdentyfication(String viewAndCmpIdentyfication) {
        this.viewAndCmpIdentyfication = viewAndCmpIdentyfication;
    }

    public String getInterpretedState() {
        return interpretedState;
    }

    public void setInterpretedState(String interpretedState) {
        this.interpretedState = interpretedState;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }

    public String getXidPoint() {
        return xidPoint;
    }

    public void setXidPoint(String xidPoint) {
        this.xidPoint = xidPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiChangeHistoryValues that = (MultiChangeHistoryValues) o;
        return id == that.id &&
                userId == that.userId &&
                valueId == that.valueId &&
                dataPointId == that.dataPointId &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(viewAndCmpIdentyfication, that.viewAndCmpIdentyfication) &&
                Objects.equals(interpretedState, that.interpretedState) &&
                Objects.equals(timeStamp, that.timeStamp) &&
                Objects.equals(value, that.value) &&
                Objects.equals(xidPoint, that.xidPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, userName, viewAndCmpIdentyfication, interpretedState, timeStamp, valueId, value, dataPointId, xidPoint);
    }

    @Override
    public String toString() {
        return "MultiChangeHistoryValues{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", viewAndCmpIdentyfication='" + viewAndCmpIdentyfication + '\'' +
                ", interpretedState='" + interpretedState + '\'' +
                ", ts=" + timeStamp +
                ", valueId=" + valueId +
                ", value='" + value + '\'' +
                ", dataPointId=" + dataPointId +
                ", xidPoint='" + xidPoint + '\'' +
                '}';
    }
}
