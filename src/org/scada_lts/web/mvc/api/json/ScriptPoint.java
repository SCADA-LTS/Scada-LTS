package org.scada_lts.web.mvc.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.scada_lts.web.beans.validation.xss.XssProtect;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptPoint {
    @XssProtect
    private String varName;
    @XssProtect
    private String dataPointXid;
    private int dataPointId;

    public ScriptPoint() {
    }

    public ScriptPoint(String varName, String dataPointXid) {
        this.varName = varName;
        this.dataPointXid = dataPointXid;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getDataPointXid() {
        return dataPointXid;
    }

    public void setDataPointXid(String dataPointXid) {
        this.dataPointXid = dataPointXid;
    }

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }
}
