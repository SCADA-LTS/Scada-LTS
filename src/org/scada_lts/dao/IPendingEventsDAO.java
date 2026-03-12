package org.scada_lts.dao;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.AlarmLevelType;
import com.serotonin.mango.vo.UserComment;

import java.util.List;
import java.util.Map;

public interface IPendingEventsDAO {

    List<EventInstance> getPendingEvents(int userId, Map<Integer, List<UserComment>> commentsMap,
                                         AlarmLevelType alarmLevel);

    List<EventInstance> getPendingEvents(int userId, Map<Integer, List<UserComment>> commentsMap,
                                         AlarmLevelType alarmLevel, int offset, int limit);
}
