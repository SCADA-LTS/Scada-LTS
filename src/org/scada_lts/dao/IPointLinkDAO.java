package org.scada_lts.dao;

import com.serotonin.mango.vo.link.PointLinkVO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IPointLinkDAO {
    PointLinkVO getPointLink(int id);

    PointLinkVO getPointLink(String xid);

    List<PointLinkVO> getPointLinks();

    List<PointLinkVO> getPointLinksForPoint(int datapointId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(PointLinkVO pointLink);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(PointLinkVO pointLink);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);
}
