package org.scada_lts.web.mvc.api.dto;

import java.util.List;

public class PointHierarchyDTO {
    private List<String> xids;
    private String newParentIdFolder;
    private List<String> childrenXids;


    public List<String> getChildrenXids() {
        return childrenXids;
    }

    public void setChildrenXids(List<String> childrenXids) {
        this.childrenXids = childrenXids;
    }

    public List<String> getXids() {
        return xids;
    }

    public void setXids(List<String> xids) {
        this.xids = xids;
    }

    public String getNewParentIdFolder() {
        return newParentIdFolder;
    }

    public void setNewParentIdFolder(String newParentIdFolder) {
        this.newParentIdFolder = newParentIdFolder;
    }

    @Override
    public String toString() {
        return "PointHierarchyDTO [XIDs=" + xids + ", newParentXidFolder=" + newParentIdFolder + "]";
    }
}
