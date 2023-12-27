package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.UserAlarmLevel;

import java.util.List;
import java.util.Optional;

public interface IHighestAlarmLevelDAO {
    Optional<UserAlarmLevel> selectAlarmLevel(User user);
    List<UserAlarmLevel> selectAlarmLevels();
}
