package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.AfterWork;
import com.serotonin.mango.rt.maint.work.ReportWorkItem;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportJob;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ReportService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;
import org.scada_lts.web.mvc.api.dto.ReportDTO;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.serotonin.mango.util.LoggingUtils.userInfo;
import static com.serotonin.mango.util.SendUtils.sendMsgTestSync;
import static org.scada_lts.utils.ValidationUtils.checkArgsIfEmptyThenBadRequest;
import static org.scada_lts.utils.ValidationUtils.checkArgsIfTwoEmptyThenBadRequest;

@Service
public class ReportsApiService implements CrudService<ReportVO> {

    private final DataPointService dataPointService;
    private final ReportService reportService;

    public ReportsApiService(DataPointService dataPointService, ReportService reportService) {
        this.dataPointService = dataPointService;
        this.reportService = reportService;
    }

    @Override
    public ReportVO create(HttpServletRequest request, ReportVO report) {
        return update(request, report);
    }

    @Override
    public ReportVO read(HttpServletRequest request, String xid, Integer id) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Id or xid cannot be null.", id, xid);
        ReportVO report;
        try {
            if (id != null)
                report = reportService.getReport(id);
            else
                report = reportService.getReport(xid);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        User user = Common.getUser(request);
        if(!reportService.hasReportReadPermission(user, report))
            throw new UnauthorizedException(request.getRequestURI());
        return report;
    }

    @Override
    public List<ReportVO> readAll(HttpServletRequest request) {
        User user = Common.getUser(request);
        try {
            return reportService.getReports(user.getId()).stream()
                    .filter(a -> reportService.hasReportReadPermission(user, a))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    @Override
    public ReportVO update(HttpServletRequest request, ReportVO report) {
        User user = Common.getUser(request);
        DwrResponseI18n response = new DwrResponseI18n();
        report.validate(response, user);
        if(response.getHasMessages()) {
            throw new BadRequestException(response, request.getRequestURI());
        }
        if(report.getId() != Common.NEW_ID) {
            if (!reportService.hasReportOwnerPermission(user, report))
                throw new UnauthorizedException(request.getRequestURI());
        }
        try {
            reportService.saveReport(report);
            ReportJob.scheduleReportJob(report);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return report;
    }

    @Override
    public ReportVO delete(HttpServletRequest request, String xid, Integer id) {
        ReportVO report = read(request, xid, id);
        User user = Common.getUser(request);
        if(!reportService.hasReportOwnerPermission(user, report))
            throw new UnauthorizedException(request.getRequestURI());
        try {
            reportService.deleteReport(report.getId());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return report;
    }

    public ReportInstance deleteReportInstance(HttpServletRequest request, Integer id) {
        ReportInstance report = readInstance(request, id);
        User user = Common.getUser(request);
        if(!reportService.hasReportInstanceOwnerPermission(user, report))
            throw new UnauthorizedException(request.getRequestURI());
        try {
            reportService.deleteReportInstance(report.getId(), user.getId());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return report;
    }

    public ReportInstance readInstance(HttpServletRequest request, Integer id) {
        checkArgsIfEmptyThenBadRequest(request, "Id cannot be null.", id);
        ReportInstance report;
        try {
            report = reportService.getReportInstance(id);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        User user = Common.getUser(request);
        if(!reportService.hasReportInstanceOwnerPermission(user, report))
            throw new UnauthorizedException(request.getRequestURI());
        return report;
    }

    public List<ReportVO> search(HttpServletRequest request, Map<String, String> query) {
        checkArgsIfEmptyThenBadRequest(request, "query cannot be null", query);
        User user = Common.getUser(request);
        try {
            return reportService.search(user, query).stream()
                    .filter(a -> reportService.hasReportReadPermission(user, a))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    public void sendTestEmail(HttpServletRequest request, Map<String, List<String>> body) {
        checkArgsIfEmptyThenBadRequest(request, "body cannot be null", body);
        List<String> addresses = body.get("emails");
        if (addresses == null || addresses.isEmpty())
            throw new BadRequestException(LocalizableMessage.getMessage(Common.getBundle(request),"js.email.noRecipForEmail"), request.getRequestURI());
        else {
           List<String> errors = new ArrayList<>();
           sendMsgTestSync(new HashSet<>(addresses), new AfterWork() {
               @Override
               public void workFail(Exception exception) {
                   errors.add(LocalizableMessage.getMessage(Common.getBundle(request),"common.default", exception.getMessage()));
               }
           }, () -> "sendTestEmail from: " + this.getClass().getName()
                   + ", " + userInfo(Common.getUser(request)));
           if(!errors.isEmpty())
               throw new InternalServerErrorException(errors, request.getRequestURI());
        }
    }

    public void runReport(HttpServletRequest request, String xid, Integer id) {
        ReportVO report = read(request, xid, id);
        User user = Common.getUser(request);
        if(!reportService.hasReportSetPermission(user, report))
            throw new UnauthorizedException(request.getRequestURI());
        DwrResponseI18n response = new DwrResponseI18n();
        report.validate(response, user);
        if(response.getHasMessages())
            throw new InternalServerErrorException(response, request.getRequestURI());
        try {
            ReportWorkItem.queueReport(report);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
    }

    public List<ReportInstance> getReportInstances(HttpServletRequest request) {
        User user = Common.getUser(request);
        List<ReportInstance> reportInstances;
        try {
            reportInstances = reportService.getReportInstances().stream()
                    .filter(a -> reportService.hasReportInstanceReadPermission(user, a))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return reportInstances;
    }

    public Integer setReportInstancePreventPurge(HttpServletRequest request, Integer id, Boolean preventPurge) {
        checkArgsIfTwoEmptyThenBadRequest(request, "Id and preventPurge cannot be null.", id, preventPurge);
        User user = Common.getUser(request);
        if(!reportService.hasReportInstanceSetPermission(user, id))
            throw new UnauthorizedException(request.getRequestURI());
        try {
            reportService.setReportInstancePreventPurge(id, preventPurge);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return id;
    }

    public ReportVO toReport(HttpServletRequest request, ReportDTO query) {
        User user = Common.getUser(request);
        List<HashMap<String, Object>> points = query.getPoints();
        List<ReportPointVO> reportPoints = new ArrayList<>();
        for (HashMap<String, Object> dp : points) {
            Integer pointId = (Integer)dp.get("pointId");
            if(pointId != null) {
                DataPointVO point;
                try {
                    point = dataPointService.getDataPoint(pointId);
                } catch (Exception ex) {
                    throw new InternalServerErrorException(ex, request.getRequestURI());
                }
                if (point != null && GetDataPointsWithAccess.hasDataPointReadPermission(user, point)) {
                    reportPoints.add(ReportPointVO.newInstance(dp));
                }
            }
        }
        return ReportVO.createReport(query, user, reportPoints);
    }
}
