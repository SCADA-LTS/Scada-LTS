package org.scada_lts.dao.event;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.utils.SQLPageWithTotal;
import org.scada_lts.web.mvc.api.dto.EventCommentDTO;
import org.scada_lts.web.mvc.api.dto.EventDTO;
import org.scada_lts.web.mvc.api.dto.eventHandler.EventHandlerPlcDTO;
import org.scada_lts.web.mvc.api.json.JsonEventSearch;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public interface IEventDAO extends GenericDaoCR<EventInstance> {
    List<EventDTO> findEventsWithLimit(int typeId, int typeRef, int limit, int offset);

    SQLPageWithTotal<EventDTO> findEvents(
            JsonEventSearch query,
            User user);

    List<EventHandlerPlcDTO> getEventHandlersByDatapointId(int datapointId);

    void updateEvent(EventInstance event);

    void updateAck(long actTS, long userId, int alternateAckSource, long eventId);

    void ackEvents(long actTS, long userId, int alternateAckSource);

    void silenceEvents(long userId);

    void unassignEvents();

    void ackAllPendingSelected(long actTS, long userId, int alternateAckSource, List<Integer> ids);

    List<EventInstance> getPendingEventsLimit(int userId, int limit);

    List<EventInstance> getPendingEventsLimitAlarmLevelMin(int userId, int alarmLevelMin, int limit);

    List<EventInstance> getEventsForDataPointLimit(int dataPointId, int userId, int limit);

    List<EventInstance> getPendingEventsLimit(int typeId, int typeRef1, int userId, int limit);

    List<EventInstance> getPendingEventsLimit(int typeId, int userId, int limit);

    @Deprecated
    int purgeEventsBefore(long time);

    int getEventCount();

    List<EventInstance> searchOld(int eventId, int eventSourceType, String status, int alarmLevel, String[] keywords,
                                  int maxResults, int userId, ResourceBundle bundle);

    int getSearchRowCount();

    int getStartRow();

    List<EventInstance> search(int eventId, int eventSourceType,
                               String status, int alarmLevel, String[] keywords,
                               long dateFrom, long dateTo, int userId,
                               ResourceBundle bundle, int from, int to,
                               Date date);

    EventType getEventHandlerType(int handlerId);

    List<EventHandlerVO> getEventHandlers(int typeId, int ref1, int ref2);

    List<EventHandlerVO> getEventHandlers();

    List<EventHandlerPlcDTO> getPlcEventHandlers();

    EventHandlerVO getEventHandler(int eventHandlerId);

    EventHandlerVO getEventHandler(String xid);

    int insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);

    void updateEventHandler(EventHandlerVO handler);

    EventHandlerVO saveEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);

    void delete(int id);

    boolean toggleSilence(int eventId, int userId, Boolean updated);

    int getHighestUnsilencedAlarmLevel(int userId);

    void updateEventAckUserId(int userId);

    List<EventInstance> getAllStatusEvents(Set<Integer> ids);

    List<EventHandlerVO> getEventHandlers(Set<Integer> ids);

    List<EventCommentDTO> findCommentsByEventId(int eventId);

    String joinAnd(List<String> conditions);

    String joinOr(List<String> conditions);

    boolean isSilence(int eventId, int userId);

    boolean assign(long eventId, long acceptTs, User user);

    boolean unassign(long eventId);
}
