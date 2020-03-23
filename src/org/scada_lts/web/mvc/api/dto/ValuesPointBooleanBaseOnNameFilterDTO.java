package org.scada_lts.web.mvc.api.dto;

import com.serotonin.bacnet4j.type.constructed.DateTime;

import java.util.Objects;

/**
 * @autor grzegorz.bylica@gmail.com on 23.03.2020
 */
public class ValuesPointBooleanBaseOnNameFilterDTO {

    private long ts;
    private String name;
    private Boolean value;

    public ValuesPointBooleanBaseOnNameFilterDTO() {
    }

    public ValuesPointBooleanBaseOnNameFilterDTO(long ts, String name, Boolean value) {
        this.ts = ts;
        this.name = name;
        this.value = value;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValuesPointBooleanBaseOnNameFilterDTO that = (ValuesPointBooleanBaseOnNameFilterDTO) o;
        return Objects.equals(ts, that.ts) &&
                Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ts, name, value);
    }

    @Override
    public String toString() {
        return "ValuesPointBooleanBaseOnNameFilterDTO{" +
                "ts=" + ts +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
