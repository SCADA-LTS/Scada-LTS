package org.scada_lts.web.mvc.api.components.synoptic_panel;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.service.SynopticPanelService;
import org.scada_lts.service.model.SynopticPanel;
import org.scada_lts.utils.ValidationUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for Synoptic Panels
 * <p>
 * Using the REST API best practices for naming endpoints
 * https://nordicapis.com/10-best-practices-for-naming-api-endpoints/
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
@Controller
@RequestMapping(path = "/api/synoptic-panels")
public class SynopticPanelAPI {

    private static final Log LOG = LogFactory.getLog(SynopticPanelAPI.class);

    private final SynopticPanelService synopticPanelService;

    public SynopticPanelAPI(SynopticPanelService synopticPanelService) {
        this.synopticPanelService = synopticPanelService;
    }

    /**
     * Use universal ScadaObjectIdentifier Class
     * to send a list of objects without details.
     *
     * @param request HTTP request with user data.
     * @return SynopticPanelList
     */
    @GetMapping(value = "/")
    public ResponseEntity<List<ScadaObjectIdentifier>> getSynopticPanels(HttpServletRequest request) {
        LOG.info("GET:" + request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(synopticPanelService.getSimpleSynopticPanelsList(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SynopticPanel> getSynopticPanel(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("GET:" + request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return synopticPanelService.getSynopticPanel(id)
                        .map(panel -> new ResponseEntity<>(panel, HttpStatus.OK))
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/generateXid")
    public ResponseEntity<String> generateXid(HttpServletRequest request) {
        LOG.info("GET:" + request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(synopticPanelService.generateXid(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/")
    public ResponseEntity<SynopticPanel> createSynopticPanel(HttpServletRequest request, @RequestBody SynopticPanel requestBody) {
        LOG.info("POST:" + request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if(user != null) {
                String error = ValidationUtils.validSvg(requestBody.getVectorImage());
                if(!StringUtils.isEmpty(error)) {
                    return ResponseEntity.badRequest().build();
                }
                SynopticPanel result = synopticPanelService.createSynopticPanel(requestBody);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/")
    public ResponseEntity<SynopticPanel> updateSynopticPanel(HttpServletRequest request, @RequestBody SynopticPanel requestBody) {
        LOG.info("PUT:" + request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = ValidationUtils.validSvg(requestBody.getVectorImage());
                if(!StringUtils.isEmpty(error)) {
                    return ResponseEntity.badRequest().build();
                }
                SynopticPanel result = synopticPanelService.updateSynopticPanel(requestBody);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteSynopticPanel(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("DELETE:" + request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(synopticPanelService.deleteSynopticPanel(id) != -1) {
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


