package org.scada_lts.web.mvc.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @autor grzegorz.bylica@gmail.com on 23.03.2020
 */
public class PointValueDTO {

    private String tsStr;
    private String name;
    private String valueStr;

    public PointValueDTO() {
    }

    public PointValueDTO(long ts, String name, String value) {
        ;
        SimpleDateFormat DateFor = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        String stringDate= DateFor.format(new Date(ts));
        this.tsStr = stringDate;
        this.name = name;
        this.valueStr = value.toString();
    }

    public String getTsStr() {
        return tsStr;
    }

    public void setTsStr(String tsStr) {
        this.tsStr = tsStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointValueDTO that = (PointValueDTO) o;
        return Objects.equals(tsStr, that.tsStr) &&
                Objects.equals(name, that.name) &&
                Objects.equals(valueStr, that.valueStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tsStr, name, valueStr);
    }

    @Override
    public String toString() {
        return "PointValueDTO{" +
                "tsStr='" + tsStr + '\'' +
                ", name='" + name + '\'' +
                ", valueStr='" + valueStr + '\'' +
                '}';
    }
}
