package org.scada_lts.web.mvc.api.dto;


import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.io.Serializable;

public class MoveObjectHierarchyDTO extends ObjectHierarchyDTO implements Serializable {

    private static final long serialVersionUID = -1L;
    @XssProtect
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
