package org.scada_lts.web.mvc.api.alarms;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.alarms.Notification;
import org.scada_lts.dao.alarms.NotificationService;
import org.scada_lts.dao.alarms.Range;
import org.scada_lts.dao.alarms.Scheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/api/alarms/notification")
public class NotificationAPI {

    private static final Log LOG = LogFactory.getLog(NotificationAPI.class);

    @Resource
    private NotificationService notificationService;

    @GetMapping(value = "/getScheduler/{id}")
    public ResponseEntity<List<Scheduler>> getScheduler(@PathVariable("id") Long id, HttpServletRequest request) {
        LOG.info("/api/alarms/notification/getScheduler/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                List<Scheduler> result = notificationService.getSchedulerById(id);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getAllRanges", produces = "application/json")
    public ResponseEntity<List<Range>> getAllRanges(HttpServletRequest request) {
        LOG.info("/api/alarms/notification/getAllRanges");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                List<Range> result = notificationService.getAllRanges();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/setRange", consumes = "application/json")
    public ResponseEntity<Range> setRange(HttpServletRequest request, @RequestBody Range range) {
        LOG.info("/api/alarms/notification/setRange");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Range result = notificationService.insertRange(range);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getAllNotifications", produces = "application/json")
    public ResponseEntity<List<Notification>> getAllNotifications(HttpServletRequest request) {
        LOG.info("/api/alarms/notification/getAllNotifications");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                List<Notification> result = notificationService.getAllNotifications();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/setNotification", consumes = "application/json")
    public ResponseEntity<Notification> setNotification(HttpServletRequest request, @RequestBody Notification notification) {
        LOG.info("/api/alarms/notification/setRange");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Notification result = notificationService.insertNotification(notification);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getSchedulers/dataPoint/{id}")
    public ResponseEntity<List<Scheduler>> getSchedulersForDataPoint(@PathVariable("id") Long id, HttpServletRequest request) {
        LOG.info("/api/alarms/notification/getSchedulers/dataPoint/" + id);
        try {
            User user = Common.getUser(request);
            if (user != null) {
                List<Scheduler> result = notificationService.getDataPointSchedulers(id);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
