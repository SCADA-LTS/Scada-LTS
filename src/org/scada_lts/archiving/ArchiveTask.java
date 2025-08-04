package org.scada_lts.archiving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.TimePeriodType;

public class ArchiveTask {

    private int ageValue;
    private TimePeriodType ageUnit;
    private ArchiveFunction function;
    private String table;

    @JsonCreator
    public ArchiveTask(
            @JsonProperty("ageValue") int ageValue,
            @JsonProperty("ageUnit") TimePeriodType ageUnit,
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

    public TimePeriodType getAgeUnit() {
        return ageUnit;
    }

    public void setAgeUnit(TimePeriodType ageUnit) {
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
