package br.org.scadabr.view.component;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.BaseDwr;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BaseDwr.class, WebContextFactory.class, Common.class,
        AlarmListComponent.class})
public class AlarmListComponentMockitoTest {

    private final static String CONTENT_EXPECTED = "test";
    private AlarmListComponent subject;

    @Mock
    private WebContext webContext;

    @Mock
    private EventDao eventDao;

    @Mock
    private User user;

    @Before
    public void setup() throws Exception {

        subject = new AlarmListComponent();

        mockStatic(WebContextFactory.class);
        mockStatic(BaseDwr.class);
        mockStatic(Common.class);

        when(WebContextFactory.get()).thenReturn(webContext);
        when(Common.getUser()).thenReturn(user);

        whenNew(EventDao.class).withNoArguments().thenReturn(eventDao);

        ArrayList<EventInstance> eventsOriginal = new ArrayList<>();
        when(eventDao.getPendingEvents(anyInt())).thenReturn(eventsOriginal);

        HashMap<String, Object> model = new HashMap<>();
        whenNew(HashMap.class).withNoArguments().thenReturn(model);

        when(BaseDwr.generateContent(any(), any(), eq(model)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArguments();
                    Map<String, Object> model1 = (Map<String, Object>)args[2];
                    List<String> events = (List<String>)model1.get("events");
                    eventsOriginal.add(null);
                    events.size();
                    return CONTENT_EXPECTED;
                });
    }

    @Test
    public void test_generateContent() {
        //when:
        String result = subject.generateContent();

        //then:
        assertEquals(CONTENT_EXPECTED, result);
    }

}