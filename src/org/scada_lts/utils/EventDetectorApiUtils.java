package org.scada_lts.utils;

import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.dto.EventDetectorDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.scada_lts.utils.UpdateValueUtils.setIf;
import static org.scada_lts.utils.ValidationUtils.*;

public final class EventDetectorApiUtils {

    private static final Log LOG = LogFactory.getLog(EventDetectorApiUtils.class);

    private EventDetectorApiUtils() {}

    public static Optional<PointEventDetectorVO> getEventDetector(DataPointVO dataPointVO, int eventDetectorId) {
        List<PointEventDetectorVO> peds = dataPointVO.getEventDetectors();
        if (!peds.isEmpty()) {
            for (PointEventDetectorVO ped : peds) {
                if (ped != null && ped.getId() == eventDetectorId) {
                    return Optional.of(ped);
                }
            }
        }
        return Optional.empty();
    }

    public static void updateValueEventDetector(PointEventDetectorVO toUpdate, EventDetectorDTO source) {
        setIf(source.getXid(), toUpdate::setXid, a -> !StringUtils.isEmpty(a));
        setIf(source.getAlias(), toUpdate::setAlias, Objects::nonNull);
        setIf(source.getAlarmLevel(), toUpdate::setAlarmLevel, Objects::nonNull);
        setIf(source.getDetectorType(), toUpdate::setDetectorType, Objects::nonNull);
        setIf(source.getLimit(), toUpdate::setLimit, Objects::nonNull);
        setIf(source.getDuration(), toUpdate::setDuration, Objects::nonNull);
        setIf(source.getDurationType(), toUpdate::setDurationType, Objects::nonNull);
        setIf(source.getBinaryState(), toUpdate::setBinaryState, Objects::nonNull);
        setIf(source.getMultistateState(), toUpdate::setMultistateState, Objects::nonNull);
        setIf(source.getChangeCount(), toUpdate::setChangeCount, Objects::nonNull);
        setIf(source.getAlphanumericState(), toUpdate::setAlphanumericState, Objects::nonNull);
        setIf(source.getWeight(), toUpdate::setWeight, Objects::nonNull);
    }

    public static String validEventDetectorBodyUpdate(Integer dataPointId, Integer eventDetectorId,
                                                      EventDetectorDTO body) {

        StringBuilder msg = new StringBuilder();
        msg.append(validId(eventDetectorId));

        msg.append(msgIfNull( "Correct dataPointId", dataPointId));
        msg.append(msgIfNonNullAndInvalid("AlarmLevel does not exist for value {0};", body.getAlarmLevel(),
                a -> !PointEventDetectorVO.validAlarmLevel(a)));
        msg.append(msgIfNonNullAndInvalid("DetectorType does not exist for value {0};", body.getDetectorType(),
                a -> !PointEventDetectorVO.validDetectorType(a)));
        msg.append(msgIfNonNullAndInvalid("DurationType does not exist for value {0};", body.getDurationType(),
                a -> !PointEventDetectorVO.validDurationType(a)));
        msg.append(msgIfNonNullAndInvalid("Correct duration, it must be >= 0, value {0};", body.getDuration(), a -> a < 0));
        msg.append(msgIfNonNullAndInvalid("Correct changeCount, it must be >= 2, value {0};", body.getChangeCount(), a -> a < 2));
        msg.append(validChangeCountAndDurationValue(body));
        return msg.toString();
    }

    public static Optional<DataPointVO> getDataPointById(int id, DataPointService dataPointService) {
        try {
            DataPointVO dataPointVO = dataPointService.getDataPoint(id);
            return Optional.ofNullable(dataPointVO);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static String validEventDetector(PointEventDetectorVO eventDetector, DataPointVO dataPoint,
                                            EventDetectorDTO body) {
        return validXid(eventDetector.getXid(), body.getXid()) +
                validEventDetectorType(dataPoint, body);
    }

    public static String validEventDetectorBodyCreate(Integer dataPointId, EventDetectorDTO body) {

        StringBuilder msg = new StringBuilder();

        msg.append(msgIfNull( "Correct dataPointId", dataPointId));
        msg.append(msgIfNullOrInvalid("AlarmLevel does not exist for value {0};", body.getAlarmLevel(),
                a -> !PointEventDetectorVO.validAlarmLevel(a)));
        msg.append(msgIfNullOrInvalid("DetectorType does not exist for value {0};", body.getDetectorType(),
                a -> !PointEventDetectorVO.validDetectorType(a)));
        msg.append(msgIfNullOrInvalid("DurationType does not exist for value {0};", body.getDurationType(),
                a -> !PointEventDetectorVO.validDurationType(a)));
        msg.append(msgIfNullOrInvalid("Correct duration, it must be >= 0, value {0};", body.getDuration(), a -> a < 0));
        msg.append(msgIfNullOrInvalid("Correct changeCount, it must be >= 2, value {0};", body.getChangeCount(), a -> a < 2));
        msg.append(validChangeCountAndDurationValue(body));
        return msg.toString();
    }

    public static String validChangeCountAndDurationValue(EventDetectorDTO body) {
        return msgIfNonNullAndInvalid("ChangeCount value must be >= 2, for detectorType: {0};", body.getDetectorType(), a -> invalidChangeCountValue(body)) +
                msgIfNonNullAndInvalid("Duration value must be >= 1, for detectorType: {0};", body.getDetectorType(), a -> invalidDurationValue(body));
    }

    private static String validEventDetectorType(DataPointVO dataPoint, EventDetectorDTO body) {
        PointEventDetectorVO eventDetector = body.createPointEventDetectorVO(dataPoint);
        int dataTypeId = dataPoint.getPointLocator().getDataTypeId();
        if(eventDetector.getDef() == null) {
            return "DetectorType is required;";
        }
        if(!eventDetector.getDef().supports(dataTypeId)) {
            return "DetectorType not supported for dataPointType: " + dataTypeId + ". Supported: " + toString(PointEventDetectorVO.getImplementations(dataTypeId)) + " ;";
        }
        return "";
    }

    private static String toString(List<ImplDefinition> definitions) {
        return definitions.stream()
                .map(ImplDefinition::getId)
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static boolean isNoChangeOrNoUpdateType(int detectorType) {
        return detectorType == PointEventDetectorVO.TYPE_NO_CHANGE
                || detectorType == PointEventDetectorVO.TYPE_NO_UPDATE;
    }

    private static boolean isStateChangeCountType(int detectorType) {
        return detectorType == PointEventDetectorVO.TYPE_STATE_CHANGE_COUNT;
    }

    private static boolean invalidChangeCountValue(EventDetectorDTO body) {
        Integer detectorType = body.getDetectorType();
        return detectorType != null
                && isStateChangeCountType(detectorType)
                && (body.getChangeCount() == null || body.getChangeCount() < 2);
    }

    private static boolean invalidDurationValue(EventDetectorDTO body) {
        Integer detectorType = body.getDetectorType();
        return detectorType != null
                && (isNoChangeOrNoUpdateType(detectorType) || isStateChangeCountType(detectorType))
                && (body.getDuration() == null || body.getDuration() < 1);
    }
}
