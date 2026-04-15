package org.scada_lts.dao.report;

import com.serotonin.mango.vo.report.ReportVO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IReportDAO {
    ReportVO getReport(int id);

    ReportVO getReport(String xid);

    List<ReportVO> getReports();

    List<ReportVO> search(Map<String, String> query);

    List<ReportVO> search(int userId, Map<String, String> query);

    List<ReportVO> getReports(int userId);

    int insert(ReportVO report);

    void update(ReportVO report);

    void delete(int id);
}
