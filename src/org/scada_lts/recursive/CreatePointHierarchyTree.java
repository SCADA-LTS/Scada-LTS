package org.scada_lts.recursive;

import com.serotonin.mango.Common;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

public class CreatePointHierarchyTree implements Callable<Void> {

    private final PointHierarchyNode root;
    private final List<PointHierarchyNode> pointAndFolderNodes;
    private final int safe;

    public CreatePointHierarchyTree(PointHierarchyNode root, List<PointHierarchyNode> pointAndFolderNodes, int safe) {
        this.root = root;
        this.pointAndFolderNodes = pointAndFolderNodes;
        this.safe = safe;
    }

    @Override
    public Void call() throws Exception {
        if(safe < 0)
            return null;
        int temp = safe - 1;
        List<Callable<Void>> tasks = new CopyOnWriteArrayList<>();
        for(PointHierarchyNode node: pointAndFolderNodes) {
            if(root.getKey() == node.getParentId()) {
                root.getChildren().add(node);
                if(Boolean.TRUE.equals(node.isFolder())) {
                    tasks.add(new CreatePointHierarchyTree(node, pointAndFolderNodes, temp));
                }
            }
        }
        if(!tasks.isEmpty()) {
            Common.ctx.getBackgroundProcessing().getCommonPool().invokeAll(tasks);
        }
        return null;
    }
}