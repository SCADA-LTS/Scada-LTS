package com.serotonin.mango.web.dwr;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.longPoll.LongPollRequest;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.web.content.SnippetContentGenerator;
import org.springframework.mock.web.MockHttpSession;
import utils.TestConcurrentUtils;
import utils.mock.EventServiceMock;
import utils.mock.MockUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WebContextFactory.class, Common.class, MiscDwr.class, SystemSettingsDAO.class,
        SnippetContentGenerator.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class MiscDwrDoLongPollAlarmsMultiThreadTest {

    private final int sessionId = 123351;
    private MiscDwr subject;
    private EventServiceMock eventServiceMock;
    private LongPollRequest longPollRequest;
    private String statesKey;

    @Before
    public void config() throws Exception {

        this.statesKey = "pendingAlarmsContent";

        String userName = "user mock";
        User user = new User();
        user.setId(534);
        user.setUsername(userName);

        mockStatic(SnippetContentGenerator.class);
        when(SnippetContentGenerator.generateContent(any(HttpServletRequest.class), anyString(), anyMap())).thenAnswer(a -> {
            Map<String, Object> params = (Map<String, Object>) a.getArguments()[2];
            List<EventInstance> eventInstance = (List<EventInstance>)params.get("events");
            return eventInstance.stream()
                    .map(event -> String.valueOf(event.getId()))
                    .collect(Collectors.joining(","));
        });

        RuntimeManager runtimeManagerMock = mock(RuntimeManager.class);
        MockUtils.configMock(runtimeManagerMock, user);

        //WebContext mock
        HttpSession httpSession = new MockHttpSession();
        mockStatic(WebContextFactory.class);
        WebContext webContextMock = mock(WebContext.class);
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(webContextMock.getHttpServletRequest()).thenReturn(httpServletRequestMock);

        when(webContextMock.getSession()).thenReturn(httpSession);
        when(WebContextFactory.get()).thenReturn(webContextMock);

        mockStatic(SystemSettingsDAO.class);

        eventServiceMock = new EventServiceMock(Arrays.asList(
                EventInstance.emptySystemNoneEvent(1)
        ), true);

        whenNew(EventService.class)
                .withAnyArguments()
                .thenReturn(eventServiceMock);

        longPollRequest = new LongPollRequest();
        longPollRequest.setPendingAlarms(true);

        BaseDwr.initialize();

        subject = new MiscDwr();
    }

    @Test
    public void when_doLongPoll_with_five_other_pointValue_then_five_newStates() {

        //given:
        subject.initializeLongPoll(sessionId, longPollRequest);

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResult(5,
                subject::doLongPoll, sessionId,
                response -> response.get(statesKey));

        //then:
        assertEquals(5, result.size());
    }

    @Test
    public void when_doLongPoll_with_same_pointValue_then_one_newState() {

        //given:
        subject.initializeLongPoll(sessionId, longPollRequest);
        eventServiceMock.addEvent(EventInstance.emptySystemNoneEvent(256));
        eventServiceMock.setAdded(false);

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResult(5,
                subject::doLongPoll, sessionId,
                response -> response.get(statesKey));

        //then:
        assertEquals(1, result.size());
    }
}