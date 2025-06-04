package org.scada_lts.dao;

import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.dao.model.viewshierarchy.ViewInViewHierarchyNode;

import java.util.List;

public interface IViewHierarchyDAO extends GenericHierarchyDAO<ViewHierarchyNode> {
    List<ViewHierarchyNode> getAll();

    List<ViewHierarchyNode> getNode(long l);

    List<ViewInViewHierarchyNode> getViewInHierarchyNode();

    int add(ViewHierarchyNode node);

    int update(ViewHierarchyNode node);

    int moveFolder(int id, int newParentId);

    int moveView(int id, int newParentId);

    int delView(int id);

    int delFolder(int id);
}
