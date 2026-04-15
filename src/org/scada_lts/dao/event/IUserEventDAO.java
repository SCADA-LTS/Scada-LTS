package org.scada_lts.dao.event;

import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.event.UserEvent;

import java.util.List;

public interface IUserEventDAO extends GenericDaoCR<UserEvent> {

    void batchUpdate(int userId, List<Integer> eventIds, boolean silence);

    void updateAck(long eventId, boolean alarmWentOff);

    void silenceEvent(long eventId, int userId);

    void unsilenceEvent(long eventId, int userId);

    void silenceEvents(List<Integer> eventIds, int userId);

    void unsilenceEvents(List<Integer> eventIds, int userId);

    void delete(int eventId);
}

