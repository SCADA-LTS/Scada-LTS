package org.scada_lts.web.mvc.api.dto;

import java.io.Serializable;
import java.util.List;

public class DeleteObjectHierarchyDTO extends ObjectHierarchyDTO implements Serializable {

    private static final long serialVersionUID = -1L;
    private List<ObjectHierarchy> deleteObjects;

    public List<ObjectHierarchy> getDeleteObjects() {
        return deleteObjects;
    }

    public void setDeleteObjects(List<ObjectHierarchy> deleteObjects) {
        this.deleteObjects = deleteObjects;
    }

    @Override
    public String toString() {
        return "deleteObjects=" + deleteObjects + ", " + super.toString();
    }
}
