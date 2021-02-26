package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.web.mvc.api.json.JsonMailingList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for MailingList
 *
 * @author Artur Wolak
 */
@Controller
@RequestMapping(path = "/api/mailingList")
public class MailingListAPI {
    private static final Log LOG = LogFactory.getLog(MailingListAPI.class);

    @Resource
    private MailingListService mailingListService;

    @GetMapping(value = "/getAll", produces = "application/json")
    public ResponseEntity<List<MailingList>> getMailingLists(HttpServletRequest request) {
        LOG.info("/api/mailingList/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(mailingListService.getMailingLists(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get Simple Mailing List
     *
     * This method returns only simplified list of
     * all existing mailing list without details.
     * This request reduce the amount of data sent via
     * HTTP request and increase the performance of the application
     * To see more detailed information user has to request for
     * specific mailing list by another method.
     *
     * @param request HTTP request
     * @return List<JsonMailingList>
     */
    @GetMapping(value = "/getAllSimple", produces = "application/json")
    public ResponseEntity<List<JsonMailingList>> getSimpleMailingLists(HttpServletRequest request) {
        LOG.info("/api/mailingList/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(mailingListService.getSimpleMailingLists(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/id/{id}", produces = "application/json")
    public ResponseEntity<MailingList> getMailingListsById(@PathVariable int id, HttpServletRequest request) {
        LOG.info("/api/mailingList/get/id/{id}");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(mailingListService.getMailingList(id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/{xid}", produces = "application/json")
    public ResponseEntity<MailingList> getMailingListsByXid(@PathVariable String xid, HttpServletRequest request) {
        LOG.info("/api/mailingList/get/{xid}");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(mailingListService.getMailingList(xid), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<String> createMailingList(@RequestBody MailingList mailingList, HttpServletRequest request) {
        LOG.info("POST:/api/mailingList");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                mailingListService.saveMailingList(mailingList);
                return new ResponseEntity<>("{\"status\":\"created\"}", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "", produces = "application/json")
    public ResponseEntity<String> updateMailingList(@RequestBody MailingList mailingList, HttpServletRequest request) {
        LOG.info("PUT:/api/mailingList");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                mailingListService.saveMailingList(mailingList);
                return new ResponseEntity<>("{\"status\":\"updated\"}", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<String> deleteMailingListById(@PathVariable int id, HttpServletRequest request) {
        LOG.info("DELETE: /api/mailingList/{id}");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                mailingListService.deleteMailingList(id);
                return new ResponseEntity<>("{\"status\":\"deleted\"}", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
