package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.HierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PointHierarchyUtils {

    private PointHierarchyUtils() {}

    public static PointHierarchyNode getRoot(User user, HierarchyDAO hierarchyDAO, DataPointDAO dataPointDAO)  {
        List<PointHierarchyNode> pointNodes = getPointNodesWithAccess(user, dataPointDAO);
        List<PointHierarchyNode> folderNodes = hierarchyDAO.getHierarchy();
        List<PointHierarchyNode> pointAndFolderNodes = new ArrayList<>();
        pointAndFolderNodes.addAll(pointNodes);
        pointAndFolderNodes.addAll(folderNodes);

        PointHierarchyNode root = PointHierarchyNode.rootNode();
        createTree(root, pointAndFolderNodes);
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
        root.setChildren(new ArrayList<>());
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
        int safe = 100;
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
}
