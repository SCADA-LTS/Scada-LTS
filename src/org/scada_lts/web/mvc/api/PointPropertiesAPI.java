package org.scada_lts.web.mvc.api;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.view.ImplDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;


/**
 * Hellper class
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */


/**
 * Controller for API point properties
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller
public class PointPropertiesAPI {

    private static final Log LOG = LogFactory.getLog(PointPropertiesAPI.class);

    private DataPointService dataPointService = new DataPointService();


    @RequestMapping(value = "/api/point_properties/getPropertiesBaseOnId/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getPropertiesBaseOnId(@PathVariable("id") int id, HttpServletRequest request) {
        LOG.info("/api/point_properties/getPropertiesBaseOnId/{id} id:" + id);

        try {
            // check may use watch list
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dpvo = dataPointService.getDataPoint(id);
                String json = null;
                ObjectMapper mapper = new ObjectMapper();

                ResourceBundle bundle = Common.getBundle();

                class PropertiesPointToJSON implements Serializable {

                    private static final long serialVersionUID = 1L;

                    private int loggingType;
                    private int intervalLoggingPeriod;
                    private int intervalLoggingPeriodType;
                    private int intervalLoggingType;
                    private int purgeType;
                    private int purgePeriod;
                    private int defaultCacheSize;
                    private String typeKey;
                    private TextRenderer textRenderer;
                    private ImplDefinition def;
                    private ChartRenderer chartRenderer;
                    private int engineeringUnits;
                    private double tolerance;
                    private boolean isDiscardExtremeValues;
                    private double discardLowLimit;
                    private double discardHighLimit;


                    public PropertiesPointToJSON(
                            int loggingType,
                            int intervalLoggingPeriod,
                            int intervalLoggingPeriodType,
                            int purgeType,
                            int purgePeriod,
                            int defaultCacheSize,
                            String typeKey,
                            TextRenderer textRenderer,
                            ImplDefinition def,
                            ChartRenderer chartRenderer,
                            int engineeringUnits,
                            double tolerance,
                            int intervalLoggingType,
                            boolean isDiscardExtremeValues,
                            double discardLowLimit,
                            double discardHighLimit

                    ) {
                        this.loggingType = loggingType;
                        this.intervalLoggingPeriod = intervalLoggingPeriod;
                        this.intervalLoggingPeriodType = intervalLoggingPeriodType;
                        this.purgeType = purgeType;
                        this.purgePeriod =  purgePeriod;
                        this.defaultCacheSize = defaultCacheSize;
                        this.typeKey = typeKey;
                        this.textRenderer = textRenderer;
                        this.def = def;
                        this.chartRenderer = chartRenderer;
                        this.engineeringUnits = engineeringUnits;
                        this.tolerance = tolerance;
                        this.intervalLoggingType = intervalLoggingType;
                        this.isDiscardExtremeValues = isDiscardExtremeValues;
                        this.discardLowLimit = discardLowLimit;
                        this.discardHighLimit = discardHighLimit;

                    }

                    public int getLoggingType() {
                        return loggingType;
                    }

                    public void setLoggingType(int loggingType) {
                        this.loggingType = loggingType;
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

                    public int getPurgeType() {
                        return purgeType;
                    }

                    public void setPurgeType(int purgeType) {
                        this.purgeType = purgeType;
                    }

                    public int getDefaultCacheSize() {
                        return defaultCacheSize;
                    }

                    public void setDefaultCacheSize(int defaultCacheSize) {
                        this.defaultCacheSize = defaultCacheSize;
                    }

                    public String getTypeKey() {
                        return typeKey;
                    }

                    public void setTypeKey(String typeKey) {
                        this.typeKey = typeKey;
                    }

                    public TextRenderer getTextRenderer() {
                        return textRenderer;
                    }

                    public void setTextRenderer(TextRenderer textRenderer) {
                        this.textRenderer = textRenderer;
                    }

                    public ImplDefinition getDef() {
                        return def;
                    }

                    public void setDef(ImplDefinition def) {
                        this.def = def;
                    }

                    public ChartRenderer getChartRenderer() {
                        return chartRenderer;
                    }

                    public void setChartRenderer(ChartRenderer chartRenderer) {
                        this.chartRenderer = chartRenderer;
                    }

                    public int getEngineeringUnits() {
                        return engineeringUnits;
                    }

                    public void setEngineeringUnits(int engineeringUnits) {
                        this.engineeringUnits = engineeringUnits;
                    }

                    public double getTolerance() {
                        return tolerance;
                    }

                    public void setTolerance(double tolerance) {
                        this.tolerance = tolerance;
                    }

                    public int getIntervalLoggingType() {
                        return intervalLoggingType;
                    }

                    public void setIntervalLoggingType(int intervalLoggingType) {
                        this.intervalLoggingType = intervalLoggingType;
                    }

                    public boolean isDiscardExtremeValues() {
                        return isDiscardExtremeValues;
                    }

                    public void setDiscardExtremeValues(boolean discardExtremeValues) {
                        isDiscardExtremeValues = discardExtremeValues;
                    }

                    public double getDiscardLowLimit() {
                        return discardLowLimit;
                    }

                    public void setDiscardLowLimit(double discardLowLimit) {
                        this.discardLowLimit = discardLowLimit;
                    }

                    public double getDiscardHighLimit() {
                        return discardHighLimit;
                    }

                    public void setDiscardHighLimit(double discardHighLimit) {
                        this.discardHighLimit = discardHighLimit;
                    }

                    public int getPurgePeriod() {
                        return purgePeriod;
                    }

                    public void setPurgePeriod(int purgePeriod) {
                        this.purgePeriod = purgePeriod;
                    }
                }

                PropertiesPointToJSON p = new PropertiesPointToJSON(
                        dpvo.getLoggingType(),
                        dpvo.getIntervalLoggingPeriod(),
                        dpvo.getIntervalLoggingPeriodType(),
                        dpvo.getPurgeType(),
                        dpvo.getPurgePeriod(),
                        dpvo.getDefaultCacheSize(),
                        dpvo.getTypeKey(),
                        dpvo.getTextRenderer(),
                        dpvo.getTextRenderer().getDef(),
                        dpvo.getChartRenderer(),
                        dpvo.getEngineeringUnits(),
                        dpvo.getTolerance(),
                        dpvo.getIntervalLoggingType(),
                        dpvo.isDiscardExtremeValues(),
                        dpvo.getDiscardLowLimit(),
                        dpvo.getDiscardHighLimit()
                );

                json = mapper.writeValueAsString(p);

                return new ResponseEntity<String>(json, HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/api/point_properties/getProperties/{xid}", method = RequestMethod.GET)
    public ResponseEntity<String> getProperties(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("/api/point_properties/getProperties/{xid} id:" + xid);

        try {
            // check may use watch list
            User user = Common.getUser(request);
            if (user != null) {
                DataPointVO dpvo = dataPointService.getDataPoint(xid);
                String json = null;
                ObjectMapper mapper = new ObjectMapper();

                ResourceBundle bundle = Common.getBundle();

                class PropertiesPointToJSON implements Serializable {

                    private static final long serialVersionUID = 1L;

                    private String chartColour;
                    private ChartRenderer chartRenderer;
                    private String descConfiguration;
                    private String dataSourceName;
                    private String dataSourceXId;
                    private String dataTypeMessage;
                    private String deviceName;
                    private Double discardHighLimit;
                    private Double discardLowLimit;
                    private int engineeringUnits;
                    private String extendedName;
                    private int intervalLoggingPeriod;
                    private int intervalLoggingPeriodType;
                    private int intervalLoggingType;
                    private String name;
                    private int purgePeriod;
                    private int purgeType;
                    private TextRenderer textRenderer;
                    private double tolerance;
                    private String typeKey;

                    public PropertiesPointToJSON(String chartColour, ChartRenderer chartRenderer, String descConfiguration, String dataSourceName, String dataSourceXId,
                                                 String dataTypeMessage, String deviceName, Double discardHighLimit, Double discardLowLimit, int engineeringUnits, String extendedName, int intervalLoggingPeriod,
                                                 int intervalLoggingPeriodType, int intervalLoggingType, String name, int purgePeriod, int purgeType, TextRenderer textRenderer, double tolerance, String typeKey) {

                        this.chartColour = chartColour;
                        this.chartRenderer = chartRenderer;
                        this.descConfiguration = descConfiguration;
                        this.dataSourceName = dataSourceName;
                        this.dataSourceXId = dataSourceXId;
                        this.dataTypeMessage = dataTypeMessage;
                        this.deviceName = deviceName;
                        this.discardHighLimit = discardHighLimit;
                        this.discardLowLimit = discardLowLimit;
                        this.engineeringUnits = engineeringUnits;
                        this.extendedName = extendedName;
                        this.intervalLoggingPeriod = intervalLoggingPeriod;
                        this.intervalLoggingPeriodType = intervalLoggingPeriodType;
                        this.intervalLoggingType = intervalLoggingType;
                        this.name = name;
                        this.purgePeriod = purgePeriod;
                        this.purgeType = purgeType;
                        this.textRenderer = textRenderer;
                        this.tolerance = tolerance;
                        this.typeKey = typeKey;
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

                }

                //dpvo.isDiscardExtremeValues()


                PropertiesPointToJSON p = new PropertiesPointToJSON(
                        dpvo.getChartColour(),
                        dpvo.getChartRenderer(),
                        dpvo.getConfigurationDescription().getLocalizedMessage(bundle),
                        dpvo.getDataSourceName(),
                        dpvo.getDataSourceXid(),

                        dpvo.getDataTypeMessage().getLocalizedMessage(bundle),
                        dpvo.getDeviceName(),
                        dpvo.getDiscardHighLimit(),
                        dpvo.getDiscardLowLimit(),
                        dpvo.getEngineeringUnits(),
                        dpvo.getExtendedName(),
                        dpvo.getIntervalLoggingPeriod(),

                        dpvo.getIntervalLoggingPeriodType(),
                        dpvo.getIntervalLoggingType(),
                        dpvo.getName(),
                        dpvo.getPurgePeriod(),
                        dpvo.getPurgeType(),
                        dpvo.getTextRenderer(),
                        dpvo.getTolerance(),
                        dpvo.getTypeKey());

                json = mapper.writeValueAsString(p);

                return new ResponseEntity<String>(json, HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

    }
}
