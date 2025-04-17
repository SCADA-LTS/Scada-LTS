package org.scada_lts.web.mvc.api.dto;


import java.io.Serializable;

public class MoveObjectHierarchyDTO extends ObjectHierarchyDTO implements Serializable {

    private static final long serialVersionUID = -1L;
    private String destinationFolderXid;

    public String getDestinationFolderXid() {
        return destinationFolderXid;
    }

    public void setDestinationFolderXid(String destinationFolderXid) {
        this.destinationFolderXid = destinationFolderXid;
    }

    @Override
    public String toString() {
        return "destinationFolderXid=" + destinationFolderXid  + ", " + super.toString();
    }
}
