package org.scada_lts.web.mvc.api;

import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.error.EntityNotUniqueException;
import org.scada_lts.errors.ErrorCode;
import org.scada_lts.mango.service.ScriptService;
import org.scada_lts.web.mvc.api.json.JsonScript;
import org.scada_lts.web.mvc.api.json.JsonUser;
import org.scada_lts.web.mvc.api.json.JsonUserPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorExecutionScript;

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

    /**
     * Get Scripts related to specific Data Point
     *
     * @param query       Additional query like limit or offset
     * @param request     HTTP request with user data
     * @return ScriptDTO List
     */
    @GetMapping(value = "/search")
    public ResponseEntity<List<ScriptVO<?>>> getScripts(HttpServletRequest request) {
        LOG.info("GET::/api/scripts/search");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/execute/{xid}")
    public ResponseEntity<List<ScriptVO<?>>> getScripts(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("GET::/api/scripts/execute");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                ScriptVO<?> script = new ScriptDao().getScript(xid);
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
    public ResponseEntity<List<ScriptVO<?>>> deleteScripts(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("DELETE::/api/scripts");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                new ScriptDao().deleteScript(id);
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get Scripts related to specific Data Point
     *
     * @param query       Additional query like limit or offset
     * @param request     HTTP request with user data
     * @return ScriptDTO List
     */
    @PostMapping(value = "/save")
    public ResponseEntity<List<ScriptVO<?>>> saveScript(@RequestBody JsonScript script, HttpServletRequest request) {
        LOG.info("GET::/api/scripts/search");
        try {
            User user = Common.getUser(request);
            if (user != null) {
//                scriptService.saveScript(script);
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
