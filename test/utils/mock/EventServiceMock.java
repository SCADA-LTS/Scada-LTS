package utils.mock;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.utils.SQLPageWithTotal;
import org.scada_lts.web.mvc.api.dto.EventCommentDTO;
import org.scada_lts.web.mvc.api.dto.EventDTO;
import org.scada_lts.web.mvc.api.dto.eventHandler.EventHandlerPlcDTO;
import org.scada_lts.web.mvc.api.json.JsonEventSearch;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventServiceMock extends EventService {

    private final List<EventInstance> events = new CopyOnWriteArrayList<>();
    private boolean added;

    public EventServiceMock(List<EventInstance> events, boolean added) {
        this.events.addAll(events);
        this.added = added;
    }

    @Override
    public void saveEvent(EventInstance event) {

    }

    @Override
    public void ackEvent(int eventId, long time, int userId, int alternateAckSource, boolean signalAlarmLevelChange) {

    }

    @Override
    public void ackEvent(int eventId, long time, int userId, int alternateAckSource) {

    }

    @Override
    public void silenceEvent(int eventId, int userId) {

    }

    @Override
    public void unsilenceEvent(int eventId, int userId) {

    }

    @Override
    public void silenceEvents(List<Integer> eventIds, int userId) {

    }

    @Override
    public void unsilenceEvents(List<Integer> eventIds, int userId) {
        super.unsilenceEvents(eventIds, userId);
    }

    @Override
    public void ackAllPending(long time, int userId, int alternateAckSource) {

    }

    @Override
    public void silenceAll(int userId) {

    }

    @Override
    public void ackSelected(long time, int userId, int alternateAckSource, List<Integer> ids) {

    }

    @Override
    public void insertUserEvents(int eventId, List<Integer> userIds, boolean alarm) {

    }

    @Override
    public List<EventInstance> getActiveEvents() {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> getPendingEvents(int typeId, int typeRef1, int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> getEventsForDataPoint(int dataPointId, int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> getPendingEventsForDataPoint(int dataPointId, int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> getPendingEventsForDataSource(int dataSourceId, int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> getPendingEventsForPublisher(int publisherId, int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> getPendingEvents(int userId) {
        if(added)
            events.add(EventInstance.emptySystemNoneEvent(3));
        return new ArrayList<>(events);
    }

    @Override
    public void attachRelationalInfo(List<EventInstance> list) {

    }

    @Override
    public EventInstance insertEventComment(int eventId, UserComment comment) {
        return null;
    }

    @Override
    public int purgeEventsBefore(long time) {
        return -1;
    }

    @Override
    public int getEventCount() {
        return events.size();
    }

    @Override
    public List<EventInstance> searchOld(int eventId, int eventSourceType, String status, int alarmLevel, String[] keywords, int maxResults, int userId, ResourceBundle bundle) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel, String[] keywords, int userId, ResourceBundle bundle, int from, int to, Date date) {
        return Collections.emptyList();
    }

    @Override
    public List<EventInstance> search(int eventId, int eventSourceType, String status, int alarmLevel, String[] keywords, long dateFrom, long dateTo, int userId, ResourceBundle bundle, int from, int to, Date date) {
        return Collections.emptyList();
    }

    @Override
    public int getSearchRowCount() {
        return -1;
    }

    @Override
    public int getStartRow() {
        return -1;
    }

    @Override
    public EventType getEventHandlerType(int handlerId) {
        return null;
    }

    @Override
    public List<EventHandlerVO> getEventHandlers(EventType type) {
        return Collections.emptyList();
    }

    @Override
    public List<EventHandlerVO> getEventHandlers(EventTypeVO type) {
        return Collections.emptyList();
    }

    @Override
    public List<EventHandlerVO> getEventHandlers() {
        return Collections.emptyList();
    }

    @Override
    public List<EventHandlerPlcDTO> getPlcEventHandlers() {
        return Collections.emptyList();
    }

    @Override
    public List<EventHandlerPlcDTO> getEventHandlersByDatapointId(int datapointId) {
        return Collections.emptyList();
    }

    @Override
    public EventHandlerVO getEventHandler(int eventHandlerId) {
        return null;
    }

    @Override
    public EventHandlerVO getEventHandler(String xid) {
        return null;
    }

    @Override
    public EventHandlerVO saveEventHandler(EventType type, EventHandlerVO handler) {
        return null;
    }

    @Override
    public EventHandlerVO saveEventHandler(EventTypeVO type, EventHandlerVO handler) {
        return null;
    }

    @Override
    public void deleteEventHandler(int handlerId) {

    }

    @Override
    public void deleteEventHandler(String handlerXid) {

    }

    @Override
    public boolean toggleSilence(int eventId, int userId) {
        return super.toggleSilence(eventId, userId);
    }

    @Override
    public int getHighestUnsilencedAlarmLevel(int userId) {
        return -1;
    }

    @Override
    public void attachRelationalInfo(EventInstance event) {

    }

    @Override
    public void insertEventHandler(int typeId, int typeRef1, int typeRef2, EventHandlerVO handler) {

    }

    @Override
    public void updateEventHandler(EventHandlerVO handler) {

    }

    @Override
    public void attachRelationInfo(List<EventInstance> list) {

    }

    @Override
    public EventInstance getEvent(int eventId) {
        return null;
    }

    @Override
    public void updateEventAckUserId(int userId) {

    }

    @Override
    public void deleteUserEvent(int userId) {

    }

    @Override
    public List<EventDTO> getDataPointEventsWithLimit(int datapointId, int limit, int offset) {
        return Collections.emptyList();
    }

    @Override
    public SQLPageWithTotal<EventDTO> getEventsWithLimit(JsonEventSearch query, User user) {
        return null;
    }

    @Override
    public List<EventCommentDTO> findCommentsByEventId(int eventId) {
        return Collections.emptyList();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return null;
    }

    @Override
    protected void finalize() throws Throwable {

    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public void addEvent(EventInstance eventInstance) {
        this.events.add(eventInstance);
    }
}
