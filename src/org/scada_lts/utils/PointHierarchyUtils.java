package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.IHierarchyDAO;
import org.scada_lts.dao.IDataPointDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.recursive.CreatePointHierarchyTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class PointHierarchyUtils {

    private static final int TREE_DEPTH = 10;

    private PointHierarchyUtils() {}

    public static List<PointHierarchyNode> getPointHierarchyWithEmptyByKey(User user,
                                                                           IHierarchyDAO hierarchyDAO,
                                                                           IDataPointDAO dataPointDAO,
                                                                           int key)  {
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> folderNodes = getFolderNodes(hierarchyDAO);
        return getPointHierarchyNodes(key, pointNodes, folderNodes, true);
    }

    public static List<PointHierarchyNode> getPointHierarchyByKey(User user,
                                                                  IHierarchyDAO hierarchyDAO,
                                                                  IDataPointDAO dataPointDAO,
                                                                  int key)  {
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> folderNodes = getFolderNodes(hierarchyDAO);
        return getPointHierarchyNodes(key, pointNodes, folderNodes, false);
    }

    public static PointHierarchyNode getPointHierarchyWithEmptyRoot(User user, IHierarchyDAO hierarchyDAO, IDataPointDAO dataPointDAO)  {
        return getRootNode(user, hierarchyDAO, dataPointDAO);
    }

    public static PointHierarchyNode getPointHierarchyRoot(User user, IHierarchyDAO hierarchyDAO, IDataPointDAO dataPointDAO)  {
        PointHierarchyNode root = getRootNode(user, hierarchyDAO, dataPointDAO);
        cleanTree(root);
        return root;
    }

    public static List<PointHierarchyNode> getPointNodesWithAccess(User user, IDataPointDAO dataPointDAO) {
        List<DataPointVO> dataPoints = dataPointDAO.getDataPoints();
        List<PointHierarchyNode> pointNodes = new ArrayList<>();
        for (DataPointVO point : dataPoints) {
            if (Permissions.hasDataPointReadPermission(user, point)) {
                pointNodes.add(PointHierarchyNode.pointNode(point));
            }
        }
        sort(pointNodes);
        return pointNodes;
    }

    public static void sort(List<PointHierarchyNode> nodes) {
        for(PointHierarchyNode node: nodes) {
            sort(node);
        }
        nodes.sort(Comparator.comparing(PointHierarchyNode::getTitle));
    }

    private static void sort(PointHierarchyNode node) {
        if(Boolean.TRUE.equals(node.isFolder()))
            node.getChildren().sort(Comparator.comparing(PointHierarchyNode::getTitle));
    }

    private static void cleanTree(PointHierarchyNode root) {
        int treeDepth = TREE_DEPTH;
        while (isEmptyRoot(root, TREE_DEPTH) && treeDepth > 0) {
            removeEmpty(root, TREE_DEPTH);
            --treeDepth;
        }
    }

    private static void removeEmpty(PointHierarchyNode root, int treeDepth) {
        if(treeDepth < 0)
            return;
        List<PointHierarchyNode> toRemove = new ArrayList<>();
        int temp = --treeDepth;
        for (PointHierarchyNode node : root.getChildren()) {
            if (Boolean.TRUE.equals(node.isFolder())) {
                if (node.getChildren().isEmpty())
                    toRemove.add(node);
                else
                    removeEmpty(node, temp);
            }
        }
        root.getChildren().removeAll(toRemove);
    }

    private static boolean isEmptyRoot(PointHierarchyNode root, int treeDepth) {
        if(treeDepth < 0)
            return false;
        if(Boolean.TRUE.equals(root.isFolder()) && root.getChildren().isEmpty())
            return true;
        int temp = --treeDepth;
        for(PointHierarchyNode node: root.getChildren()) {
            if(Boolean.TRUE.equals(node.isFolder())) {
                if(node.getChildren().isEmpty() || isEmptyRoot(node, temp))
                    return true;
            }
        }
        return false;
    }

    private static boolean isNotEmpty(PointHierarchyNode root,
                                      List<PointHierarchyNode> nodes,
                                      int treeDepth) {
        if(treeDepth < 0)
            return true;
        int temp = --treeDepth;
        for(PointHierarchyNode node: nodes) {
            if(root.getKey() == node.getParentId()) {
                if(!Boolean.TRUE.equals(node.isFolder()))
                    return true;
                if(isNotEmpty(node, nodes, temp))
                    return true;
            }
        }
        return false;
    }

    private static List<PointHierarchyNode> getFolderNodes(IHierarchyDAO hierarchyDAO) {
        List<PointHierarchyNode> folderNodes =  hierarchyDAO.getHierarchy();
        if(folderNodes == null)
            return Collections.emptyList();
        for(PointHierarchyNode node: folderNodes) {
            node.setChildren(new ArrayList<>());
        }
        sort(folderNodes);
        return folderNodes;
    }

    private static List<PointHierarchyNode> getPointHierarchyNodes(int key,
                                                                   List<PointHierarchyNode> pointNodes,
                                                                   List<PointHierarchyNode> folderNodes,
                                                                   boolean withEmpty) {
        List<PointHierarchyNode> pointAndFolderNodes = new ArrayList<>();
        pointAndFolderNodes.addAll(folderNodes);
        pointAndFolderNodes.addAll(pointNodes);
        return pointAndFolderNodes.stream()
                .filter(node -> node.getParentId() == key)
                .filter(node -> filter(withEmpty, pointAndFolderNodes, node))
                .collect(Collectors.toList());
    }

    private static boolean filter(boolean withEmpty, List<PointHierarchyNode> pointAndFolderNodes, PointHierarchyNode node) {
        return !node.isFolder() || withEmpty || isNotEmpty(node, pointAndFolderNodes, TREE_DEPTH);
    }

    private static PointHierarchyNode getRootNode(User user, IHierarchyDAO hierarchyDAO, IDataPointDAO dataPointDAO) {
        List<PointHierarchyNode> folderNodes = getFolderNodes(hierarchyDAO);
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> pointAndFolderNodes = new ArrayList<>();
        pointAndFolderNodes.addAll(folderNodes);
        pointAndFolderNodes.addAll(pointNodes);
        PointHierarchyNode root = PointHierarchyNode.rootNode();
        CreatePointHierarchyTree createPointHierarchyTree = new CreatePointHierarchyTree(root, pointAndFolderNodes, TREE_DEPTH);
        try {
            createPointHierarchyTree.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return root;
    }
}
