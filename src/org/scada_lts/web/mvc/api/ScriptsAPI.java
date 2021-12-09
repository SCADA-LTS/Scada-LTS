package org.scada_lts.web.mvc.api;

import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
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
    public ResponseEntity<Integer> deleteScript(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("DELETE::/api/scripts");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                scriptService.deleteScript(id);
                return new ResponseEntity<Integer>(id, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Map<String, Integer>> saveScript(@RequestBody JsonScript jsonBodyRequest, HttpServletRequest request) {
        LOG.info("POST::/api/scripts/save");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                ContextualizedScriptVO vo = createScriptFromBody(jsonBodyRequest, user);
                scriptService.saveScript(vo);
                Map<String, Integer> response = new HashMap<>();
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
                ContextualizedScriptVO vo = createScriptFromBody(jsonBodyRequest, user);
                scriptService.saveScript(vo);
                return new ResponseEntity<>("Script updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ContextualizedScriptVO createScriptFromBody(JsonScript jsonBodyRequest, User user) {
        ContextualizedScriptVO vo = new ContextualizedScriptVO();
        vo.setId(jsonBodyRequest.getId());
        vo.setXid(jsonBodyRequest.getXid());
        vo.setName(jsonBodyRequest.getName());
        vo.setScript(jsonBodyRequest.getScript());
        vo.setPointsOnContext(convertPointsOnContext(jsonBodyRequest.getPointsOnContext()));
        vo.setObjectsOnContext(convertObjectsOnContext(jsonBodyRequest));
        vo.setUserId(user.getId());
        return vo;
    }

    private List<IntValuePair> convertPointsOnContext(List<ScriptPoint> pointsOnContext) {
        List<IntValuePair> points = new ArrayList<>();
        for (ScriptPoint point : pointsOnContext) {
            DataPointVO dp = dataPointService.getDataPoint(point.getDataPointXid());
            points.add(new IntValuePair(dp.getId(), point.getVarName()));
        }
        return points;
    }

    private List<IntValuePair> convertObjectsOnContext(JsonScript jsonBodyRequest) {
        List<IntValuePair> objects = new ArrayList<>();
        objects.add(new IntValuePair(1, jsonBodyRequest.getDatasourceContext()));
        objects.add(new IntValuePair(2, jsonBodyRequest.getDatapointContext()));
        return objects;
    }
}
