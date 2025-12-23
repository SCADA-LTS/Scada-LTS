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
@RequestMapping(value = {"/api/scripts", "/script"})
public class ScriptsAPI {

    private static final Log LOG = LogFactory.getLog(ScriptsAPI.class);

    private final ScriptService scriptService;
    private final DataPointService dataPointService;

    public ScriptsAPI(ScriptService scriptService, DataPointService dataPointService) {
        this.scriptService = scriptService;
        this.dataPointService = dataPointService;
    }

    /**
     * Retrieve all scripts available in the system (requires admin privileges).
     *
     * @param request the HTTP request containing the authenticated user information; the user must be an admin
     * @return a list of ScriptVO objects representing all scripts
     * @throws UnauthorizedException if the authenticated user is not an admin
     * @throws InternalServerErrorException if an error occurs while fetching scripts
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

    /**
     * Executes the script identified by the provided XID.
     *
     * @param xid the script XID to execute
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @return an empty string in the response body
     * @throws UnauthorizedException if the caller is not an admin or no user is present
     * @throws NotFoundException if no script exists for the given XID
     * @throws InternalServerErrorException if script execution fails
     */
    @PostMapping(value = "/execute/{xid}")
    public ResponseEntity<String> executeScript(@PathVariable("xid") @Valid @XssProtect String xid, HttpServletRequest request, HttpServletResponse response) {
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
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    /**
     * Executes a script constructed from the given JSON body for testing purposes.
     *
     * The caller must be an admin; the script is created from the provided payload and executed immediately.
     *
     * @param scriptJson the JSON representation of the script to create and execute
     * @param request the HTTP request (used to determine the caller and for error context)
     * @return an empty string on success
     * @throws UnauthorizedException if the caller is not an admin
     * @throws InternalServerErrorException if an error occurs while creating or executing the script
     */
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
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    /**
     * Deletes the script identified by the given database id if the requester is an administrator.
     *
     * Validates whether the script may be deleted and performs the deletion; on success returns
     * the deleted script's id.
     *
     * @param id the database id of the script to delete
     * @param request the HTTP request used to resolve the authenticated user and request URI
     * @return the deleted script id as a string
     * @throws BadRequestException if validation fails and deletion must be rejected (contains an errors map)
     * @throws InternalServerErrorException if an unexpected server error occurs while deleting the script
     * @throws UnauthorizedException if the requester is not an administrator
     */
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

    /**
     * Check whether the provided script XID is already in use.
     *
     * @param jsonBodyRequest the JSON payload containing the XID to validate
     * @param request the HTTP servlet request (used to determine the caller and error context)
     * @return a map with key "xidRepeated" set to "true" if a script with the given XID exists, "false" otherwise
     * @throws UnauthorizedException if the caller is not an administrator
     * @throws InternalServerErrorException if an unexpected error occurs while checking XID presence
     */
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

    /**
     * Validate and persist a new script provided in the request body.
     *
     * @param jsonBodyRequest the JSON payload describing the script; XID uniqueness and referenced datapoints are validated
     * @param request the HTTP request used to determine the calling user and to populate error response metadata
     * @return a map containing the key "scriptId" with the newly created script's identifier
     * @throws BadRequestException if the request body fails validation or the XID is already in use
     * @throws NotFoundException if referenced datapoints in the script context cannot be resolved
     * @throws UnauthorizedException if the caller is not an administrator
     * @throws InternalServerErrorException if an unexpected error occurs while creating or saving the script
     */
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

    /**
     * Updates an existing script using the provided JSON payload.
     *
     * @param jsonBodyRequest JSON representation of the script containing updated fields; must include the script identifier
     * @param request         the HTTP request used to resolve the current user and request URI
     * @return a map describing the result: on success contains a status entry (e.g., "updated"); on validation failure contains an "errors" entry with a message
     * @throws UnauthorizedException if the current user is not an administrator
     */
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

    /**
     * Generates a globally unique script XID for creating new scripts; only accessible to admin users.
     *
     * @param request the HTTP servlet request used to resolve the caller
     * @return the generated unique script XID
     * @throws UnauthorizedException if the caller is not an administrator
     * @throws InternalServerErrorException if an error occurs while generating the XID
     */
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

    /**
     * Locate the script identified by the request body and apply updates, returning the update result.
     *
     * @param body    the JSON payload carrying the script id and updated fields
     * @param request the HTTP request (used to provide the request URI when throwing a NotFoundException)
     * @return        a ResponseEntity whose body is a map describing the outcome of the update
     * @throws NotFoundException if no script exists with the id provided in {@code body}
     */
    private ResponseEntity<Map<String, String>> findAndUpdateScript(JsonScript body, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("errors", "Script not found");
        return getScript(body.getId(), scriptService).map(toUpdate -> updateScriptBody(toUpdate, body))
                .orElseThrow(() -> new NotFoundException(request.getRequestURI(), errors));
    }

    /**
     * Apply updates from the provided JsonScript to the existing ContextualizedScriptVO, persist the updated script, and return a result map.
     *
     * @param toUpdate the existing contextualized script to update
     * @param body the incoming script data containing updated fields and context
     * @return a ResponseEntity whose body contains either {@code "status":"updated"} on success or {@code "errors": "<message>"} when validation fails
     */
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