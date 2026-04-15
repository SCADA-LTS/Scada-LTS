package org.scada_lts.dao.report;

import com.serotonin.mango.vo.report.ReportDataStreamHandler;
import com.serotonin.mango.vo.report.ReportDataValue;
import com.serotonin.mango.vo.report.ReportPointInfo;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public interface IReportInstanceDataDAO {
    int insert(Object[] params, int reportPointId, String timestampSql);

    int insertReportInstanceDataAnnotations(String annotationCase, int reportPointId);

    void setReportValue(ReportPointInfo point, ReportDataValue rdv, ReportDataStreamHandler handler);
}
