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
import java.util.List;

import static org.scada_lts.utils.ApiUtils.*;
import static org.scada_lts.utils.MailingListApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.formatErrorsJson;

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
    public ResponseEntity<String> createMailingList(@RequestBody CreateMailingList mailingList, HttpServletRequest request) {
        LOG.info("POST:/api/mailingList");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validateMailingListCreate(mailingList);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                if (isMailingListPresent(mailingList.getXid(), mailingListService)) {
                    return new ResponseEntity<>(formatErrorsJson("This XID is already in use"), HttpStatus.BAD_REQUEST);
                }
                if (!usersExist(mailingList.getEntries(), userService)) {
                    return new ResponseEntity<>(formatErrorsJson("user or users not found"), HttpStatus.NOT_FOUND);
                }
                mailingListService.saveMailingList(createMailingListFromBody(mailingList));
                return new ResponseEntity<>("{\"status\":\"created\"}", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> updateMailingList(@RequestBody UpdateMailingList mailingList, HttpServletRequest request) {
        LOG.info("PUT:/api/mailingList");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validateMailingListUpdate(mailingList);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
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
    public ResponseEntity<String> deleteMailingListById(@PathVariable Integer id, HttpServletRequest request) {
        LOG.info("DELETE: /api/mailingList/{id}");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validateMailingListDelete(id);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                mailingListService.deleteMailingList(id);
                return new ResponseEntity<>("{\"status\":\"deleted\"}", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> findAndUpdateMailingList(UpdateMailingList mailingListBody) {
        return getMailingList(mailingListBody.getId(), mailingListService)
                .map(toUpdate -> updateMailingList(toUpdate, mailingListBody)).
                        orElse(new ResponseEntity<>(formatErrorsJson("mailingList not found"), HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<String> updateMailingList(MailingList toUpdate, UpdateMailingList mailingListBody) {
        if (mailingListBody.getEntries() != null && !usersExist(mailingListBody.getEntries(), userService)) {
            return new ResponseEntity<>(formatErrorsJson("user or users not found"), HttpStatus.NOT_FOUND);
        }
        if (isXidChanged(toUpdate.getXid(), mailingListBody.getXid()) &&
                isMailingListPresent(mailingListBody.getXid(), mailingListService)){
            return new ResponseEntity<>(formatErrorsJson("This XID is already in use"), HttpStatus.BAD_REQUEST);
        }
        updateValueMailingList(toUpdate, mailingListBody);
        mailingListService.saveMailingList(toUpdate);
        return new ResponseEntity<>("{\"status\":\"updated\"}", HttpStatus.OK);
    }
}
