package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.dto.EventDetectorDTO;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.scada_lts.utils.PointPropertiesApiUtils.msgIfValueNotNullAndInvalid;

public class EventDetectorApiUtils {

    public static PointEventDetectorVO getEventDetector(DataPointVO dataPointVO, int pedId) {
        List<PointEventDetectorVO> peds = dataPointVO.getEventDetectors();
        if (!peds.isEmpty()) {
            for (PointEventDetectorVO ped : peds) {
                if (ped.getId() == pedId) {
                    return ped;
                }
            }
        }
        return null;
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

    public static String validEventDetector(Integer datapointId, Integer id, EventDetectorDTO body) {

        StringBuilder msg = new StringBuilder();

        msg.append(validDatapointId(datapointId));
        msg.append(validId(id));

        msg.append(msgIfValueNotNullAndInvalid("AlarmLevel does no exist for value {0}", body.getAlarmLevel(),
                a -> !PointEventDetectorVO.validAlarmLevel(a)));
        msg.append(msgIfValueNotNullAndInvalid("DetectorType does no exist for value {0}", body.getDetectorType(),
                a -> !PointEventDetectorVO.validDetectorType(a)));
        msg.append(msgIfValueNotNullAndInvalid("DurationType does no exist for value {0}", body.getDurationType(),
                a -> !PointEventDetectorVO.validDurationType(a)));
        msg.append(msgIfValueNotNullAndInvalid("Correct duration, it must be >= 0, value {0}", body.getDuration(), a -> a < 0));
        msg.append(msgIfValueNotNullAndInvalid("Correct changeCount, it must be >= 0, value {0}", body.getChangeCount(), a -> a < 0));
        return msg.toString();
    }

    public static String validId(Integer id) {
        if (id == null) {
            return "Correct id;";
        }
        return "";
    }

    public static String validDatapointId(Integer datapointId) {
        if (datapointId == null) {
            return "Correct datapointId;";
        }
        return "";
    }

    private static <T> void setIf(T value, Consumer<T> setter, Predicate<T> setIf) {
        if(setIf.test(value))
            setter.accept(value);
    }
}
