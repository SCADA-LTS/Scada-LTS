package org.scada_lts.service.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author  grzegorz.bylica@abilit.eu on 15.10.2019
 */
public class MultiChangeHistoryDTO {


    public transient final static int NEW_ID = -1;

    transient private int id;
    private String userName;
    private int userId;
    private long unixTime;
    private String interpretedState;
    private Set<ValuesMultiChangesHistoryDTO> values;

    public MultiChangeHistoryDTO(MultiChangeHistoryDTO mch) {
        this.userName = mch.userName;
        this.userId = mch.userId;
        this.unixTime = mch.unixTime;
        this.interpretedState = mch.interpretedState;
        values = new HashSet<>();
        this.values.addAll(mch.values);
    }

    public MultiChangeHistoryDTO() {
        this.id = NEW_ID;
        values = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public String getInterpretedState() {
        return interpretedState;
    }

    public void setInterpretedState(String interpretedState) {
        this.interpretedState = interpretedState;
    }

    public Set<ValuesMultiChangesHistoryDTO> getValues() {
        return values;
    }

    public void setValues(Set<ValuesMultiChangesHistoryDTO> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiChangeHistoryDTO that = (MultiChangeHistoryDTO) o;
        return id == that.id &&
                userId == that.userId &&
                unixTime == that.unixTime &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(interpretedState, that.interpretedState) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, userId, unixTime, interpretedState, values);
    }

    @Override
    public String toString() {
        return "MultiChangeHistoryDTO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", unixTime=" + unixTime +
                ", interpretedState='" + interpretedState + '\'' +
                ", values=" + values +
                '}';
    }
}
