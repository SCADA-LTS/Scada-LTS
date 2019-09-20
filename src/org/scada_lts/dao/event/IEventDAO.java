package org.scada_lts.dao.event;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.scada_lts.dao.GenericDaoCR;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public interface IEventDAO extends GenericDaoCR<EventInstance> {
    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void updateEvent(EventInstance event);

    void updateAck(long actTS, long userId, int alternateAckSource, long eventId);

    List<EventInstance> getEventsForDataPoint(int dataPointId, int userId);

    List<EventInstance> getPendingEvents(int typeId, int typeRef1, int userId);

    List<EventInstance> getPendingEvents(int typeId, int userId);

    List<EventInstance> getPendingEventsLimit(int userId, int limit);

    void attachRelationalInfo(EventInstance event);

    @Deprecated
    @Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
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

    EventHandlerVO getEventHandler(int eventHandlerId);

    EventHandlerVO getEventHandler(String xid);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    int insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void updateEventHandler(EventHandlerVO handler);

    //TODO rewrite because insert does not requires select
    EventHandlerVO saveEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler);

    @Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void delete(int id);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    boolean toggleSilence(int eventId, int userId, Boolean updated);

    int getHighestUnsilencedAlarmLevel(int userId);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void updateEventAckUserId(int userId);
}
