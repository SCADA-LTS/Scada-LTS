package org.scada_lts.web.mvc.api.components.cmp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SetValueErrorsDTO implements Serializable {

    private List<PointError> errors;

    public SetValueErrorsDTO() {
        this.errors = new ArrayList<PointError>();
    }

    public List<PointError> getErrors() {
        return errors;
    }

    public void setErrors(List<PointError> errors) {
        this.errors = errors;
    }

    public static class PointError implements Serializable {
        private String xid;
        private String name;
        private String message;

        public PointError() {}

        public PointError(String xid, String name, String message) {
            this.xid = xid;
            this.name = name;
            this.message = message;
        }

        public String getXid() {
            return xid;
        }

        public void setXid(String xid) {
            this.xid = xid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
