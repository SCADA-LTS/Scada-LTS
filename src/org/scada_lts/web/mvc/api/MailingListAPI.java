package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.mvc.api.dto.CreateMailingList;
import org.scada_lts.web.mvc.api.dto.UpdateMailingList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.scada_lts.utils.ApiUtils.*;
import static org.scada_lts.utils.MailingListApiUtils.*;

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
    private UserService userService = new UserService();

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
    public ResponseEntity<List<ScadaObjectIdentifier>> getSimpleMailingLists(HttpServletRequest request) {
        LOG.info("/api/mailingList/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(mailingListService.getSimpleMailingLists(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/generateUniqueXid")
    public ResponseEntity<String> generateUniqueXid(HttpServletRequest request) {
        LOG.info("/api/mailingList/generateUniqueXid");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(mailingListService.generateUniqueXid(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Map<String, String>> createMailingList(@RequestBody CreateMailingList mailingList, HttpServletRequest request) {
        LOG.info("POST:/api/mailingList");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                Map<String, String> response = new HashMap<>();
                String error = validateMailingListCreate(mailingList);
                if (!error.isEmpty()) {
                    response.put("errors", error);
                    return ResponseEntity.badRequest().body(response);
                }
                if (isMailingListPresent(mailingList.getXid(), mailingListService)) {
                    response.put("errors", "This XID is already in use");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if (!usersExist(mailingList.getEntries(), userService)) {
                    response.put("errors", "user or users not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
                mailingListService.saveMailingList(createMailingListFromBody(mailingList));
                response.put("status", "created");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<Map<String, String>> updateMailingList(@RequestBody UpdateMailingList mailingList, HttpServletRequest request) {
        LOG.info("PUT:/api/mailingList");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                Map<String, String> response = new HashMap<>();
                String error = validateMailingListUpdate(mailingList);
                if (!error.isEmpty()) {
                    response.put("errors", error);
                    return ResponseEntity.badRequest().body(response);
                }
                return findAndUpdateMailingList(mailingList);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Map<String, String>> deleteMailingListById(@PathVariable Integer id, HttpServletRequest request) {
        LOG.info("DELETE: /api/mailingList/{id}");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                Map<String, String> response = new HashMap<>();
                String error = validateMailingListDelete(id);
                if (!error.isEmpty()) {
                    response.put("errors", error);
                    return ResponseEntity.badRequest().body(response);
                }
                mailingListService.deleteMailingList(id);
                response.put("status", "deleted");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> findAndUpdateMailingList(UpdateMailingList mailingListBody) {
        Map<String, String> errors = new HashMap<>();
        errors.put("errors", "mailingList not found");
        return getMailingList(mailingListBody.getId(), mailingListService)
                .map(toUpdate -> updateMailingList(toUpdate, mailingListBody)).
                        orElse(new ResponseEntity<>(errors, HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Map<String, String>> updateMailingList(MailingList toUpdate, UpdateMailingList mailingListBody) {
        Map<String, String> response = new HashMap<>();
        if (mailingListBody.getEntries() != null && !usersExist(mailingListBody.getEntries(), userService)) {
            response.put("errors", "user or users not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (isXidChanged(toUpdate.getXid(), mailingListBody.getXid()) &&
                isMailingListPresent(mailingListBody.getXid(), mailingListService)) {
            response.put("errors", "This XID is already in use");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        updateValueMailingList(toUpdate, mailingListBody);
        mailingListService.saveMailingList(toUpdate);
        response.put("status", "updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
