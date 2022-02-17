package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.HierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class PointHierarchyUtils {

    private PointHierarchyUtils() {}

    public static List<PointHierarchyNode> getPointHierarchyWithEmptyByKey(User user,
                                                                           HierarchyDAO hierarchyDAO,
                                                                           DataPointDAO dataPointDAO,
                                                                           int key)  {
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> folderNodes = getFolderNodes(hierarchyDAO);
        return getPointHierarchyNodes(key, pointNodes, folderNodes, true);
    }

    public static List<PointHierarchyNode> getPointHierarchyByKey(User user,
                                                                  HierarchyDAO hierarchyDAO,
                                                                  DataPointDAO dataPointDAO,
                                                                  int key)  {
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> folderNodes = getFolderNodes(hierarchyDAO);
        return getPointHierarchyNodes(key, pointNodes, folderNodes, false);
    }

    public static PointHierarchyNode getPointHierarchyWithEmptyRoot(User user, HierarchyDAO hierarchyDAO, DataPointDAO dataPointDAO)  {
        return getRootNode(user, hierarchyDAO, dataPointDAO);
    }

    public static PointHierarchyNode getPointHierarchyRoot(User user, HierarchyDAO hierarchyDAO, DataPointDAO dataPointDAO)  {
        PointHierarchyNode root = getRootNode(user, hierarchyDAO, dataPointDAO);
        cleanTree(root);
        return root;
    }

    public static List<PointHierarchyNode> getPointNodesWithAccess(User user, DataPointDAO dataPointDAO) {
        List<DataPointVO> dataPoints = dataPointDAO.getDataPoints();
        List<PointHierarchyNode> pointNodes = new ArrayList<>();
        for (DataPointVO point : dataPoints) {
            if (Permissions.hasDataPointReadPermission(user, point)) {
                pointNodes.add(PointHierarchyNode.pointNode(point));
            }
        }
        return pointNodes;
    }

    public static void sort(List<PointHierarchyNode> nodes) {
        for(PointHierarchyNode node: nodes) {
            sort(node);
        }
    }

    private static void sort(PointHierarchyNode node) {
        if(node.isFolder())
            node.getChildren().sort(Comparator.comparing(PointHierarchyNode::getTitle));
    }

    private static void createTree(PointHierarchyNode root,
                                  List<PointHierarchyNode> pointAndFolderNodes) {
        for(PointHierarchyNode node: pointAndFolderNodes) {
            if(root.getKey() == node.getParentId()) {
                root.getChildren().add(node);
                if(node.isFolder()) {
                    createTree(node, pointAndFolderNodes);
                }
            }
        }
        sort(root);
    }

    private static void cleanTree(PointHierarchyNode root) {
        int safe = 10;
        while (isEmpty(root) && safe > 0) {
            removeEmpty(root);
            --safe;
        }
    }

    private static void removeEmpty(PointHierarchyNode root) {
        List<PointHierarchyNode> toRemove = new ArrayList<>();
        for (PointHierarchyNode node : root.getChildren()) {
            if (node.isFolder()) {
                if (node.getChildren().isEmpty())
                    toRemove.add(node);
                else
                    removeEmpty(node);
            }
        }
        root.getChildren().removeAll(toRemove);
    }

    private static boolean isEmpty(PointHierarchyNode root) {
        for(PointHierarchyNode node: root.getChildren()) {
            if(node.isFolder()) {
                if(node.getChildren().isEmpty())
                    return true;
                return isEmpty(node);
            }
        }
        return false;
    }

    private static boolean isEmpty(PointHierarchyNode root, List<PointHierarchyNode> nodes) {
        for(PointHierarchyNode node: nodes) {
            if(root.getKey() == node.getParentId()) {
                return false;
            }
        }
        return true;
    }

    private static List<PointHierarchyNode> getFolderNodes(HierarchyDAO hierarchyDAO) {
        List<PointHierarchyNode> folderNodes =  hierarchyDAO.getHierarchy();
        if(folderNodes == null)
            return Collections.emptyList();
        for(PointHierarchyNode node: folderNodes) {
            node.setChildren(new ArrayList<>());
        }
        return folderNodes;
    }

    private static List<PointHierarchyNode> getPointHierarchyNodes(int key,
                                                                   List<PointHierarchyNode> pointNodes,
                                                                   List<PointHierarchyNode> folderNodes,
                                                                   boolean withEmpty) {
        List<PointHierarchyNode> pointAndFolderNodes = new ArrayList<>();
        pointAndFolderNodes.addAll(pointNodes);
        pointAndFolderNodes.addAll(folderNodes);
        sort(pointAndFolderNodes);
        return pointAndFolderNodes.stream()
                .filter(a -> a.getParentId() == key)
                .filter(a -> !a.isFolder() || withEmpty || !isEmpty(a, pointAndFolderNodes))
                .collect(Collectors.toList());
    }

    private static PointHierarchyNode getRootNode(User user, HierarchyDAO hierarchyDAO, DataPointDAO dataPointDAO) {
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> folderNodes = getFolderNodes(hierarchyDAO);
        List<PointHierarchyNode> pointAndFolderNodes = new ArrayList<>();
        pointAndFolderNodes.addAll(pointNodes);
        pointAndFolderNodes.addAll(folderNodes);
        PointHierarchyNode root = PointHierarchyNode.rootNode();
        createTree(root, pointAndFolderNodes);
        return root;
    }
}
