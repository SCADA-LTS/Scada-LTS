package org.scada_lts.web.mvc.api.components.cmp.model;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.modbus4j.code.DataType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author grzegorz.bylica@gmail.com on 17.01.19
 */
public class ReadValuePointDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String value;
    private Long ts;
    private String name;
    private String xid;
    private String type;
    private String error;

    public ReadValuePointDTO(String xid) {
        this.xid = xid;
    }

    public void set(PointValueTime pvt, DataPointVO dpvo) {
        if (pvt == null ||  pvt.getValue() == null) {
            setValue( MangoValue.stringToValue("0", DataType.BINARY));
            setTs(new Date().getTime());
        } else {
            setValue(pvt.getValue());
            setTs(pvt.getTime());
        }
        setName(dpvo.getName());
        setXid(dpvo.getXid());
    }

    public String getValue() {
        return value;
    }

    public void setValue(MangoValue value) {
        if (value instanceof AlphanumericValue) {
            this.value = ((AlphanumericValue) value).getStringValue();
            setType("AlphanumericValue");
        } else if (value instanceof BinaryValue) {
            this.value = String.valueOf(((BinaryValue) value).getBooleanValue());
            setType("BinaryValue");
        } else if (value instanceof ImageValue) {
            this.value = ((ImageValue) value).getFilename();
            setType("ImageValue");
        } else if (value instanceof MultistateValue) {
            this.value = String.valueOf(((MultistateValue) value).getIntegerValue());
            setType("MultistateValue");
        } else if (value instanceof NumericValue) {
            this.value = String.valueOf(((NumericValue) value).getFloatValue());
            setType("NumericValue");
        }
        //error
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
