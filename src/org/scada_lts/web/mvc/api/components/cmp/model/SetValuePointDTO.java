package org.scada_lts.web.mvc.api.components.cmp.model;

import java.io.Serializable;

/**
 * @author grzegorz.bylica@gmail.com on 17.01.19
 */
public class SetValuePointDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String xid;
    private String value;
    private String resultOperationSave;
    private String error;

    public SetValuePointDTO() {
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getResultOperationSave() {
        return resultOperationSave;
    }

    public void setResultOperationSave(String resultOperationSave) {
        this.resultOperationSave = resultOperationSave;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
