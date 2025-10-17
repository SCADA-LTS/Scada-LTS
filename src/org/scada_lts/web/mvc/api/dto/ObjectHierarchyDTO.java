package org.scada_lts.web.mvc.api.dto;

import java.io.Serializable;
import java.util.List;

public class ObjectHierarchyDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<ObjectHierarchy> moveObjects;

    public List<ObjectHierarchy> getMoveObjects() {
        return moveObjects;
    }

    public void setMoveObjects(List<ObjectHierarchy> moveObjects) {
        this.moveObjects = moveObjects;
    }

    @Override
    public String toString() {
        return "moveObjects=" + moveObjects;
    }
}
