package org.scada_lts.utils;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.UserService;

import static org.scada_lts.utils.ValidationUtils.*;

public final class UserCommentApiUtils {

    private static final Log LOG = LogFactory.getLog(UserCommentApiUtils.class);

    private UserCommentApiUtils() {}

    public static String validUserComment(Integer typeId, Integer refId, String comment) {
        return validTypeIdAndRefId(typeId, refId);
    }

    public static String validUserCommentWithTs(Integer typeId, Integer refId, Integer userId, Long ts) {
        String msg = validTypeIdAndRefId(typeId, refId);
        msg += msgIfNullOrInvalid("Correct userId;", userId,
                a -> !validUserId(a));
        msg += msgIfNullOrInvalid("Correct ts, it must be > 0, value {0};", ts, a -> a <= 0);
        return msg;
    }

    private static String validTypeIdAndRefId(Integer typeId, Integer refId) {
        String msg = msgIfNullOrInvalid("UserComment does not exist for type {0};", typeId,
                a -> !UserComment.validUserCommentType(a));
        if(typeId == UserComment.TYPE_EVENT) {
            msg += msgIfNullOrInvalid("Event does not exist for refId (eventId) {0};", refId, a -> !validEventRefId(a));
        }
        if(typeId == UserComment.TYPE_POINT) {
            msg += msgIfNullOrInvalid("Datapoint does not exist for refId (dataPointId) {0};", refId, a -> !validDataPointRefId(a));
        }
        return msg;
    }

    private static boolean validEventRefId(Integer id) {
        try {
            EventService eventService = new EventService();
            EventInstance eventInstance = eventService.getEvent(id);
            return eventInstance != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validDataPointRefId(Integer id) {
        try {
            DataPointService dataPointService = new DataPointService();
            DataPointVO dataPointVO = dataPointService.getDataPoint(id);
            return dataPointVO != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validUserId(Integer id){
        try {
            if (id == 0)
                return false;
            UserService userService = new UserService();
            User user = userService.getUser(id);
            return user != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }
}
