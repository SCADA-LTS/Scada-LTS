package org.scada_lts.service.model;

import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;

public class SynopticPanel {

    public static final String XID_PREFIX = "SP_";

    private int id = Common.NEW_ID;
    //    @JsonRemoteProperty
    private String xid;
    //    @JsonRemoteProperty
    private String name;
    //    @JsonRemoteProperty
    private String vectorImage;
    private String componentData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getVectorImage() {
        return vectorImage;
    }

    public void setVectorImage(String vectorImage) {
        this.vectorImage = vectorImage;
    }

    public String getComponentData() {
        return componentData;
    }

    public void setComponentData(String componentData) {
        this.componentData = componentData;
    }
}