package org.scada_lts.web.mvc.api.dto;


import java.io.Serializable;

public class MoveObjectHierarchyDTO extends ObjectHierarchyDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private String parentFolderXid;

    public String getParentFolderXid() {
        return parentFolderXid;
    }

    public void setParentFolderXid(String parentFolderXid) {
        this.parentFolderXid = parentFolderXid;
    }

    @Override
    public String toString() {
        return "parentFolderXid=" + parentFolderXid + ", " + super.toString();
    }
}
