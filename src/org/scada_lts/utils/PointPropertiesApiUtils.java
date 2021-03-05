package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.json.JsonPointProperties;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Predicate;

public class PointPropertiesApiUtils {

    private static final Log LOG = LogFactory.getLog(PointPropertiesApiUtils.class);

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

        JsonPointProperties.defaultValues(body);

        msg.append(msgIfError("Correct intervalLoggingPeriod, it must be >= 0, value {0}", body.getIntervalLoggingPeriod(), a -> a < 0));
        msg.append(msgIfError("Correct defaultCacheSize, it must be >= 0, value {0}", body.getDefaultCacheSize(), a -> a < 0));
        msg.append(msgIfError("Correct purgePeriod, it must be >= 0, value {0}", body.getPurgePeriod(), a -> a < 0));
        msg.append(msgIfError("EngineeringUnit does no exist for value {0}", body.getEngineeringUnits(),
                a -> !DataPointVO.validEngineeringUnit(a)));
        msg.append(msgIfError("IntervalLoggingType does no exist for value {0}", body.getIntervalLoggingType(),
                a -> !DataPointVO.validIntervalLoggingType(a)));
        msg.append(msgIfError("IntervalLoggingPeriodType does no exist for value {0}", body.getIntervalLoggingPeriodType(),
                a -> !DataPointVO.validIntervalLoggingPeriodType(a)));
        msg.append(msgIfError("LoggingType does no exist for value {0}", body.getLoggingType(),
                a -> !DataPointVO.validLoggingType(a)));
        msg.append(msgIfError("PurgeType does no exist for value {0}", body.getPurgeType(),
                a -> !DataPointVO.validPurgeType(a)));
        return msg.toString();
    }

    public static String validId(Integer id, String xid) {
        if (id == null && StringUtils.isEmpty(xid)) {
            return "Correct id and xid;";
        }
        return "";
    }

    private static <T> String msgIfError(String msg, T value, Predicate<T> errorIf) {
        if(errorIf.test(value)) {
            return MessageFormat.format(msg, String.valueOf(value));
        }
        return "";
    }
}
