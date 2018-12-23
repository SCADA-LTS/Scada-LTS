package org.scada_lts.mango.service;

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

    private final static short DATA_POINT_TYPE = 2;

    public void lockDataPoint(int userId, long dataPointId) {
        UserLock userLock = new UserLock();
        userLock.setUserId(userId);
        userLock.setLockType(DATA_POINT_TYPE);
        userLock.setTypeKey(dataPointId);
        userLock.setTimestamp(System.currentTimeMillis());

        userLockDAO.insertUserLock(userLock);
    }

    public boolean checkIfDataPointIsLocked(long dataPointId) {
        boolean result;
        if (!(userLockDAO.selectUserLock(DATA_POINT_TYPE, dataPointId)==null)){
            result = true;
        } else {
         result = false;
        }
        return result;
    }

}
