package org.scada_lts.mango.service;

import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.User;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.common.ElementType;
import org.scada_lts.dao.UserLockDAO;
import org.scada_lts.dao.model.userLock.UserLock;
import org.springframework.stereotype.Service;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
@Service
public class UserLockService {

    UserLockDAO userLockDAO = new UserLockDAO();

    public void lockDataPoint(User user, long dataPointId) {
        UserLock userLock = new UserLock();
        userLock.setUserId(user.getId());
        userLock.setLockType(ElementType.DATA_POINT);
        userLock.setTypeKey(dataPointId);
        userLock.setTimestamp(System.currentTimeMillis());

        SystemEventType.raiseEvent(new SystemEventType(
                SystemEventType.TYPE_USER_LOCK_POINT), System
                .currentTimeMillis(), false, new LocalizableMessage(
                "event.dp.locked", user.getUsername(), dataPointId));

        userLockDAO.insertUserLock(userLock);
    }

    public void unlockDataPoint(User user, long dataPointId) {

        SystemEventType.raiseEvent(new SystemEventType(
                SystemEventType.TYPE_USER_LOCK_POINT), System
                .currentTimeMillis(), false, new LocalizableMessage(
                "event.dp.unlocked", user.getUsername(), dataPointId));

        userLockDAO.deleteUserLock(ElementType.DATA_POINT, dataPointId);
    }

    public boolean checkIfDataPointIsLocked(long dataPointId) {
        return !(userLockDAO.selectUserLock(ElementType.DATA_POINT, dataPointId) == null);
    }

}
