package org.scada_lts.dao.report;

import com.serotonin.mango.vo.report.ReportDataStreamHandler;
import com.serotonin.mango.vo.report.ReportDataValue;
import com.serotonin.mango.vo.report.ReportPointInfo;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public interface IReportInstanceDataDAO {
    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor= SQLException.class)
    int insert(Object[] params, int reportPointId, String timestampSql);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    int insertReportInstanceDataAnnotations(String annotationCase, int reportPointId);

    void setReportValue(ReportPointInfo point, ReportDataValue rdv, ReportDataStreamHandler handler);
}
