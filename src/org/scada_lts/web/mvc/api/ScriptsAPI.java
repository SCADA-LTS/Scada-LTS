package org.scada_lts.web.mvc.api;

import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.error.EntityNotUniqueException;
import org.scada_lts.errors.ErrorCode;
import org.scada_lts.mango.service.DataPointService;
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
            if (user != null || !user.isAdmin()) {
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

            if (user != null || !user.isAdmin()) {
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
            if (user != null || !user.isAdmin()) {
                new ScriptDao().deleteScript(id);
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<List<ScriptVO<?>>> saveScript(@RequestBody JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("POST::/api/scripts/save");
        try {
            User user = Common.getUser(request);
            if (user != null || !user.isAdmin()) {
                int scriptId = jsonBodyRequest.getId();
                int userId = user.getId();
                if (scriptId != -1) {
                    ScriptVO vo = scriptService.getScript(scriptId);
                    userId = vo.getUserId();
                }

                List<IntValuePair> pointsOnContext = new ArrayList<IntValuePair>();
                List<IntValuePair> objectsOnContext = new ArrayList<IntValuePair>();

//                for (IntValuePair dp:jsonBodyRequest.getPointsOnContext()) {
//                    dataPointService.getDataPoint(id).getXid()
//                    String[] kv = term.split(":");
//                    objectsOnContext.add(new IntValuePair(Integer.parseInt(kv[0]), kv[1]));
//                }

                if (jsonBodyRequest.getDatapointContext() != "") {
                    objectsOnContext.add(new IntValuePair(2, jsonBodyRequest.getDatapointContext()));
                }

                if (jsonBodyRequest.getDatasourceContext() != "") {
                    objectsOnContext.add(new IntValuePair(1, jsonBodyRequest.getDatapointContext()));
                }

                ContextualizedScriptVO vo = new ContextualizedScriptVO();
                vo.setId(scriptId);
                vo.setXid(jsonBodyRequest.getXid());
                vo.setName(jsonBodyRequest.getName());
                vo.setScript(jsonBodyRequest.getScript());
                vo.setPointsOnContext(pointsOnContext);
                vo.setObjectsOnContext(objectsOnContext);
                vo.setUserId(userId);
                scriptService.saveScript(vo);
                return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
