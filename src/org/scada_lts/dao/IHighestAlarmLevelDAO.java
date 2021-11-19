package org.scada_lts.dao;

import com.serotonin.mango.vo.User;

public interface IHighestAlarmLevelDAO {
    int selectAlarmLevel(User user);
}
