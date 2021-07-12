package org.scada_lts.web.mvc.api.components;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.web.mvc.api.AggregateSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(path = "/api/amcharts")
public class AmChartValuesAPI {
    private static final Log LOG = LogFactory.getLog(AmChartValuesAPI.class);

    private static final DataPointService dpService = new DataPointService();
    private static final SystemSettingsService systemSettingsService = new SystemSettingsService();

    @GetMapping("/")
    public ResponseEntity<List<Map<String, Double>>> getValuesFromTimeRange(
            @RequestParam String ids,
            @RequestParam long startTs,
            @RequestParam long endTs,
            @RequestParam(required = false, defaultValue = "false")  boolean xid,
            @RequestParam(required = false, defaultValue = "false")  boolean cmp,
            @RequestParam(required = false, defaultValue = "true") boolean configFromSystem,
            AggregateSettings aggregateSettings,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if (user != null) {
                if (configFromSystem) {
                    aggregateSettings = systemSettingsService.getAggregateSettings();
                } else {
                    String errors = SystemSettingsUtils.validateAggregateSettings(aggregateSettings);
                    if (!errors.isEmpty())
                        return ResponseEntity.badRequest().build();
                }
                if (xid) {
                    return getPointValuesFromRangeByXid(ids, startTs, endTs, cmp, aggregateSettings);
                } else {
                    return getPointValuesFromRangeById(ids, startTs, endTs, cmp, aggregateSettings);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-xid")
    public ResponseEntity<List<Map<String, Double>>> getValuesFromTimeRangeByXid(
            @RequestParam String ids,
            @RequestParam long startTs,
            @RequestParam long endTs,
            @RequestParam(required = false, defaultValue = "false")  boolean cmp,
            @RequestParam(required = false, defaultValue = "true") boolean configFromSystem,
            AggregateSettings aggregateSettings,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(configFromSystem) {
                    aggregateSettings = systemSettingsService.getAggregateSettings();
                } else {
                    String errors = SystemSettingsUtils.validateAggregateSettings(aggregateSettings);
                    if(!errors.isEmpty())
                        return ResponseEntity.badRequest().build();
                }
                return getPointValuesFromRangeByXid(ids, startTs, endTs, cmp, aggregateSettings);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-id")
    public ResponseEntity<List<Map<String, Double>>> getValuesFromTimeRangeById(
            @RequestParam String ids,
            @RequestParam long startTs,
            @RequestParam long endTs,
            @RequestParam(required = false, defaultValue = "false")  boolean cmp,
            @RequestParam(required = false, defaultValue = "true") boolean configFromSystem,
            AggregateSettings aggregateSettings,
            HttpServletRequest request
    ) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                if(configFromSystem) {
                    aggregateSettings = systemSettingsService.getAggregateSettings();
                } else {
                    String errors = SystemSettingsUtils.validateAggregateSettings(aggregateSettings);
                    if(!errors.isEmpty())
                        return ResponseEntity.badRequest().build();
                }
                return getPointValuesFromRangeById(ids, startTs, endTs, cmp, aggregateSettings);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<List<Map<String, Double>>> getPointValuesFromRangeById(String ids, long startTs, long endTs, boolean cmp, AggregateSettings aggregateSettings) {
        if(cmp) {
            return new ResponseEntity<>(dpService.getPointValuesToCompareFromRangeId(ids, startTs, endTs, aggregateSettings), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(dpService.getPointValuesFromRangeId(ids, startTs, endTs, aggregateSettings), HttpStatus.OK);
        }
    }

    private ResponseEntity<List<Map<String, Double>>> getPointValuesFromRangeByXid(String ids, long startTs, long endTs, boolean cmp, AggregateSettings aggregateSettings) {
        if(cmp) {
            return new ResponseEntity<>(dpService.getPointValuesToCompareFromRangeXid(ids, startTs, endTs, aggregateSettings), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(dpService.getPointValuesFromRangeXid(ids, startTs, endTs, aggregateSettings), HttpStatus.OK);
        }
    }
}
