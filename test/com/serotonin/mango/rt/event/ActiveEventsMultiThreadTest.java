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
import org.scada_lts.utils.EventTypeUtil;
import utils.EventTestUtils;
import utils.TestConcurrentUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        return Arrays.asList(new Object[][]{
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
        activeEvents = ActiveEvents.newSync();
    }

    @Test
    public void when_initActiveEvents_then_same_list_events() throws SQLException {

        //when:
        TestConcurrentUtils.consumer(1, activeEvents::initActiveEvents, events);

        //then:
        List<EventInstance> result = activeEvents.removeActiveEvents(key, null);
        Assert.assertEquals(result, expectedEvents);
    }

    @Test
    public void when_initActiveEvents_then_size() throws SQLException {

        //when:
        TestConcurrentUtils.consumer(1, activeEvents::initActiveEvents, events);

        //then:
        List<EventInstance> result = activeEvents.removeActiveEvents(key, null);
        Assert.assertEquals(result.size(), expectedEvents.size());
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_rtnApplicable_true_and_ignore_same_message_and_add_same_event_twice_then_ignore_true() throws SQLException {
        //when:
        activeEvents.isIgnoreIfNotThenAddActiveEvent(eventInstance5EventType3);
        List<Boolean> ignored = TestConcurrentUtils.functionWithResult(4, activeEvents::isIgnoreIfNotThenAddActiveEvent, eventInstance5EventType3);

        //then:
        for(Boolean ignore: ignored) {
            Assert.assertEquals(true, ignore);
        }
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_rtnApplicable_true_and_ignore_same_message_and_add_event_once_then_ignore_false() throws SQLException {

        //when:
        List<Boolean> ignored = TestConcurrentUtils.functionWithResult(4, activeEvents::isIgnoreIfNotThenAddActiveEvent, eventInstance5EventType3);
        List<Boolean> result = ignored.stream().filter(a -> !a).collect(Collectors.toList());

        //then:
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void when_isIgnoreIfNotThenAddActiveEvent_rtnApplicable_true_and_ignore_same_message_and_add_event_once_then_this_event() throws SQLException {
        List<EventInstance> expected = Arrays.asList(eventInstance5EventType3);

        //when:
        TestConcurrentUtils.functionWithResult(4, activeEvents::isIgnoreIfNotThenAddActiveEvent, eventInstance5EventType3);

        //then:
        List<EventInstance> result = activeEvents.removeActiveEvents(eventType3, null);
        Assert.assertEquals(expected, result);
    }

}