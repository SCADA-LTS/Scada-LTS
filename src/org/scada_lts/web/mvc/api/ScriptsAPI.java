package org.scada_lts.web.mvc.api;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ScriptService;
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

}
