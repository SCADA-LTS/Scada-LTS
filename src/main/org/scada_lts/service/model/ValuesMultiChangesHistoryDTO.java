package org.scada_lts.service.model;

import java.util.Objects;

/**
 * @author grzegorz.bylica@abilit.eu on 15.10.2019
 */
public class ValuesMultiChangesHistoryDTO {
    private String xidPoint;
    private String value;

    public String getXidPoint() {
        return xidPoint;
    }

    public void setXidPoint(String xidPoint) {
        this.xidPoint = xidPoint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValuesMultiChangesHistoryDTO that = (ValuesMultiChangesHistoryDTO) o;
        return Objects.equals(xidPoint, that.xidPoint) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xidPoint, value);
    }

    @Override
    public String toString() {
        return "ValuesMultiChangesHistoryDTO{" +
                "xidPoint='" + xidPoint + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
