package org.scada_lts.web.mvc.api;

import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.dto.ReportDTO;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    private final ReportsApiService reportsApiService;

    public ReportsAPI(ReportsApiService reportsApiService) {
        this.reportsApiService = reportsApiService;
    }

    /**
     * Get Reports related to user
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @PostMapping(value = "/search")
    public ResponseEntity<List<ReportVO>> search(@RequestParam Map<String, String> query, HttpServletRequest request) {
        LOG.info("GET::/api/reports/search");
        List<ReportVO> response;
        try {
            response = reportsApiService.search(request, query);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex, request.getRequestURI());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<String> deleteReport(@PathVariable("id") Integer id, HttpServletRequest request) {
        reportsApiService.delete(request, null, id);
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
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
        ReportVO report = reportsApiService.toReport(request, query);
        reportsApiService.create(request, report);
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

   @PostMapping(value = "/sendTestEmails", produces = "application/json")
   public ResponseEntity<String> sendTestEmail(@RequestBody Map<String, List<String>> body, HttpServletRequest request) {
       LOG.info("/api/reports/sendTestEmails");
       reportsApiService.sendTestEmail(request, body);
       return new ResponseEntity<>("OK", HttpStatus.OK);
   }

    /**
     * Run Report
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @GetMapping(value = "/run/{id}")
    public ResponseEntity<String> runReport(@PathVariable("id") Integer id, HttpServletRequest request) {
        LOG.info("GET::/api/reports/run");
        reportsApiService.runReport(request, null, id);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    /**
     * Get Report instances related to user
     *
     * @param request     HTTP request with user data
     * List<ReportInstance>
     */
    @GetMapping(value = "/instances")
    public ResponseEntity<List<ReportInstance>> getReportInstances(HttpServletRequest request) {
        LOG.info("GET::/api/reports/instances");
        List<ReportInstance> reportInstances = reportsApiService.getReportInstances(request);
        return new ResponseEntity<>(reportInstances, HttpStatus.OK);
    }

    /**
     * Delete Report instance related to user
     *
     * @param request     HTTP request with user data
     *
     */
    @DeleteMapping(value = "/instances/{id}")
    public HttpEntity<Integer> deleteReportInstance(@PathVariable("id") Integer id, HttpServletRequest request) {
        LOG.info("GET::/api/reports/instances");
        reportsApiService.deleteReportInstance(request, id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping(value = "/instances/{id}/preventPurge/{preventPurge}")
    public HttpEntity<Integer> setReportInstancePreventPurge(@PathVariable("id") Integer id, @PathVariable("preventPurge") Boolean preventPurge, HttpServletRequest request) {
        LOG.info("GET::/api/reports/instances/"+id+"/preventPurge/"+preventPurge);
        reportsApiService.setReportInstancePreventPurge(request, id, preventPurge);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
