package org.scada_lts.web.mvc.api.dto;

import java.io.Serializable;
import java.util.List;

public class DeleteObjectHierarchyDTO extends ObjectHierarchyDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<ObjectHierarchy> childrenMoveObjects;

    public List<ObjectHierarchy> getChildrenMoveObjects() {
        return childrenMoveObjects;
    }

    public void setChildrenMoveObjects(List<ObjectHierarchy> childrenMoveObjects) {
        this.childrenMoveObjects = childrenMoveObjects;
    }

    @Override
    public String toString() {
        return "childrenObjectsXidList=" + childrenMoveObjects + ", " + super.toString();
    }
}
