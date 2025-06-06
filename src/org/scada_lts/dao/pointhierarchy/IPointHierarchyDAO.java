package org.scada_lts.dao.pointhierarchy;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IPointHierarchyDAO {
    List<PointHierarchyNode> getPointsHierarchy();

    DataPointVO getPointsHierarchy(int id);

    Map<Integer, List<PointFolder>> getFolderList();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    boolean updateTitle(int id, String title);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    boolean updateParentIdDataPoint(int id, int parentId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    boolean updateParentId(int id, int parentId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(int parentId, String name);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(int id, int parentId, String name);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    boolean deleteFolder(int key, int parentId);
}
