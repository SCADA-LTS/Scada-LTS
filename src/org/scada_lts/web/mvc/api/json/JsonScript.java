package org.scada_lts.web.mvc.api.json;

import java.util.List;

public class JsonScript {

    private Integer id;
    private Integer userId;
    private String xid;
    private String name;
    private String script;
    private List<ScriptPoint> pointsOnContext;
    private String datapointContext;
    private String datasourceContext;

    public JsonScript() {}

    public JsonScript(Integer id, Integer userId, String xid, String name, String script, List<ScriptPoint> pointsOnContext, String datapointContext, String datasourceContext) {
        this.id = id;
        this.userId = userId;
        this.xid = xid;
        this.name = name;
        this.script = script;
        this.pointsOnContext = pointsOnContext;
        this.datapointContext = datapointContext;
        this.datasourceContext = datasourceContext;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<ScriptPoint> getPointsOnContext() {
        return pointsOnContext;
    }

    public void setPointsOnContext(List<ScriptPoint> pointsOnContext) {
        this.pointsOnContext = pointsOnContext;
    }

    public String getDatapointContext() {
        return datapointContext;
    }

    public void setDatapointContext(String datapointContext) {
        this.datapointContext = datapointContext;
    }

    public String getDatasourceContext() {
        return datasourceContext;
    }

    public void setDatasourceContext(String datasourceContext) {
        this.datasourceContext = datasourceContext;
    }
}
