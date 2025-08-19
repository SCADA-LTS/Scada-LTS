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

    int insert(ReportInstance reportInstance);

    void updateTime(ReportInstance reportInstance);

    void updatePreventPurge(int id, boolean preventPurge, int userId);

    void delete(int id, int userId);

    int deleteReportBefore(long time);

    List<EventInstance> getReportInstanceEvents(int instanceId);

    List<ReportInstance> getReportInstances();

    void updatePreventPurge(int id, boolean preventPurge);

}
