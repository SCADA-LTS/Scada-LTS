package org.scada_lts.web.mvc.api.json;

import com.serotonin.db.IntValuePair;

import java.util.List;
import java.util.Map;

public class JsonScript {

    private int id;
    private int userId;
    private String xid;
    private String name;
    private String script;
    private List<Map<String, String>> pointsOnContext;
    private String datapointContext;
    private String datasourceContext;

    public JsonScript() {}

    public JsonScript(int id, int userId, String xid, String name, String script) {
        this.id = id;
        this.userId = userId;
        this.xid = xid;
        this.name = name;
        this.script = script;
    }

    public JsonScript(int id, int userId, String xid, String name, String script, List<Map<String, String>> pointsOnContext, String datapointContext, String datasourceContext) {
        this.id = id;
        this.userId = userId;
        this.xid = xid;
        this.name = name;
        this.script = script;
        this.pointsOnContext = pointsOnContext;
        this.datapointContext = datapointContext;
        this.datasourceContext = datasourceContext;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public List<Map<String, String>> getPointsOnContext() {
        return pointsOnContext;
    }

    public void setPointsOnContext(List<Map<String, String>> pointsOnContext) {
        this.pointsOnContext = pointsOnContext;
    }
}
