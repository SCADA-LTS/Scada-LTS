package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.dataSource.meta.MetaDataSourceRT;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.utils.EventTypeUtil;
import utils.EventTestUtils;
import utils.TestConcurrentUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class ActiveEventsMultiThreadTest {

    private static EventType eventType1 ;
    private static EventType eventType2 ;
    private static EventType eventType3 ;

    static {
        try {
            eventType1 = EventTypeUtil.createEventType(EventType.EventSources.DATA_SOURCE, 11, MetaDataSourceRT.EVENT_TYPE_CONTEXT_POINT_DISABLED, 1);
            eventType2 = EventTypeUtil.createEventType(EventType.EventSources.DATA_SOURCE, 21, MetaDataSourceRT.EVENT_TYPE_CONTEXT_POINT_MISSING, 2);
            eventType3 = EventTypeUtil.createEventType(EventType.EventSources.DATA_SOURCE, 31, MetaDataSourceRT.EVENT_TYPE_CONTEXT_POINT_UNAVAILABLE, 3);
        } catch (Exception e) {
            eventType1 = null;
            eventType2 = null;
            eventType3 = null;
        }
    }

    private static final EventInstance eventInstance1EventType1 = EventTestUtils.createEventCriticalWithActiveTime(1, DateTime.now(),
            new LocalizableMessage("key1"),
            eventType1,
            true);

    private static final EventInstance eventInstance2EventType1 = EventTestUtils.createEventCriticalWithActiveTime(2, DateTime.now(),
            new LocalizableMessage("key2"),
            eventType1,
            true);

    private static final EventInstance eventInstance3EventType1 = EventTestUtils.createEventCriticalWithActiveTime(3, DateTime.now(),
            new LocalizableMessage("key3"),
            eventType1,
            true);

    private static final EventInstance eventInstance4EventType2 = EventTestUtils.createEventCriticalWithActiveTime(4, DateTime.now(),
            new LocalizableMessage("key2"),
            eventType2,
            true);

    private static final EventInstance eventInstance5EventType3 = EventTestUtils.createEventCriticalWithActiveTime(5, DateTime.now(),
            new LocalizableMessage("key3"),
            eventType3,
            true);

    @Parameterized.Parameters(name= "{index}: events: {0}, expectedEvents: {1}, keys: {2}")
    public static Collection primeNumbers() throws SQLException {
        return Arrays.asList(new Object[][] {
                {
                    Arrays.asList(eventInstance1EventType1, eventInstance2EventType1, eventInstance3EventType1),
                    Arrays.asList(eventInstance1EventType1, eventInstance2EventType1, eventInstance3EventType1),
                    eventType1
                },
                {
                    Arrays.asList(eventInstance3EventType1, eventInstance4EventType2, eventInstance5EventType3),
                    Arrays.asList(eventInstance4EventType2),
                    eventType2
                },


        });
    }

    private final List<EventInstance> events;
    private final List<EventInstance> expectedEvents;
    private final EventType key;

    public ActiveEventsMultiThreadTest(List<EventInstance> events, List<EventInstance> expectedEvents, EventType key) {
        this.events = events;
        this.expectedEvents = expectedEvents;
        this.key = key;
    }

    private ActiveEvents activeEvents;

    @Before
    public void config() {
        MangoEvent mangoEvent = mock(MangoEvent.class);
        activeEvents = ActiveEvents.newSync(mangoEvent);
    }

    @Test
    public void when_initActiveEvents_then_same_list_events() {

        //when:
        TestConcurrentUtils.consumer(1, activeEvents::initActiveEvents, events);

        //then:
        List<EventInstance> result = activeEvents.removeActiveEvents(key, null);
        Assert.assertEquals(expectedEvents, result);
    }

    @Test
    public void when_initActiveEvents_then_size() {

        //when:
        TestConcurrentUtils.consumer(1, activeEvents::initActiveEvents, events);

        //then:
        List<EventInstance> result = activeEvents.removeActiveEvents(key, null);
        Assert.assertEquals(expectedEvents.size(), result.size());
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_rtnApplicable_true_and_ignore_same_message_and_add_same_event_twice_then_ignore_true() {
        //when:
        activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance5EventType3, false);
        List<Boolean> ignored = TestConcurrentUtils.functionWithResultCheck(4, activeEvents::isIgnoreIfNotThenAddActiveEvent, eventInstance5EventType3, false);

        //then:
        for(Boolean ignore: ignored) {
            Assert.assertEquals(true, ignore);
        }
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_rtnApplicable_true_and_ignore_same_message_and_add_event_once_then_ignore_false() {

        //when:
        List<Boolean> ignored = TestConcurrentUtils.functionWithResultCheck(4, activeEvents::isIgnoreIfNotThenAddActiveEvent, eventInstance5EventType3, false);
        List<Boolean> result = ignored.stream().filter(a -> !a).collect(Collectors.toList());

        //then:
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_rtnApplicable_true_and_ignore_same_message_and_add_event_once_then_this_event() {
        List<EventInstance> expected = Arrays.asList(eventInstance5EventType3);

        //when:
        TestConcurrentUtils.functionWithResultCheck(4, activeEvents::isIgnoreIfNotThenAddActiveEvent, eventInstance5EventType3, false);

        //then:
        List<EventInstance> result = activeEvents.removeActiveEvents(eventType3, null);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_and_removeActiveEvents_in_multi_threads_then_same_list_events() throws Throwable {

        //given:
        List<EventInstance> expected = Arrays.asList(eventInstance1EventType1, eventInstance2EventType1, eventInstance3EventType1);

        List<Function<EventType, ?>> tasks = new ArrayList<>();
        //activeEvents.initActiveEvents(Arrays.asList(eventInstance1EventType1, eventInstance2EventType1, eventInstance3EventType1));
        tasks.add(eventType -> activeEvents.removeActiveEvents(eventType, null));
        tasks.add(eventType -> activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance1EventType1, false));
        tasks.add(eventType -> activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance2EventType1, false));
        tasks.add(eventType -> activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance3EventType1, false));
        tasks.add(eventType -> activeEvents.removeActiveEvents(eventType, null));
        tasks.add(eventType -> activeEvents.removeActiveEvents(eventType, null));

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResultObjectCheck(1, tasks, eventType1);
        List<EventInstance> removeEnd = activeEvents.removeActiveEvents(eventType1, null);

        if(removeEnd != null)
            result.add(removeEnd);
        result = result.stream().filter(object -> object instanceof List)
                .flatMap(list -> ((List<?>) list).stream())
                .map(event -> (EventInstance)event)
                .sorted(Comparator.comparing(EventInstance::getId))
                .collect(Collectors.toList());
        //then:
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_and_removeActiveEvents_in_multi_threads_then_same_list_size() throws Throwable {

        //given:
        List<EventInstance> expected = Arrays.asList(eventInstance1EventType1, eventInstance2EventType1, eventInstance3EventType1);

        List<Function<EventType, ?>> tasks = new ArrayList<>();
        tasks.add(eventType -> activeEvents.removeActiveEvents(eventType, null));
        tasks.add(eventType -> activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance1EventType1, false));
        tasks.add(eventType -> activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance2EventType1, false));
        tasks.add(eventType -> activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance3EventType1, false));
        tasks.add(eventType -> activeEvents.removeActiveEvents(eventType, null));
        tasks.add(eventType -> activeEvents.removeActiveEvents(eventType, null));

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResultObjectCheck(1, tasks, eventType1);
        List<EventInstance> removeEnd = activeEvents.removeActiveEvents(eventType1, null);

        if(removeEnd != null)
            result.add(removeEnd);
        result = result.stream().filter(object -> object instanceof List)
                .flatMap(list -> ((List<?>) list).stream())
                .collect(Collectors.toList());
        //then:
        Assert.assertEquals(expected.size(), result.size());
    }
}