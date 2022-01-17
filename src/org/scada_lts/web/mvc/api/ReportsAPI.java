package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.maint.work.EmailWorkItem;
import com.serotonin.mango.rt.maint.work.ReportWorkItem;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.report.ReportInstancePointDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.ReportService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.utils.SQLPageWithTotal;
import org.scada_lts.web.mvc.api.dto.EventCommentDTO;
import org.scada_lts.web.mvc.api.dto.EventDTO;
import org.scada_lts.web.mvc.api.json.JsonEventSearch;
import org.scada_lts.web.mvc.api.json.JsonIdSelection;
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



    /**
     * Save Reports related to user
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @PostMapping(value = "/save")
    public HttpEntity<String> save(@RequestBody Map<String, Object> query, HttpServletRequest request) {
        LOG.info("GET::/api/reports/save");
        try {
            User user = Common.getUser(request);
            ReportVO report = new ReportVO();

            report.setId(Integer.parseInt(query.get("id").toString()));
            report.setName(query.get("name").toString());
            //report.setPoints(query.get("points"));
            report.setIncludeEvents(Integer.parseInt(query.get("includeEvents").toString()));
            report.setIncludeUserComments(Boolean.parseBoolean(query.get("includeUserComments").toString()));
            report.setDateRangeType(Integer.parseInt(query.get("dateRangeType").toString()));
            report.setRelativeDateType(Integer.parseInt(query.get("relativeDateType").toString()));
            report.setPreviousPeriodCount(Integer.parseInt(query.get("previousPeriodCount").toString()));
            report.setPreviousPeriodType(Integer.parseInt(query.get("previousPeriodType").toString()));
            report.setPastPeriodCount(Integer.parseInt(query.get("pastPeriodCount").toString()));
            report.setPastPeriodType(Integer.parseInt(query.get("pastPeriodType").toString()));
            report.setFromNone(Boolean.parseBoolean(query.get("fromNone").toString()));
            report.setFromYear(Integer.parseInt(query.get("fromYear").toString()));
            report.setFromMonth(Integer.parseInt(query.get("fromMonth").toString()));
            report.setFromDay(Integer.parseInt(query.get("fromDay").toString()));
            report.setFromHour(Integer.parseInt(query.get("fromHour").toString()));
            report.setFromMinute(Integer.parseInt(query.get("fromMinute").toString()));
            report.setToNone(Boolean.parseBoolean(query.get("toNone").toString()));
            report.setToYear(Integer.parseInt(query.get("toYear").toString()));
            report.setToMonth(Integer.parseInt(query.get("toMonth").toString()));
            report.setToDay(Integer.parseInt(query.get("toDay").toString()));
            report.setToHour(Integer.parseInt(query.get("toHour").toString()));
            report.setToMinute(Integer.parseInt(query.get("toMinute").toString()));
            report.setSchedule(Boolean.parseBoolean(query.get("schedule").toString()));
            report.setSchedulePeriod(Integer.parseInt(query.get("schedulePeriod").toString()));
            report.setRunDelayMinutes(Integer.parseInt(query.get("runDelayMinutes").toString()));
            report.setScheduleCron(query.get("scheduleCron").toString());
            report.setEmail(Boolean.parseBoolean(query.get("email").toString()));
            report.setIncludeData(Boolean.parseBoolean(query.get("includeData").toString()));
            report.setZipData(Boolean.parseBoolean(query.get("zipData").toString()));

            reportService.saveReport(report);
            List<RecipientListEntryBean> recipients;
            if (user != null) {
                return new ResponseEntity<String>(
                        "Ok",
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
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
                String[] toAddrs = new String[body.get("emails").size()];
                for (int i = 0; i < body.get("emails").size(); i++) {
                    toAddrs[i]= body.get("emails").get(i);
                }

                DwrResponseI18n response = new DwrResponseI18n();

                if (toAddrs.length == 0)
                    response.addGenericMessage("js.email.noRecipForEmail");
                else {
                    try {
                        ResourceBundle bundle = Common.getBundle();
                        Map<String, Object> model = new HashMap<String, Object>();
                        model.put("user", Common.getUser());
                        model.put("message", new LocalizableMessage(
                               "reports.recipTestEmailMessage"));
                        MangoEmailContent cnt = new MangoEmailContent("testEmail",
                               model, bundle, I18NUtils.getMessage(bundle,
                               "ftl.testEmail"), Common.UTF8);
                        EmailWorkItem.queueEmail(toAddrs, cnt);
                        return new ResponseEntity<String>(
                                "OK",
                                HttpStatus.OK
                        );
                    } catch (Exception e) {
                        response.addGenericMessage("common.default", e.getMessage());
                    }
                }
            } else {
               return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
       } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
       return null;
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
