package org.scada_lts.dao;

import com.serotonin.mango.vo.event.MaintenanceEventVO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IMaintenanceEventDAO {
    MaintenanceEventVO getMaintenanceEvent(int id);

    MaintenanceEventVO getMaintenanceEvent(String xid);

    List<MaintenanceEventVO> getMaintenanceEvents();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(MaintenanceEventVO maintenanceEvent);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(MaintenanceEventVO maintenanceEvent);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void deleteMaintenanceEventsForDataSource(int dataSourceId);
}
