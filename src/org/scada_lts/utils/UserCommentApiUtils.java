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

import static org.scada_lts.utils.ValidationUtils.msgIfNonNullAndInvalid;

public final class UserCommentApiUtils {

    private static final Log LOG = LogFactory.getLog(UserCommentApiUtils.class);

    public static String validUserComment(int typeId, int refId, UserComment body) {
        StringBuilder msg = validTypeIdAndRefId(typeId, refId);
        msg.append(msgIfNonNullAndInvalid("User doesn't exist for id {0};", body.getUserId(),
                a -> !validUserId(a)));
        msg.append(msgIfNonNullAndInvalid("User doesn't exist for username {0};", body.getUsername(),
                a -> !validUserUsername(a)));
        return msg.toString();
    }

    public static String validUserCommentWithTs(int typeId, int refId, int userId, long ts) {
        StringBuilder msg = validTypeIdAndRefId(typeId, refId);
        msg.append(msgIfNonNullAndInvalid("User doesn't exist for id {0};", userId,
                a -> !validUserId(a)));
        msg.append(msgIfNonNullAndInvalid("Correct ts, it must be >= 0, value {0};", ts, a -> a < 0));
        return msg.toString();
    }

    private static StringBuilder validTypeIdAndRefId(int typeId, int refId) {
        StringBuilder msg = new StringBuilder();
        msg.append(msgIfNonNullAndInvalid("UserComment doesn't exist for type {0};", typeId,
                a -> !UserComment.validUserCommentType(a)));
        if (msg.toString().isEmpty()) {
            if (typeId == UserComment.TYPE_EVENT)
                msg.append(msgIfNonNullAndInvalid("Event doesn't exist for refId (eventId) {0};", refId,
                        a -> !validEventRefId(a)));
            else if (typeId == UserComment.TYPE_POINT)
                msg.append(msgIfNonNullAndInvalid("Datapoint doesn't exist for refId (dataPointId) {0};", refId,
                        a -> !validDataPointRefId(a)));
        }
        return msg;
    }

    private static boolean validEventRefId(int id) {
        try {
            EventService eventService = new EventService();
            EventInstance eventInstance = eventService.getEvent(id);
            return eventInstance != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validDataPointRefId(int id) {
        try {
            DataPointService dataPointService = new DataPointService();
            DataPointVO dataPointVO = dataPointService.getDataPoint(id);
            return dataPointVO != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validUserId(int id){
        try {
            UserService userService = new UserService();
            User user = userService.getUser(id);
            return user != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validUserUsername(String username){
        try {
            UserService userService = new UserService();
            User user = userService.getUser(username);
            return user != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }
}
