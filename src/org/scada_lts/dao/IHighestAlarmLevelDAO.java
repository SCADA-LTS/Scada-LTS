package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.UserAlarmLevel;

import java.util.List;

public interface IHighestAlarmLevelDAO {
    UserAlarmLevel selectAlarmLevel(User user);
    List<UserAlarmLevel> selectAlarmLevels();
}
