package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.AfterWork;
import com.serotonin.mango.rt.maint.work.ReportWorkItem;
import com.serotonin.mango.util.SendMsgUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportJob;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ReportService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;
import org.scada_lts.web.mvc.api.dto.ReportDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Simple controller for Reports in Scada-LTS
 *
 * @author Sergio Selvaggi <sselvaggi@softq.pl>
 */
@Controller
@RequestMapping(value = "/api/reports")
public class ReportsAPI {

    private static final Log LOG = LogFactory.getLog(ReportsAPI.class);
    private DataPointService dataPointService = new DataPointService();

    @Resource
    private ReportService reportService;

    @Resource
    private SystemSettingsService systemSettingsService;

    /**
     * Get Reports related to user
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @PostMapping(value = "/search")
    public ResponseEntity<List<ReportVO>> getAll(@RequestParam Map<String, String> query, HttpServletRequest request) {
        LOG.info("GET::/api/reports/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<List<ReportVO>>(
                    reportService.search(query),
                    HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteReport(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                reportService.deleteReport(id);
                return new ResponseEntity<>("DELETED", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * Save Reports related to user
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @PostMapping(value = "/save")
    public HttpEntity<String> save(@RequestBody ReportDTO query, HttpServletRequest request) {
        LOG.info("GET::/api/reports/save");
        try {
            User user = Common.getUser(request);
            if(user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            List<HashMap<String, Object>> points = query.getPoints();
            List<ReportPointVO> reportPoints = new ArrayList<>();
            for (HashMap<String, Object> dp : points) {
                Integer pointId = (Integer)dp.get("pointId");
                if(pointId != null) {
                    DataPointVO point = dataPointService.getDataPoint(pointId);
                    if (point != null && GetDataPointsWithAccess.hasDataPointReadPermission(user, point)) {
                        reportPoints.add(ReportPointVO.newInstance(dp));
                    }
                }
            }
            ReportVO report = ReportVO.createReport(query, user, reportPoints);
            reportService.saveReport(report);
            ReportJob.scheduleReportJob(report);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   @PostMapping(value = "/sendTestEmails", produces = "application/json")
   public ResponseEntity<String> sendTestEmail(@RequestBody Map<String, List<String>> body, HttpServletRequest request) {
       LOG.info("/api/reports/sendTestEmails");
       try {
           User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                List<String> addresses = body.get("emails");

                if (addresses == null || addresses.isEmpty())
                    return new ResponseEntity<>(LocalizableMessage.getMessage(Common.getBundle(),"js.email.noRecipForEmail"), HttpStatus.BAD_REQUEST);
                else {
                    List<String> errors = new ArrayList<>();
                    boolean sent = SendMsgUtils.sendEmailTestSync(new HashSet<>(addresses), new AfterWork() {
                        @Override
                        public void workFail(Exception exception) {
                            errors.add(LocalizableMessage.getMessage(Common.getBundle(),"common.default", exception.getMessage()));
                        }
                        @Override
                        public void workSuccess() {}
                    });
                    if(sent && errors.isEmpty())
                        return new ResponseEntity<>("OK", HttpStatus.OK);
                    if(!sent)
                        return new ResponseEntity<>("Email has not been sent.", HttpStatus.BAD_REQUEST);
                    return new ResponseEntity<>(errors.get(0), HttpStatus.BAD_REQUEST);
                }
            } else {
               return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
       } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }

    /**
     * Run Report
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @GetMapping(value = "/run/{id}")
    public ResponseEntity<String> runReport(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("GET::/api/reports/run");
        try {
            User user = Common.getUser(request);

            if (user != null) {
                ReportVO report = reportService.getReport(id);
                ReportWorkItem.queueReport(report);
                return new ResponseEntity<String>(
                        "ok",
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get Report instances related to user
     *
     * @param request     HTTP request with user data
     * List<ReportInstance>
     */
    @GetMapping(value = "/instances")
    public ResponseEntity<List<ReportInstance>> runReport(HttpServletRequest request) {
        LOG.info("GET::/api/reports/instances");
        try {
            User user = Common.getUser(request);

            if (user != null) {
                List<ReportInstance> reportInstances = reportService.getReportInstances(user.getId());

                return new ResponseEntity<List<ReportInstance>>(
                        reportInstances,
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete Report instance related to user
     *
     * @param request     HTTP request with user data
     *
     */
    @DeleteMapping(value = "/instances/{id}")
    public HttpEntity<Integer> deleteReportInstance(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("GET::/api/reports/instances");
        try {
            User user = Common.getUser(request);

            if (user != null) {
                 reportService.deleteReportInstance(id, user.getId());

                return new ResponseEntity<Integer>(
                        id,
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/instances/{id}/preventPurge/{preventPurge}")
    public HttpEntity<Integer> setReportInstancePreventPurge(@PathVariable("id") int id, @PathVariable("preventPurge") boolean preventPurge, HttpServletRequest request) {
        LOG.info("GET::/api/reports/instances/"+id+"/preventPurge/"+preventPurge);
        try {
            User user = Common.getUser(request);

            if (user != null) {
                reportService.setReportInstancePreventPurge(id, preventPurge, user.getId());

                return new ResponseEntity<Integer>(
                        id,
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
