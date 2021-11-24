package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.UserAlarmLevelEvent;

import java.util.List;
import java.util.Optional;

public interface IHighestAlarmLevelDAO {
    Optional<UserAlarmLevelEvent> selectAlarmLevel(User user);
    List<UserAlarmLevelEvent> selectAlarmLevels();
}
