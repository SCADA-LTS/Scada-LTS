package org.scada_lts.dao.model.synopticpanel;

import java.io.Serializable;

public class SynopticPanelDTO implements Serializable {

    private int id;
    private String name;
    private String xid;
    private String vectorImage;
    private String componentData;

    public SynopticPanelDTO() {

    }

    public SynopticPanelDTO(int id, String name, String xid, String vectorImage, String componentData) {
        this.id = id;
        this.name = name;
        this.xid = xid;
        this.vectorImage = vectorImage;
        this.componentData = componentData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
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