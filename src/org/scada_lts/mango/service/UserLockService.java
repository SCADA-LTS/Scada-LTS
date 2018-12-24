package org.scada_lts.mango.service;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
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
    EventService eventService = new EventService();

    public void lockDataPoint(int userId, long dataPointId) {
        UserLock userLock = new UserLock();
        userLock.setUserId(userId);
        userLock.setLockType(ElementType.DATA_POINT);
        userLock.setTypeKey(dataPointId);
        userLock.setTimestamp(System.currentTimeMillis());

        //todo fix construktor
        EventInstance eventInstance = new EventInstance(EventType.EventSources.DATA_POINT, System.currentTimeMillis(), false, AlarmLevels.INFORMATION, null, null);

        eventService.saveEvent(eventInstance);

        userLockDAO.insertUserLock(userLock);
    }

    public void unlockDataPoint(int userId, long dataPointId) {
        UserLock userLock = new UserLock();
        userLock.setUserId(userId);
        userLock.setLockType(ElementType.DATA_POINT);
        userLock.setTypeKey(dataPointId);
        userLock.setTimestamp(System.currentTimeMillis());

        //todo fix construktor
        EventInstance eventInstance = new EventInstance(EventType.EventSources.DATA_POINT, System.currentTimeMillis(), false, AlarmLevels.INFORMATION, null, null);

        eventService.saveEvent(eventInstance);

        userLockDAO.deleteUserLock(userLock);
    }

    public boolean checkIfDataPointIsLocked(long dataPointId) {
        boolean result;
        if (!(userLockDAO.selectUserLock(ElementType.DATA_POINT, dataPointId)==null)){
            result = true;
        } else {
         result = false;
        }
        return result;
    }

}
