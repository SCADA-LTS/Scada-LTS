package org.scada_lts.dao.report;

import com.serotonin.mango.vo.report.ReportUserComment;

import java.util.List;

public interface IReportInstanceUserCommentDAO {

    List<ReportUserComment> getReportUserComments(int reportInstanceId);
}
