package org.scada_lts.dao.model.view;

import java.io.Serializable;

public class ViewDTO implements Serializable{

    private String name;
    private String xid;
    private String imagePath;
    private int size;

    public ViewDTO() {
    }

    public ViewDTO(String name, String xid, String imagePath, int size) {
        this.name = name;
        this.xid = xid;
        this.imagePath = imagePath;
        this.size = size;
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
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
}
