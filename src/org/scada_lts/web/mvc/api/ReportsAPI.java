package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.report.ReportInstancePointDAO;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.ReportService;
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

    @Resource
    private ReportService reportService;

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
     * Run Report
     *
     * @param request     HTTP request with user data
     * @return ReportVO List
     */
    @PostMapping(value = "/run/{id}")
    public ResponseEntity<List<EventInstance>> runReport(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("GET::/api/reports/run");
        try {
            User user = Common.getUser(request);

            if (user != null) {
                ReportVO r = reportService.getReport(id);
                ReportInstance reportInstance = new ReportInstance();
                List<ReportInstancePointDAO.PointInfo> points = new ArrayList<>();
                ResourceBundle bundle = new ResourceBundle() {
                    @Override
                    protected Object handleGetObject(String key) {
                        return null;
                    }

                    @Override
                    public Enumeration<String> getKeys() {
                        return null;
                    }
                };
                reportService.runReport(reportInstance, points, bundle);
                return new ResponseEntity<List<EventInstance>>(
                        reportService.getReportInstanceEvents(reportInstance.getId()),
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

}
