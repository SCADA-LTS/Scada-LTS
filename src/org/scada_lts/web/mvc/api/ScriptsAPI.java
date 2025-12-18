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
import org.scada_lts.web.beans.validation.xss.XssProtect;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.InternalServerErrorException;
import org.scada_lts.web.mvc.api.exceptions.NotFoundException;
import org.scada_lts.web.mvc.api.exceptions.UnauthorizedException;
import org.scada_lts.web.mvc.api.json.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import static org.scada_lts.utils.MailingListApiUtils.isXidChanged;
import static org.scada_lts.utils.ScriptsApiUtils.*;

/**
 * Simple controller for Scripts in Scada-LTS
 *
 * @author Sergio Selvaggi <sselvaggi@softq.pl>
 */
@RestController
@RequestMapping(value = "/api/scripts")
public class ScriptsAPI {

    private static final Log LOG = LogFactory.getLog(ScriptsAPI.class);

    private final ScriptService scriptService;
    private final DataPointService dataPointService;

    public ScriptsAPI(ScriptService scriptService, DataPointService dataPointService) {
        this.scriptService = scriptService;
        this.dataPointService = dataPointService;
    }

    /**
     * Get Scripts related to specific Data Point
     *
     * @param request     HTTP request with user data
     * @return ScriptDTO List
     */
    @GetMapping(value = "/search")
    public ResponseEntity<List<ScriptVO<?>>> getScripts(HttpServletRequest request) {
        LOG.info("GET::/api/scripts/search");
        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            List<ScriptVO<?>> scripts;
            try {
                scripts = scriptService.getScripts();
            } catch (Exception ex) {
                throw new InternalServerErrorException(ex, request.getRequestURI());
            }
            return new ResponseEntity<>(scripts, HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @PostMapping(value = "/execute/{xid}")
    public ResponseEntity<List<ScriptVO<?>>> executeScript(@PathVariable("xid") @Valid @XssProtect String xid, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("GET::/api/scripts/execute");
        User user = Common.getUser(request);

        if (user != null && user.isAdmin()) {
            ScriptVO<?> script = scriptService.getScript(xid);
            if (script != null) {
                try {
                    ScriptRT rt = script.createScriptRT();
                    rt.execute();
                } catch (Exception ex) {
                    throw new InternalServerErrorException(ex, request.getRequestURI());
                }
            } else {
                throw new NotFoundException(xid, request.getRequestURI());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @PostMapping(value = "/execute-test")
    public ResponseEntity<String> executeScriptTest(@RequestBody @Valid JsonScript scriptJson, HttpServletRequest request) {
        LOG.info("GET::/api/scripts/execute-test");
        User user = Common.getUser(request);

        if (user != null && user.isAdmin()) {
            ScriptRT rt;
            try {
                ScriptVO<?> script = createScriptFromBody(scriptJson, user, dataPointService);
                rt = script.createScriptRT();
                rt.execute();
            } catch (Exception e) {
                throw new InternalServerErrorException(e, request.getRequestURI());
            }
            return new ResponseEntity<>("ok", HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteScript(@PathVariable Integer id, HttpServletRequest request) {
        LOG.info("DELETE::/api/scripts");
        User user = Common.getUser(request);

        if (user != null && user.isAdmin()) {
            String error = validateScriptDelete(id);
            if (!error.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                errors.put("errors", error);
                throw new BadRequestException(errors, request.getRequestURI());
            }
            try {
                scriptService.deleteScript(id);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ex, request.getRequestURI());
            }
            return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @PostMapping(value = "/validateXid")
    public ResponseEntity<Map<String, String>> validateScriptXid(@RequestBody @Valid JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("POST::/api/scripts/validateXid");
        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            Map<String, String> response = new HashMap<>();
            boolean present;
            try {
                present = isScriptPresent(jsonBodyRequest.getXid(), scriptService);
            } catch (Exception e) {
                throw new InternalServerErrorException(e, request.getRequestURI());
            }
            response.put("xidRepeated", String.valueOf(present));
            return new ResponseEntity<>( response, HttpStatus.OK);

        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Map<String, Object>> saveScript(@RequestBody @Valid JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("POST::/api/scripts/save");
        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            String error = ScriptsApiUtils.validateScriptBody(jsonBodyRequest);
            Map<String, Object> response = new HashMap<>();
            if (!error.isEmpty()) {
                response.put("errors", error);
                throw new BadRequestException(request.getRequestURI(), response);
            }
            if (isScriptPresent(jsonBodyRequest.getXid(), scriptService)) {
                response.put("errors", "This XID is already in use");
                throw new BadRequestException(request.getRequestURI(), response);
            }
            String pointsError = validatePointsOnContext(jsonBodyRequest.getPointsOnContext(), dataPointService);
            if (!pointsError.isEmpty()) {
                response.put("errors", pointsError);
                throw new NotFoundException(response, request.getRequestURI());
            }
            ContextualizedScriptVO vo;
            try {
                vo = createScriptFromBody(jsonBodyRequest, user, dataPointService);
                scriptService.saveScript(vo);
            } catch (Exception e) {
                throw new InternalServerErrorException(e, request.getRequestURI());
            }
            response.put("scriptId", vo.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Map<String, String>> updateScript(@RequestBody @Valid JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("PUT::/api/scripts/update");
        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            Map<String, String> response = new HashMap<>();
            String error = validateScriptUpdate(jsonBodyRequest);
            if (!error.isEmpty()) {
                response.put("errors", error);
                return ResponseEntity.badRequest().body(response);
            }
            return findAndUpdateScript(jsonBodyRequest, request);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    @GetMapping(value = "/generateXid")
    public ResponseEntity<String> getUniqueXid(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user != null && user.isAdmin()) {
            String xid;
            try {
                xid = scriptService.generateUniqueXid();
            } catch (Exception e) {
                throw new InternalServerErrorException(e, request.getRequestURI());
            }
            return new ResponseEntity<>(xid, HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    private ResponseEntity<Map<String, String>> findAndUpdateScript(JsonScript body, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("errors", "Script not found");
        return getScript(body.getId(), scriptService).map(toUpdate -> updateScriptBody(toUpdate, body))
                .orElseThrow(() -> new NotFoundException(request.getRequestURI(), errors));
    }

    private ResponseEntity<Map<String, String>> updateScriptBody(ContextualizedScriptVO toUpdate, JsonScript body) {
        Map<String, String> response = new HashMap<>();
        if (isXidChanged(toUpdate.getXid(), body.getXid()) &&
                isScriptPresent(body.getXid(), scriptService)) {
            response.put("errors", "This XID is already in use");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String pointsError = validatePointsOnContext(body.getPointsOnContext(), dataPointService);
        if (!pointsError.isEmpty()) {
            response.put("errors", pointsError);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        updateValueScript(toUpdate, body, dataPointService);
        scriptService.saveScript(toUpdate);
        response.put("status", "updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
