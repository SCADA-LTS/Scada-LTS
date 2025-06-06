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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public interface IEventDAO extends GenericDaoCR<EventInstance> {
    List<EventDTO> findEventsWithLimit(int typeId, int typeRef, int limit, int offset);

    SQLPageWithTotal<EventDTO> findEvents(
            JsonEventSearch query,
            User user);

    @Override
    List<EventInstance> findAll();

    @Override
    EventInstance findById(Object[] pk);

    @Override
    List<EventInstance> filtered(String filter, Object[] argsFilter, long limit);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    Object[] create(EventInstance entity);

    List<EventHandlerPlcDTO> getEventHandlersByDatapointId(int datapointId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void updateEvent(EventInstance event);

    void updateAck(long actTS, long userId, int alternateAckSource, long eventId);

    void ackEvents(long actTS, long userId, int alternateAckSource);

    void silenceEvents(long userId);

    void unassignEvents();

    void ackAllPendingSelected(long actTS, long userId, int alternateAckSource, List<Integer> ids);

    @Deprecated
    List<EventInstance> getEventsForDataPoint(int dataPointId, int userId);

    @Deprecated
    List<EventInstance> getPendingEvents(int typeId, int typeRef1, int userId);

    @Deprecated
    List<EventInstance> getPendingEvents(int typeId, int userId);

    List<EventInstance> getPendingEventsLimit(int userId, int limit);

    List<EventInstance> getPendingEventsLimitAlarmLevelMin(int userId, int alarmLevelMin, int limit);

    List<EventInstance> getEventsForDataPointLimit(int dataPointId, int userId, int limit);

    List<EventInstance> getPendingEventsLimit(int typeId, int typeRef1, int userId, int limit);

    List<EventInstance> getPendingEventsLimit(int typeId, int userId, int limit);

    @Deprecated
    void attachRelationalInfo(EventInstance event);

    @Deprecated
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int purgeEventsBefore(long time);

    int getEventCount();

    //TODO rewrite
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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void updateEventHandler(EventHandlerVO handler);

    //TODO rewrite because insert does not requires select
    EventHandlerVO saveEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    boolean toggleSilence(int eventId, int userId, Boolean updated);

    int getHighestUnsilencedAlarmLevel(int userId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void updateEventAckUserId(int userId);

    @Transactional(readOnly = true)
    List<EventInstance> getAllStatusEvents(Set<Integer> ids);

    @Transactional(readOnly = true)
    List<EventHandlerVO> getEventHandlers(Set<Integer> ids);

    @Transactional(readOnly = true)
    List<EventCommentDTO> findCommentsByEventId(int eventId);

    String joinAnd(List<String> conditions);

    String joinOr(List<String> conditions);

    boolean isSilence(int eventId, int userId);

    boolean assign(long eventId, long acceptTs, User user);

    boolean unassign(long eventId);
}
