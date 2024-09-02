package org.scada_lts.web.mvc.api;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.event.EventTextRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.json.JsonBinaryEventTextRenderer;
import org.scada_lts.web.mvc.api.json.JsonPointProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;

import static org.scada_lts.utils.PointPropertiesApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.validId;

/**
 * Helper class
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */


/**
 * Controller for API point properties
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller
@RequestMapping(path = "/api/point_properties")
public class PointPropertiesAPI {

    private static final Log LOG = LogFactory.getLog(PointPropertiesAPI.class);

    private final DataPointService dataPointService;

    private static final String SAVED_MSG = "saved";

    public PointPropertiesAPI(DataPointService dataPointService) {
        this.dataPointService = dataPointService;
    }

    @RequestMapping(value = "/getPropertiesBaseOnId/{id}", method = RequestMethod.GET)
    public ResponseEntity<PropertiesPointToJSON> getPropertiesBaseOnId(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("/api/point_properties/getPropertiesBaseOnId/{id} id:" + id);

        try {
            // check may use watch list
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dpvo = dataPointService.getDataPoint(id);

                PropertiesPointToJSON p = new PropertiesPointToJSON(
                        dpvo.getDescription(),
                        dpvo.getLoggingType(),
                        dpvo.getIntervalLoggingPeriod(),
                        dpvo.getIntervalLoggingPeriodType(),
                        dpvo.getPurgeType(),
                        dpvo.getPurgePeriod(),
                        dpvo.getDefaultCacheSize(),
                        dpvo.getTypeKey(),
                        dpvo.getTextRenderer(),
                        dpvo.getEventTextRenderer(),
                        dpvo.getTextRenderer().getDef(),
                        dpvo.getChartRenderer(),
                        dpvo.getEngineeringUnits(),
                        dpvo.getTolerance(),
                        dpvo.getIntervalLoggingType(),
                        dpvo.isDiscardExtremeValues(),
                        dpvo.getDiscardLowLimit(),
                        dpvo.getDiscardHighLimit(),
                        dpvo.getPointLocator().getDataTypeId(),
                        dpvo.getPurgeStrategy(),
                        dpvo.getPurgeValuesLimit()
                );

                return new ResponseEntity<>(p, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/getProperties/{xid}", method = RequestMethod.GET)
    public ResponseEntity<PropertiesPointToJSON> getProperties(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("/api/point_properties/getProperties/{xid} id:" + xid);

        try {
            // check may use watch list
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dpvo = dataPointService.getDataPoint(xid);
                ResourceBundle bundle = Common.getBundle(request);

                PropertiesPointToJSON p = new PropertiesPointToJSON(
                        dpvo.getChartColour(),
                        dpvo.getChartRenderer(),
                        dpvo.getConfigurationDescription().getLocalizedMessage(bundle),
                        dpvo.getDataSourceName(),
                        dpvo.getDataSourceXid(),

                        dpvo.getDataTypeMessage().getLocalizedMessage(bundle),
                        dpvo.getDeviceName(),
                        dpvo.getDescription(),
                        dpvo.getDiscardHighLimit(),
                        dpvo.getDiscardLowLimit(),
                        dpvo.getEngineeringUnits(),
                        dpvo.getExtendedName(),
                        dpvo.getEventTextRenderer(),
                        dpvo.getIntervalLoggingPeriod(),

                        dpvo.getIntervalLoggingPeriodType(),
                        dpvo.getIntervalLoggingType(),
                        dpvo.getName(),
                        dpvo.getPurgePeriod(),
                        dpvo.getPurgeType(),
                        dpvo.getTextRenderer(),
                        dpvo.getTolerance(),
                        dpvo.getTypeKey(),
                        dpvo.getPurgeStrategy(),
                        dpvo.getPurgeValuesLimit());

                return new ResponseEntity<>(p, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping(value = "/updateProperties")
    public ResponseEntity<Map<String, String>> updatePointProperties(@RequestParam(required = false) Integer id,
                                                        @RequestParam(required = false) String xid,
                                                        HttpServletRequest request,
                                                        @RequestBody JsonPointProperties body) {
        try {
            User user = Common.getUser(request);
            if (user != null) {
                Map<String, String> response = new HashMap<>();
                String error = validPointProperties(id, xid, body);
                if (!error.isEmpty()) {
                    response.put("errors", error);
                    return ResponseEntity.badRequest().body(response);
                }

                return getDataPointByIdOrXid(id, xid, dataPointService).map(dataPoint -> {
                    updateValuePointProperties(dataPoint, body);
                    dataPointService.updateDataPoint(dataPoint);
                    Common.ctx.getRuntimeManager().saveDataPoint(dataPoint);
                    response.put("status", SAVED_MSG);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }).orElseGet(() -> {
                    response.put("errors", "dataPoint not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                });

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getPointDescription", produces = "application/json")
    public ResponseEntity<Map<String, String>> getPointDescription(@RequestParam(required = false) Integer id,
                                                      @RequestParam(required = false) String xid,
                                                      HttpServletRequest request) {
        LOG.info("/api/point_properties/getPointDescription");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validId(id, xid);
                if(!error.isEmpty()) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("errors", error);
                    return ResponseEntity.badRequest().body(errors);
                }
                return getPointDescription(id, xid);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getBinaryEventRenderer", produces = "application/json")
    public ResponseEntity<JsonBinaryEventTextRenderer> getBinaryEventRenderer(@RequestParam(required = false) Integer id,
                                                                              @RequestParam(required = false) String xid,
                                                                              @RequestParam Integer value,
                                                                              HttpServletRequest request) {
        LOG.info("/api/point_properties/getBinaryEventRenderer");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                String error = validId(id, xid);
                if(!error.isEmpty() || value == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                return getDataPointByIdOrXid(id, xid, dataPointService)
                        .map(a -> new ResponseEntity<>(dataPointService
                                .getBinaryEventTextRenderer(a, value), HttpStatus.OK))
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(value = "/{id}/purgeNowPeriod")
    public ResponseEntity<Map<String, String>> purgeNowPeriodDataPointValues(@PathVariable("id") Integer id,
                                                       @RequestParam Integer type,
                                                       @RequestParam Integer period,
                                                       HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                String error = validPurgeTypeAndPeriod(type,period);
                if(!error.isEmpty()) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("errors", error);
                    return ResponseEntity.badRequest().body(errors);
                }
                return purgeNowPeriodDataPointValues(id, type, period);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "/{id}/purgeNowAll")
    public ResponseEntity<Map<String, String>> purgeNowAllDataPointValues(@PathVariable("id") Integer id, HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                return purgeNowAllDataPointValues(id);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "/{id}/purgeNowLimit")
    public ResponseEntity<Map<String, String>> purgeNowWithLimitDataPointValues(@PathVariable("id") Integer id,
                                                                @RequestParam Integer limit,
                                                                HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                String error = validPurgeLimit(limit);
                if(!error.isEmpty()) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("errors", error);
                    return ResponseEntity.badRequest().body(errors);
                }
                return purgeNowWithLimitDataPointValues(id, limit);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "/{id}/clearcache")
    public ResponseEntity<String> clearDataPointCache(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                DataPointVO point = dataPointService.getDataPoint(id);
                DataPointRT rt = Common.ctx.getRuntimeManager().getDataPoint(point.getId());
                if(rt != null) {
                    rt.resetValues();
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("DataPointRT not exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "/{id}/toggle")
    public ResponseEntity<Map<String, String>> toggleDataPoint(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                Map<String, String> response = new HashMap<>();
                DataPointVO point = dataPointService.getDataPoint(id);
                RuntimeManager rm = Common.ctx.getRuntimeManager();
                point.setEnabled(!point.isEnabled());
                rm.saveDataPoint(point);

                if(point.isEnabled()) {
                    response.put("enabled", "true");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.put("enabled", "false");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> getPointDescription(Integer id, String xid) {
        Map<String, String> response = new HashMap<>();
        getDataPointByIdOrXid(id, xid, dataPointService)
                .ifPresent(a -> response.put("description", a.getDescription()));
        if(response.isEmpty()) {
            response.put("errors", "dataPoint not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, String>> purgeNowPeriodDataPointValues(Integer id, Integer type, Integer period) {
        RuntimeManager rm = Common.ctx.getRuntimeManager();
        Map<String, Long> response = new HashMap<>();
        getDataPointById(id, dataPointService)
                .ifPresent(a -> response.put("deleted", rm.purgeDataPointValues(a.getId(), type, period)));
        return prepareResponseForDataPurge(response);
    }

    private ResponseEntity<Map<String, String>> purgeNowAllDataPointValues(Integer id) {
        RuntimeManager rm = Common.ctx.getRuntimeManager();
        Map<String, Long> response = new HashMap<>();
        getDataPointById(id, dataPointService)
                .ifPresent(a -> response.put("deleted", rm.purgeDataPointValues(a.getId())));
        return prepareResponseForDataPurge(response);
    }

    private ResponseEntity<Map<String, String>> purgeNowWithLimitDataPointValues(Integer id, Integer limit) {
        RuntimeManager rm = Common.ctx.getRuntimeManager();
        Map<String, Long> response = new HashMap<>();
        getDataPointById(id, dataPointService)
                .ifPresent(a -> response.put("deleted", rm.purgeDataPointValuesWithLimit(a.getId(), limit)));
        return prepareResponseForDataPurge(response);
    }

    private ResponseEntity<Map<String, String>> prepareResponseForDataPurge(Map<String, Long> response) {
        if(response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue()))), HttpStatus.OK);
    }

    static class PropertiesPointToJSON implements Serializable {

        private static final long serialVersionUID = 1L;

        private String chartColour;
        private ChartRenderer chartRenderer;
        private String descConfiguration;
        private String dataSourceName;
        private String dataSourceXId;
        private String dataTypeMessage;
        private String deviceName;
        private String description;
        private Double discardHighLimit;
        private Double discardLowLimit;
        private int engineeringUnits;
        private String extendedName;
        private EventTextRenderer eventTextRenderer;
        private int intervalLoggingPeriod;
        private int intervalLoggingPeriodType;
        private int intervalLoggingType;
        private String name;
        private int purgePeriod;
        private int purgeType;
        private TextRenderer textRenderer;
        private double tolerance;
        private String typeKey;
        private int purgeStrategy;
        private int purgeValuesLimit;
        private int loggingType;
        private int defaultCacheSize;
        private ImplDefinition def;
        private boolean isDiscardExtremeValues;
        private int dataTypeId;

        public PropertiesPointToJSON(
                String description,
                int loggingType,
                int intervalLoggingPeriod,
                int intervalLoggingPeriodType,
                int purgeType,
                int purgePeriod,
                int defaultCacheSize,
                String typeKey,
                TextRenderer textRenderer,
                EventTextRenderer eventTextRenderer,
                ImplDefinition def,
                ChartRenderer chartRenderer,
                int engineeringUnits,
                double tolerance,
                int intervalLoggingType,
                boolean isDiscardExtremeValues,
                double discardLowLimit,
                double discardHighLimit,
                int dataTypeId,
                int purgeStrategy,
                int purgeValuesLimit
        ) {
            this.description = description;
            this.loggingType = loggingType;
            this.intervalLoggingPeriod = intervalLoggingPeriod;
            this.intervalLoggingPeriodType = intervalLoggingPeriodType;
            this.purgeType = purgeType;
            this.purgePeriod =  purgePeriod;
            this.defaultCacheSize = defaultCacheSize;
            this.typeKey = typeKey;
            this.textRenderer = textRenderer;
            this.eventTextRenderer = eventTextRenderer;
            this.def = def;
            this.chartRenderer = chartRenderer;
            this.engineeringUnits = engineeringUnits;
            this.tolerance = tolerance;
            this.intervalLoggingType = intervalLoggingType;
            this.isDiscardExtremeValues = isDiscardExtremeValues;
            this.discardLowLimit = discardLowLimit;
            this.discardHighLimit = discardHighLimit;
            this.dataTypeId = dataTypeId;
            this.purgeStrategy = purgeStrategy;
            this.purgeValuesLimit = purgeValuesLimit;
        }

        public PropertiesPointToJSON(String chartColour, ChartRenderer chartRenderer, String descConfiguration, String dataSourceName, String dataSourceXId,
                                     String dataTypeMessage, String deviceName, String description, Double discardHighLimit, Double discardLowLimit, int engineeringUnits, String extendedName, EventTextRenderer eventTextRenderer,
                                     int intervalLoggingPeriod, int intervalLoggingPeriodType, int intervalLoggingType, String name, int purgePeriod, int purgeType, TextRenderer textRenderer, double tolerance, String typeKey,
                                     int purgeStrategy, int purgeValuesLimit) {

            this.chartColour = chartColour;
            this.chartRenderer = chartRenderer;
            this.descConfiguration = descConfiguration;
            this.dataSourceName = dataSourceName;
            this.dataSourceXId = dataSourceXId;
            this.dataTypeMessage = dataTypeMessage;
            this.deviceName = deviceName;
            this.description = description;
            this.discardHighLimit = discardHighLimit;
            this.discardLowLimit = discardLowLimit;
            this.engineeringUnits = engineeringUnits;
            this.extendedName = extendedName;
            this.eventTextRenderer = eventTextRenderer;
            this.intervalLoggingPeriod = intervalLoggingPeriod;
            this.intervalLoggingPeriodType = intervalLoggingPeriodType;
            this.intervalLoggingType = intervalLoggingType;
            this.name = name;
            this.purgePeriod = purgePeriod;
            this.purgeType = purgeType;
            this.textRenderer = textRenderer;
            this.tolerance = tolerance;
            this.typeKey = typeKey;
            this.purgeStrategy = purgeStrategy;
            this.purgeValuesLimit = purgeValuesLimit;
        }

        public String getChartColour() {
            return chartColour;
        }

        public void setChartColour(String chartColour) {
            this.chartColour = chartColour;
        }

        public ChartRenderer getChartRenderer() {
            return chartRenderer;
        }

        public void setChartRenderer(ChartRenderer chartRenderer) {
            this.chartRenderer = chartRenderer;
        }

        public String getDescConfiguration() {
            return descConfiguration;
        }

        public void setDescConfiguration(String descConfiguration) {
            this.descConfiguration = descConfiguration;
        }

        public String getDataSourceName() {
            return dataSourceName;
        }

        public void setDataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
        }

        public String getDataSourceXId() {
            return dataSourceXId;
        }

        public void setDataSourceXId(String dataSourceXId) {
            this.dataSourceXId = dataSourceXId;
        }

        public String getDataTypeMessage() {
            return dataTypeMessage;
        }

        public void setDataTypeMessage(String dataTypeMessage) {
            this.dataTypeMessage = dataTypeMessage;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getDiscardHighLimit() {
            return discardHighLimit;
        }

        public void setDiscardHighLimit(Double discardHighLimit) {
            this.discardHighLimit = discardHighLimit;
        }

        public Double getDiscardLowLimit() {
            return discardLowLimit;
        }

        public void setDiscardLowLimit(Double discardLowLimit) {
            this.discardLowLimit = discardLowLimit;
        }

        public int getEngineeringUnits() {
            return engineeringUnits;
        }

        public void setEngineeringUnits(int engineeringUnits) {
            this.engineeringUnits = engineeringUnits;
        }

        public String getExtendedName() {
            return extendedName;
        }

        public void setExtendedName(String extendedName) {
            this.extendedName = extendedName;
        }

        public EventTextRenderer getEventTextRenderer() {
            return eventTextRenderer;
        }

        public void setEventTextRenderer(EventTextRenderer eventTextRenderer) {
            this.eventTextRenderer = eventTextRenderer;
        }

        public int getIntervalLoggingPeriod() {
            return intervalLoggingPeriod;
        }

        public void setIntervalLoggingPeriod(int intervalLoggingPeriod) {
            this.intervalLoggingPeriod = intervalLoggingPeriod;
        }

        public int getIntervalLoggingPeriodType() {
            return intervalLoggingPeriodType;
        }

        public void setIntervalLoggingPeriodType(int intervalLoggingPeriodType) {
            this.intervalLoggingPeriodType = intervalLoggingPeriodType;
        }

        public int getIntervalLoggingType() {
            return intervalLoggingType;
        }

        public void setIntervalLoggingType(int intervalLoggingType) {
            this.intervalLoggingType = intervalLoggingType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPurgePeriod() {
            return purgePeriod;
        }

        public void setPurgePeriod(int purgePeriod) {
            this.purgePeriod = purgePeriod;
        }

        public int getPurgeType() {
            return purgeType;
        }

        public void setPurgeType(int purgeType) {
            this.purgeType = purgeType;
        }

        public TextRenderer getTextRenderer() {
            return textRenderer;
        }

        public void setTextRenderer(TextRenderer textRenderer) {
            this.textRenderer = textRenderer;
        }

        public double getTolerance() {
            return tolerance;
        }

        public void setTolerance(double tolerance) {
            this.tolerance = tolerance;
        }

        public String getTypeKey() {
            return typeKey;
        }

        public void setTypeKey(String typeKey) {
            this.typeKey = typeKey;
        }

        public int getPurgeStrategy() {
            return purgeStrategy;
        }

        public void setPurgeStrategy(int purgeStrategy) {
            this.purgeStrategy = purgeStrategy;
        }

        public int getPurgeValuesLimit() {
            return purgeValuesLimit;
        }

        public void setPurgeValuesLimit(int purgeValuesLimit) {
            this.purgeValuesLimit = purgeValuesLimit;
        }

        public int getLoggingType() {
            return loggingType;
        }

        public void setLoggingType(int loggingType) {
            this.loggingType = loggingType;
        }

        public int getDefaultCacheSize() {
            return defaultCacheSize;
        }

        public void setDefaultCacheSize(int defaultCacheSize) {
            this.defaultCacheSize = defaultCacheSize;
        }

        public ImplDefinition getDef() {
            return def;
        }

        public void setDef(ImplDefinition def) {
            this.def = def;
        }

        public boolean isDiscardExtremeValues() {
            return isDiscardExtremeValues;
        }

        public void setDiscardExtremeValues(boolean discardExtremeValues) {
            isDiscardExtremeValues = discardExtremeValues;
        }

        public int getDataTypeId() {
            return dataTypeId;
        }

        public void setDataTypeId(int dataTypeId) {
            this.dataTypeId = dataTypeId;
        }
    }
}
