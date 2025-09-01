package org.scada_lts.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.vo.TimePeriod;

public class ArchiveTask {

    private int ageValue;
    @JsonDeserialize(using = com.serotonin.mango.vo.TimePeriodDeserializer.class)
    private TimePeriod ageUnit;
    private ArchiveFunction function;
    private String table;

    @JsonCreator
    public ArchiveTask(
            @JsonProperty("ageValue") int ageValue,
            @JsonProperty("ageUnit") TimePeriod ageUnit,
            @JsonProperty("function") ArchiveFunction function,
            @JsonProperty("table") String table) {
        this.ageValue = ageValue;
        this.ageUnit = ageUnit;
        this.function = function;
        this.table = table;
    }

    public ArchiveTask() {}

    public int getAgeValue() {
        return ageValue;
    }

    public void setAgeValue(int ageValue) {
        this.ageValue = ageValue;
    }

    public TimePeriod getAgeUnit() {
        return ageUnit;
    }

    public void setAgeUnit(TimePeriod ageUnit) {
        this.ageUnit = ageUnit;
    }

    public ArchiveFunction getFunction() {
        return function;
    }

    public void setFunction(ArchiveFunction function) {
        this.function = function;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
