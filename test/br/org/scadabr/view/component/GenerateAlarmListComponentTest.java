package br.org.scadabr.view.component;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.BaseDwr;
import com.serotonin.timer.RealTimeTimer;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.SystemSettingsService;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BaseDwr.class, WebContextFactory.class, Common.class,
        AlarmListComponent.class, LogFactory.class, EventService.class})
public class GenerateAlarmListComponentTest {

    private final static String CONTENT_EXPECTED = "test";
    private AlarmListComponent subject;

    @Mock
    private WebContext webContext;

    private EventService eventService;
    private SystemSettingsService systemSettingsService;


    @Mock
    private User user;

    @Before
    public void setup() throws Exception {

        mockStatic(WebContextFactory.class);
        mockStatic(BaseDwr.class);
        mockStatic(LogFactory.class);

        RealTimeTimer realTimeTimerMock = PowerMockito.mock(RealTimeTimer.class);
        whenNew(RealTimeTimer.class).withNoArguments().thenReturn(realTimeTimerMock);
        mockStatic(Common.class);

        when(WebContextFactory.get()).thenReturn(webContext);
        when(Common.getUser()).thenReturn(user);

        eventService = PowerMockito.mock(EventService.class);
        whenNew(EventService.class).withNoArguments().thenReturn(eventService);

        systemSettingsService = PowerMockito.mock(SystemSettingsService.class);
        whenNew(SystemSettingsService.class).withNoArguments().thenReturn(systemSettingsService);


        subject = new AlarmListComponent();
    }

    @Test
    public void when_generateContent_with_eventsSubList_after_modifying_events_and_invoke_size_on_eventsSubList_then_not_throw_ConcurrentModificationException() throws Exception {

        //given:
        CopyOnWriteArrayList<EventInstance> events = new CopyOnWriteArrayList<>();
        when(eventService.getPendingEvents(anyInt())).thenReturn(events);

        HashMap<String, Object> model = new HashMap<>();
        whenNew(HashMap.class).withNoArguments().thenReturn(model);

        when(BaseDwr.generateContent(any(), any(), eq(model)))
                .thenAnswer(invocation -> {
                    Map<String, Object> modelArgs = (Map<String, Object>)invocation.getArguments()[2];
                    List<String> eventsSubList = (List<String>)modelArgs.get("events");
                    events.add(null);
                    eventsSubList.size();
                    return CONTENT_EXPECTED;
                });

        //when:
        String result = subject.generateContent();

        //then:
        assertEquals(CONTENT_EXPECTED, result);
    }

}