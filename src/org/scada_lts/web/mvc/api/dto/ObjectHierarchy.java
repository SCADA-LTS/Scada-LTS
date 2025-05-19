package org.scada_lts.web.mvc.api.dto;

import org.scada_lts.dao.pointhierarchy.PointHierarchyXidDAO;

import java.io.Serializable;

public class ObjectHierarchy implements Serializable {

    private static final long serialVersionUID = -1L;

    private ObjectHierarchyType type;
    private String xid;

    public ObjectHierarchyType getType() {
        return type;
    }

    public void setType(ObjectHierarchyType type) {
        this.type = type;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public boolean isPoint() {
        return type == ObjectHierarchyType.POINT;
    }

    public boolean isFolder() {
        return type == ObjectHierarchyType.FOLDER;
    }

    @Override
    public String toString() {
        return "type=" + type + ", xid='" + xid;
    }

    public boolean move(String destinationFolderXid, PointHierarchyXidDAO pointHierarchyXidDAO) {
        return type.move(xid, destinationFolderXid, pointHierarchyXidDAO);
    }
}
