package org.scada_lts.web.mvc.api.components.cmp.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @autor grzegorz.bylica@gmail.com on 17.01.19
 */
public class SetValuePointDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String xid;
    private boolean value;
    private boolean resultOperationSave;
    private String error;

    public SetValuePointDTO() {
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isResultOperationSave() {
        return resultOperationSave;
    }

    public void setResultOperationSave(boolean resultOperationSave) {
        this.resultOperationSave = resultOperationSave;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
