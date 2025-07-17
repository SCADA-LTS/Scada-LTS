package org.scada_lts.dao;

import com.serotonin.mango.vo.event.ScheduledEventVO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IScheduledEventDAO {
    ScheduledEventVO getScheduledEvent(int id);

    ScheduledEventVO getScheduledEvent(String xid);

    List<ScheduledEventVO> getScheduledEvents();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(ScheduledEventVO scheduledEventVO);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(ScheduledEventVO scheduledEventVO);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);
}
