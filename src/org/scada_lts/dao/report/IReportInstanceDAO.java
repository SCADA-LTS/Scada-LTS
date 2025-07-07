package org.scada_lts.dao.report;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.*;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.i18n.LocalizableMessageParseException;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IReportInstanceDAO {

    ReportInstance getReportInstance(int id);

    List<ReportInstance> getReportInstances(int userId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(ReportInstance reportInstance);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void updateTime(ReportInstance reportInstance);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void updatePreventPurge(int id, boolean preventPurge, int userId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id, int userId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int deleteReportBefore(long time);

    List<EventInstance> getReportInstanceEvents(int instanceId);

    List<ReportInstance> getReportInstances();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void updatePreventPurge(int id, boolean preventPurge);

}
