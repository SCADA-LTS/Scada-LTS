package org.scada_lts.web.mvc.api.json;

public class ScriptPoint {
    private String varName;
    private String dataPointXid;

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
}
