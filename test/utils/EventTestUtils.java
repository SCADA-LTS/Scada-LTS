package utils;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.scada_lts.service.CommunicationChannelTypable;

import java.util.Collections;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public final class EventTestUtils {

    private EventTestUtils() { }

    public static ScheduledEvent createScheduledEventWithMock(int eventHandlerId, String eventHandlerName,
                                                              CommunicationChannelTypable channelType,
                                                              DateTime active) {
        Random random = new Random();
        EventHandlerVO eventHandler = new EventHandlerVO();
        eventHandler.setId(eventHandlerId);
        eventHandler.setAlias(eventHandlerName);
        eventHandler.setHandlerType(channelType.getEventHandlerType());

        EventType type2 = mock(DataPointEventType.class);
        int dataPointId = random.nextInt();
        when(type2.getDataPointId()).thenReturn(dataPointId);
        when(type2.getReferenceId1()).thenReturn(dataPointId);
        when(type2.getReferenceId2()).thenReturn(random.nextInt());
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTime(random.nextInt(), active, type2);
        return new ScheduledEvent(event2, eventHandler);
    }

    public static EventInstance createEventCriticalWithActiveTime(int id, DateTime activeTime, LocalizableMessage localizableMessage,
                                                                  EventType eventType) {
        EventInstance event = new EventInstance(eventType, activeTime.getMillis(), false, AlarmLevels.CRITICAL,
                localizableMessage, Collections.emptyMap());
        event.setId(id);
        return event;
    }


    public static EventInstance createEventCriticalWithActiveTime(int id, DateTime activeTime, LocalizableMessage localizableMessage) {
        EventInstance event = new EventInstance(new DataPointEventType(1,3),
                activeTime.getMillis(), false, AlarmLevels.CRITICAL,
                localizableMessage, Collections.emptyMap());
        event.setId(id);
        return event;
    }

    public static EventInstance createEventCriticalWithActiveTime(int id, DateTime activeTime, EventType eventType) {
        EventInstance event = new EventInstance(eventType, activeTime.getMillis(), false, AlarmLevels.CRITICAL,
                new LocalizableMessage("com.test"), Collections.emptyMap());
        event.setId(id);
        return event;
    }

    public static EventInstance createEventCriticalWithActiveTimeAndDataPointEventType(int id, DateTime activeTime) {
        EventInstance event = new EventInstance(new DataPointEventType(1,3),
                activeTime.getMillis(), false, AlarmLevels.CRITICAL, new LocalizableMessage("com.test"),
                Collections.emptyMap());
        event.setId(id);
        return event;
    }

    public static EventInstance createEventDataPointType(DateTime active) {
        Random random = new Random();
        EventType type2 = mock(DataPointEventType.class);
        int dataPointId = random.nextInt();
        when(type2.getDataPointId()).thenReturn(dataPointId);
        when(type2.getReferenceId1()).thenReturn(dataPointId);
        when(type2.getReferenceId2()).thenReturn(random.nextInt());
        return EventTestUtils.createEventCriticalWithActiveTime(random.nextInt(), active, type2);
    }

    public static EventHandlerVO createEventHandler(int id, int eventHandlerType) {
        EventHandlerVO emailEventHandler = new EventHandlerVO();
        emailEventHandler.setId(id);
        emailEventHandler.setHandlerType(eventHandlerType);
        return emailEventHandler;
    }
}
