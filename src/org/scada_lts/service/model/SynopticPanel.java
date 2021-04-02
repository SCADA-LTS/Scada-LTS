package org.scada_lts.service.model;

import com.serotonin.mango.Common;

public class SynopticPanel {

    public static final String XID_PREFIX = "SP_";

    private int id = Common.NEW_ID;
    private String xid;
    private String name;
    private String vectorImage;
    private String componentData;

    public SynopticPanel() {}

    public SynopticPanel(int id, String xid, String name, String vectorImage, String componentData) {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.vectorImage = vectorImage;
        this.componentData = componentData;
    }

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