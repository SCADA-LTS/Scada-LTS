package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.json.JsonPointProperties;

import java.util.Objects;
import java.util.Optional;

import static org.scada_lts.utils.UpdateValueUtils.setIf;
import static org.scada_lts.utils.ValidationUtils.msgIfNonNullAndInvalid;
import static org.scada_lts.utils.ValidationUtils.validId;

public final class PointPropertiesApiUtils {

    private static final Log LOG = LogFactory.getLog(PointPropertiesApiUtils.class);

    private PointPropertiesApiUtils() {}

    public static void updateValuePointProperties(DataPointVO toUpdate, JsonPointProperties source) {
        setIf(source.getName(), toUpdate::setName, a -> !StringUtils.isEmpty(a));
        setIf(source.getDescription(), toUpdate::setDescription, Objects::nonNull);
        setIf(source.getEnabled(), toUpdate::setEnabled, Objects::nonNull);
        setIf(source.getLoggingType(), toUpdate::setLoggingType, Objects::nonNull);
        setIf(source.getIntervalLoggingPeriodType(), toUpdate::setIntervalLoggingPeriodType, Objects::nonNull);
        setIf(source.getIntervalLoggingPeriod(), toUpdate::setIntervalLoggingPeriod, Objects::nonNull);
        setIf(source.getIntervalLoggingType(), toUpdate::setIntervalLoggingType, Objects::nonNull);
        setIf(source.getTolerance(), toUpdate::setTolerance, Objects::nonNull);
        setIf(source.getPurgeType(), toUpdate::setPurgeType, Objects::nonNull);
        setIf(source.getPurgePeriod(), toUpdate::setPurgePeriod, Objects::nonNull);
        setIf(source.getEventTextRenderer(), toUpdate::setEventTextRenderer, Objects::nonNull);
        setIf(source.getTextRenderer(), toUpdate::setTextRenderer, Objects::nonNull);
        setIf(source.getChartRenderer(), toUpdate::setChartRenderer, Objects::nonNull);
        setIf(source.getDefaultCacheSize(), toUpdate::setDefaultCacheSize, Objects::nonNull);
        setIf(source.getDiscardExtremeValues(), toUpdate::setDiscardExtremeValues, Objects::nonNull);
        setIf(source.getDiscardLowLimit(), toUpdate::setDiscardLowLimit, Objects::nonNull);
        setIf(source.getDiscardHighLimit(), toUpdate::setDiscardHighLimit, Objects::nonNull);
        setIf(source.getEngineeringUnits(), toUpdate::setEngineeringUnits, Objects::nonNull);
        setIf(source.getChartColour(), toUpdate::setChartColour, Objects::nonNull);
    }

    public static Optional<DataPointVO> getDataPointByIdOrXid(Integer id, String xid, DataPointService dataPointService) {
        try {
            DataPointVO dataPointVO = null;
            if (id != null) {
                dataPointVO = dataPointService.getDataPoint(id);
            } else if (xid != null) {
                dataPointVO = dataPointService.getDataPoint(xid);
            }
            return Optional.ofNullable(dataPointVO);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static String validPointProperties(Integer id, String xid, JsonPointProperties body) {

        StringBuilder msg = new StringBuilder();

        msg.append(validId(id, xid));

        msg.append(msgIfNonNullAndInvalid("Correct intervalLoggingPeriod, it must be >= 0, value {0};", body.getIntervalLoggingPeriod(), a -> a < 0));
        msg.append(msgIfNonNullAndInvalid("Correct defaultCacheSize, it must be >= 0, value {0};", body.getDefaultCacheSize(), a -> a < 0));
        msg.append(msgIfNonNullAndInvalid("Correct purgePeriod, it must be >= 0, value {0};", body.getPurgePeriod(), a -> a < 0));
        msg.append(msgIfNonNullAndInvalid("EngineeringUnit does not exist for value {0};", body.getEngineeringUnits(),
                a -> !DataPointVO.validEngineeringUnit(a)));
        msg.append(msgIfNonNullAndInvalid("IntervalLoggingType does not exist for value {0};", body.getIntervalLoggingType(),
                a -> !DataPointVO.validIntervalLoggingType(a)));
        msg.append(msgIfNonNullAndInvalid("IntervalLoggingPeriodType does not exist for value {0};", body.getIntervalLoggingPeriodType(),
                a -> !DataPointVO.validIntervalLoggingPeriodType(a)));
        msg.append(msgIfNonNullAndInvalid("LoggingType does not exist for value {0};", body.getLoggingType(),
                a -> !DataPointVO.validLoggingType(a)));
        msg.append(msgIfNonNullAndInvalid("PurgeType does not exist for value {0};", body.getPurgeType(),
                a -> !DataPointVO.validPurgeType(a)));
        return msg.toString();
    }
}
