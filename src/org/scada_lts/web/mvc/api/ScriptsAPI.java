package org.scada_lts.web.mvc.api;

import org.scada_lts.utils.ScriptsApiUtils;
import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ScriptService;
import org.scada_lts.web.mvc.api.json.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.scada_lts.utils.MailingListApiUtils.isXidChanged;
import static org.scada_lts.utils.ScriptsApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.formatErrorsJson;

/**
 * Simple controller for Scripts in Scada-LTS
 *
 * @author Sergio Selvaggi <sselvaggi@softq.pl>
 */
@Controller
@RequestMapping(value = "/api/scripts")
public class ScriptsAPI {

    private static final Log LOG = LogFactory.getLog(ScriptsAPI.class);

    @Resource
    private ScriptService scriptService;

    @Resource
    private DataPointService dataPointService;

    /**
     * Get Scripts related to specific Data Point
     *
     * @param request     HTTP request with user data
     * @return ScriptDTO List
     */
    @GetMapping(value = "/search")
    public ResponseEntity<List<ScriptVO<?>>> getScripts(HttpServletRequest request) {
        LOG.info("GET::/api/scripts/search");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/execute/{xid}")
    public ResponseEntity<List<ScriptVO<?>>> executeScript(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("GET::/api/scripts/execute");
        try {
            User user = Common.getUser(request);

            if (user != null && user.isAdmin()) {
                ScriptVO<?> script = scriptService.getScript(xid);
                if (script != null) {
                    ScriptRT rt = script.createScriptRT();
                    rt.execute();
                }
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteScript(@PathVariable Integer id, HttpServletRequest request) {
        LOG.info("DELETE::/api/scripts");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validateScriptDelete(id);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                scriptService.deleteScript(id);
                return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/validateXid")
    public ResponseEntity<Map<String, String>> validateScriptXid(@RequestBody JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("POST::/api/scripts/validateXid");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                Map<String, String> response = new HashMap<>();
                response.put("xidRepeated", isScriptPresent(jsonBodyRequest.getXid(), scriptService)?"true":"false");
                return new ResponseEntity<>( response, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Map<String, Object>> saveScript(@RequestBody JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("POST::/api/scripts/save");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = ScriptsApiUtils.validateScriptBody(jsonBodyRequest);
                Map<String, Object> response = new HashMap<>();
                if (!error.isEmpty()) {
                    response.put("errors", error);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if (isScriptPresent(jsonBodyRequest.getXid(), scriptService)) {
                    response.put("errors", "This XID is already in use");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                String pointsError = validatePointsOnContext(jsonBodyRequest.getPointsOnContext(), dataPointService);
                if (!pointsError.isEmpty()) {
                    response.put("errors", pointsError);
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
                ContextualizedScriptVO vo = createScriptFromBody(jsonBodyRequest, user, dataPointService);
                scriptService.saveScript(vo);
                response.put("scriptId", vo.getId());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<String> updateScript(@RequestBody JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("PUT::/api/scripts/update");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validateScriptUpdate(jsonBodyRequest);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                return findAndUpdateScript(jsonBodyRequest);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/generateXid")
    public ResponseEntity<String> getUniqueXid(HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                return new ResponseEntity<>(scriptService.generateUniqueXid(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> findAndUpdateScript(JsonScript body) {
        return getScript(body.getId(), scriptService).map(toUpdate -> updateScriptBody(toUpdate, body))
                .orElse(new ResponseEntity<>(formatErrorsJson("Script not found"), HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<String> updateScriptBody(ContextualizedScriptVO toUpdate, JsonScript body) {
        if (isXidChanged(toUpdate.getXid(), body.getXid()) &&
                isScriptPresent(body.getXid(), scriptService)){
            return new ResponseEntity<>(formatErrorsJson("This XID is already in use"), HttpStatus.BAD_REQUEST);
        }
        String pointsError = validatePointsOnContext(body.getPointsOnContext(), dataPointService);
        if (!pointsError.isEmpty()) {
            return new ResponseEntity<>(formatErrorsJson(pointsError), HttpStatus.NOT_FOUND);
        }
        updateValueScript(toUpdate, body, dataPointService);
        scriptService.saveScript(toUpdate);
        return new ResponseEntity<>("{\"status\":\"updated\"}", HttpStatus.OK);
    }
}
