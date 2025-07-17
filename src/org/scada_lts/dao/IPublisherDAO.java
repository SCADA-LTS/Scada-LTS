package org.scada_lts.dao;

import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IPublisherDAO {
    PublisherVO<? extends PublishedPointVO> getPublisher(int id);

    PublisherVO<? extends PublishedPointVO> getPublisher(String xid);

    List<PublisherVO<? extends PublishedPointVO>> getPublishers();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(PublisherVO<? extends PublishedPointVO> publisher);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(PublisherVO<? extends PublishedPointVO> publisher);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);
}
