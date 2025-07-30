package org.scada_lts.archiving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.temporal.ChronoUnit;

public class ArchivalTask {

    private int ageValue;
    private ChronoUnit ageUnit;
    private ArchivalFunction function;
    private String table;

    @JsonCreator
    public ArchivalTask(
            @JsonProperty("ageValue") int ageValue,
            @JsonProperty("ageUnit") ChronoUnit ageUnit,
            @JsonProperty("function") ArchivalFunction function,
            @JsonProperty("table") String table) {
        this.ageValue = ageValue;
        this.ageUnit = ageUnit;
        this.function = function;
        this.table = table;
    }

    public ArchivalTask() {}

    public int getAgeValue() {
        return ageValue;
    }

    public void setAgeValue(int ageValue) {
        this.ageValue = ageValue;
    }

    public ChronoUnit getAgeUnit() {
        return ageUnit;
    }

    public void setAgeUnit(ChronoUnit ageUnit) {
        this.ageUnit = ageUnit;
    }

    public ArchivalFunction getFunction() {
        return function;
    }

    public void setFunction(ArchivalFunction function) {
        this.function = function;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
