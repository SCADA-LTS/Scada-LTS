package org.scada_lts.mango.service;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.*;
import com.serotonin.web.i18n.LocalizableMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.mango.adapter.MangoEvent;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(value = PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({EventService.class})
public class SaveEventMethodFromEventServiceTest {

    @Parameterized.Parameters( name = "{index}: eventType: {0} message: {1} context: {2} activeTimestamp: {3} rtnApplicable: {4}" )
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        new DataPointEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new AuditEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new CompoundDetectorEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new DataSourceEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new MaintenanceEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new PublisherEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new ScheduledEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new SystemEventType(), new LocalizableMessage(""),
                        new HashMap<>(), System.nanoTime(), true
                },
                {
                        new DataPointEventType(), null,
                        null, 0, false
                },
                {
                        new AuditEventType(), null,
                        null, 0, false
                },
                {
                        new CompoundDetectorEventType(), null,
                        null, 0, false
                },
                {
                        new DataSourceEventType(), null,
                        null, 0, false
                },
                {
                        new MaintenanceEventType(), null,
                        null, 0, false
                },
                {
                        new PublisherEventType(), null,
                        null, 0, false
                },
                {
                        new ScheduledEventType(), null,
                        null, 0, false
                },
                {
                        new SystemEventType(), null,
                        null, 0, false
                }
        });
    }

    private final EventType eventType;
    private final LocalizableMessage message;
    private final Map<String, Object> context;
    private final long activeTimestamp;
    private final boolean rtnApplicable;

    public SaveEventMethodFromEventServiceTest(EventType eventType, LocalizableMessage message,
                                               Map<String, Object> context, long activeTimestamp,
                                               boolean rtnApplicable) {
        this.eventType = eventType;
        this.message = message;
        this.context = context;
        this.activeTimestamp = activeTimestamp;
        this.rtnApplicable = rtnApplicable;
    }

    private MangoEvent eventServiceSubject;
    private EventDAO eventDAO;
    private UserEventDAO userEventDAO;

    @Before
    public void beforeTest() throws Exception {
        eventDAO = mock(EventDAO.class);
        userEventDAO = mock(UserEventDAO.class);
        whenNew(EventDAO.class).withNoArguments().thenReturn(eventDAO);
        whenNew(UserEventDAO.class).withNoArguments().thenReturn(userEventDAO);
    }

    @Test
    public void test_saveEvent_with_alarmLevel_NONE_then_not_invoke_create_and_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventNone = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.NONE, message, context);
        eventNone.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventNone);

        //then:
        verify(eventDAO, times(0)).create(eq(eventNone));
        verify(eventDAO, times(0)).updateEvent(eq(eventNone));
    }

    @Test
    public void test_saveEvent_with_newEvent_and_alarmLevel_NONE_then_not_invoke_create_and_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventNone = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.NONE, message, context);
        eventNone.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventNone);

        //then:
        verify(eventDAO, times(0)).create(eq(eventNone));
        verify(eventDAO, times(0)).updateEvent(eq(eventNone));
    }

    @Test
    public void test_saveEvent_with_newEvent_and_alarmLevel_CRITICAL_then_once_invoke_create_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.CRITICAL, message, context);
        eventCritical.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(1)).create(eq(eventCritical));
        verify(eventDAO, times(0)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_with_event_and_alarmLevel_CRITICAL_then_once_invoke_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.CRITICAL, message, context);
        eventCritical.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(0)).create(eq(eventCritical));
        verify(eventDAO, times(1)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_twice_invoke_with_event_and_alarmLevel_CRITICAL_then_twice_invoke_create_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.CRITICAL, message, context);
        eventCritical.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventCritical);
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(2)).create(eq(eventCritical));
        verify(eventDAO, times(0)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_twice_invoke_with_event_and_alarmLevel_CRITICAL_then_twice_invoke_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.CRITICAL, message, context);
        eventCritical.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventCritical);
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(0)).create(eq(eventCritical));
        verify(eventDAO, times(2)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_with_newEvent_and_alarmLevel_LIFE_SAFETY_then_once_invoke_create_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.LIFE_SAFETY, message, context);
        eventCritical.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(1)).create(eq(eventCritical));
        verify(eventDAO, times(0)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_with_event_and_alarmLevel_LIFE_SAFETY_then_once_invoke_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.LIFE_SAFETY, message, context);
        eventCritical.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(0)).create(eq(eventCritical));
        verify(eventDAO, times(1)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_twice_invoke_with_event_and_alarmLevel_LIFE_SAFETY_then_twice_invoke_create_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.LIFE_SAFETY, message, context);
        eventCritical.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventCritical);
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(2)).create(eq(eventCritical));
        verify(eventDAO, times(0)).updateEvent(eq(eventCritical));
    }

    @Test
    public void test_saveEvent_twice_invoke_with_event_and_alarmLevel_LIFE_SAFETY_then_twice_invoke_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,activeTimestamp,
                rtnApplicable, AlarmLevels.LIFE_SAFETY, message, context);
        eventCritical.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventCritical);
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(0)).create(eq(eventCritical));
        verify(eventDAO, times(2)).updateEvent(eq(eventCritical));
    }

}