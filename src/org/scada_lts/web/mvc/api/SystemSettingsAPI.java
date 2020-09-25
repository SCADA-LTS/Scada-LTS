package org.scada_lts.web.mvc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.beans.IntegerPair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.web.mvc.api.json.JsonSettingsEmail;
import org.scada_lts.web.mvc.api.json.JsonSettingsHttp;
import org.scada_lts.web.mvc.api.json.JsonSettingsMisc;
import org.scada_lts.web.mvc.api.json.JsonSettingsSystemInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for SystemSettings page
 *
 * @author Radoslaw Jajko
 */
@Controller
@RequestMapping(path = "/api/systemSettings")
public class SystemSettingsAPI {

    private static final Log LOG = LogFactory.getLog(SystemSettingsAPI.class);

    private static final String SAVED_MSG = "saved";

    @Resource
    private SystemSettingsService systemSettingsService;

    @RequestMapping(value = "/getSettings", method = RequestMethod.GET)
    public ResponseEntity<String> getSettings(HttpServletRequest request) {
        LOG.info("/api/systemSettings/getSettings");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String json = mapper.writeValueAsString(systemSettingsService.getSettings());
                    return new ResponseEntity<>(json, HttpStatus.OK);
                } catch (JsonProcessingException e) {
                    LOG.error(e);
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getEmail", produces = "application/json")
    public ResponseEntity<JsonSettingsEmail> getEmail(HttpServletRequest request) {
        LOG.info("/api/systemSettings/getEmail");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(systemSettingsService.getEmailSettings(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/saveEmail", consumes = "application/json")
    public ResponseEntity<String> saveEmail(HttpServletRequest request, @RequestBody JsonSettingsEmail jsonSettingsEmail) {
        LOG.info("/api/systemSettings/saveEmail");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.saveEmailSettings(jsonSettingsEmail);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getHttp", produces = "application/json")
    public ResponseEntity<JsonSettingsHttp> getHttp(HttpServletRequest request) {
        LOG.info("/api/systemSettings/getHttp");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(systemSettingsService.getHttpSettings(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/saveHttp", consumes = "application/json")
    public ResponseEntity<String> saveHttp(HttpServletRequest request, @RequestBody JsonSettingsHttp jsonSettingsHttp) {
        LOG.info("/api/systemSettings/saveHttp");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.saveHttpSettings(jsonSettingsHttp);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getMisc", produces = "application/json")
    public ResponseEntity<JsonSettingsMisc> getMisc(HttpServletRequest request){
        LOG.info("/api/systemSettings/getMisc");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(systemSettingsService.getMiscSettings(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/saveMisc", consumes = "application/json")
    public ResponseEntity<String> saveMisc(HttpServletRequest request, @RequestBody JsonSettingsMisc jsonSettingsMisc) {
        LOG.info("/api/systemSettings/saveMisc");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.saveMiscSettings(jsonSettingsMisc);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param request - request with user data
     * @param eventAlarmLevels - Audit Events with ID from 1 to 8 (AuditEventType)
     *                           with alarm levels from 0 to 4 (AlarmLevels)
     * @return Response with HTTP status
     */
    @PostMapping(value = "/saveAuditEventAlarmLevels", consumes = "application/json")
    public ResponseEntity<String> saveAuditEventAlarmLevels(HttpServletRequest request, @RequestBody List<IntegerPair> eventAlarmLevels) {
        LOG.info("/api/systemSettings/saveAuditEventAlarmLevels");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.saveAuditEventAlarmLevels(eventAlarmLevels);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param request - request with user data
     * @param eventSystemLevels - Audit Events with ID from 1 to 10 (SystemEventType)
     *                            Exclude ID=5 it does not exist.
     *                            With alarm levels from 0 to 4 (AlarmLevels)
     * @return Response with HTTP status
     */
    @PostMapping(value = "/saveSystemEventAlarmLevels", consumes = "application/json")
    public ResponseEntity<String> saveSystemEventAlarmLevels(HttpServletRequest request, @RequestBody List<IntegerPair> eventSystemLevels) {
        LOG.info("/api/systemSettings/saveSystemEventAlarmLevels");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.saveSystemEventAlarmLevels(eventSystemLevels);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getSystemInfo", produces = "application/json")
    public ResponseEntity<JsonSettingsSystemInfo> getSystemInfo (HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(systemSettingsService.getSystemInfoSettings(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/saveSystemInfo", consumes = "application/json")
    public ResponseEntity<String> saveSystemInfo (HttpServletRequest request, @RequestBody JsonSettingsSystemInfo jsonSettingsSystemInfo) {
        LOG.info("/api/systemSettings/saveSystemInfo");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.saveSystemInfoSettings(jsonSettingsSystemInfo);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getDatabaseType", produces = "application/json")
    public ResponseEntity<String> getDatabaseType(HttpServletRequest request) {
        LOG.info("/api/systemSettings/getDatabaseType");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String json = "{\"databaseType\":\"" + systemSettingsService.getDatabaseType() + "\"";
                return new ResponseEntity<>(json, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/saveDatabaseType/{databaseType}")
    public ResponseEntity<String> saveDatabaseType(HttpServletRequest request, @PathVariable("databaseType") String databaseType) {
        LOG.info("/api/systemSettings/saveDatabaseType");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                systemSettingsService.setDatabaseType(databaseType);
                return new ResponseEntity<>(SAVED_MSG, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
