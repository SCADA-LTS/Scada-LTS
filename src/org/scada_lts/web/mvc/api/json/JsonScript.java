package org.scada_lts.web.mvc.api.json;

public class JsonScript {

    private int id;
    private int userId;
    private String xid;
    private String name;
    private String script;

    public JsonScript() {}

    public JsonScript(int id, int userId, String xid, String name, String script) {
        this.id = id;
        this.userId = userId;
        this.xid = xid;
        this.name = name;
        this.script = script;
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
}
