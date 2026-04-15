package org.scada_lts.dao;

import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.dao.model.viewshierarchy.ViewInViewHierarchyNode;

import java.util.List;

public interface IViewHierarchyDAO extends GenericHierarchyDAO<ViewHierarchyNode> {
    int ROOT_ID = -1;

    List<ViewInViewHierarchyNode> getViewInHierarchyNode();
}
