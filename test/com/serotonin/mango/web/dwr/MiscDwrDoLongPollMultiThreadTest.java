package com.serotonin.mango.web.dwr;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.*;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.custom.CustomView;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.web.dwr.longPoll.LongPollRequest;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.web.contnet.SnippetContentGenerator;
import org.springframework.mock.web.MockHttpSession;
import utils.*;
import utils.mock.ChangePointValueDataPointRtMock;
import utils.mock.MockUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({WebContextFactory.class, Common.class, MiscDwr.class, SystemSettingsDAO.class,
        SnippetContentGenerator.class, DataPointRT.class, DataPointDetailsDwr.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class MiscDwrDoLongPollMultiThreadTest {

    @Parameterized.Parameters(name = "{index}: statesKey: {1}, testing: {7}")
    public static Object[][] data() {
        int dataPointId1 = 123;
        int dataPointId2 = 124;

        String userName = "user mock";
        User user = new User();
        user.setId(534);
        user.setUsername(userName);

        DataPointVO dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint1.setId(dataPointId1);
        dataPoint1.setXid("DP_TEST_123");
        dataPoint1.setName("point 1 mock");
        dataPoint1.defaultTextRenderer();

        PointLocatorVO pointLocator1 = new VirtualPointLocatorVO();
        dataPoint1.setPointLocator(pointLocator1);
        user.setEditPoint(dataPoint1);

        DataPointVO dataPoint2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(dataPointId2);
        dataPoint2.setXid("DP_TEST_124");
        dataPoint2.setName("point 2 mock");
        dataPoint2.defaultTextRenderer();

        PointLocatorVO pointLocator2 = new VirtualPointLocatorVO();
        dataPoint2.setPointLocator(pointLocator2);
        user.setEditPoint(dataPoint1);

        List<DataPointVO> dataPoints = Arrays.asList(dataPoint1, dataPoint2);

        View view = ViewTestUtils.newView(dataPoints, new ShareUser(user.getId(), 2), user);
        CustomView customView = ViewTestUtils.newCustomView(dataPoints, user);

        user.setDataPointPermissions(Arrays.asList(
                new DataPointAccess(dataPointId1, 2),
                new DataPointAccess(dataPointId2, 2)));
        user.setViewProfilePermissions(Arrays.asList(new ViewAccess(view.getId(), 2)));

        WatchList watchList = new WatchList();
        watchList.setId(467876);
        watchList.setName("watchlist mock");
        watchList.setXid("WL_TEST_12335");
        watchList.setPointList(dataPoints);
        user.setWatchList(watchList);

        return new Object[][] {
                {(Supplier<LongPollRequest>) () -> {
                    LongPollRequest longPollRequest = new LongPollRequest();
                    longPollRequest.setWatchList(true);
                    return longPollRequest;
                }, "watchListStates", view, customView, watchList, user, dataPoints, "WatchList"},
                {(Supplier<LongPollRequest>) () -> {
                    LongPollRequest longPollRequest = new LongPollRequest();
                    longPollRequest.setView(true);
                    longPollRequest.setViewId(view.getId());
                    return longPollRequest;
                }, "viewStates", view, customView, watchList, user, dataPoints, "View"},
                {(Supplier<LongPollRequest>) () -> {
                    LongPollRequest longPollRequest = new LongPollRequest();
                    longPollRequest.setView(true);
                    longPollRequest.setAnonViewId(view.getId());
                    return longPollRequest;
                }, "viewStates", view, customView, watchList, user, dataPoints, "AnonView"},
                {(Supplier<LongPollRequest>) () -> {
                    LongPollRequest longPollRequest = new LongPollRequest();
                    longPollRequest.setPointDetails(true);
                    return longPollRequest;
                }, "pointDetailsState", view, customView, watchList, user, dataPoints, "PointDetails"},
                {(Supplier<LongPollRequest>) () -> {
                    LongPollRequest longPollRequest = new LongPollRequest();
                    longPollRequest.setCustomView(true);
                    return longPollRequest;
                }, "customViewStates", view, customView, watchList, user, dataPoints, "CustomView"},
        };
    }

    private final LongPollRequest longPollRequest;
    private final String statesKey;
    private final View view;
    private final CustomView customView;
    private final WatchList watchList;
    private final User user;
    private final List<DataPointVO> dataPoints;

    public MiscDwrDoLongPollMultiThreadTest(Supplier<LongPollRequest> longPollRequest, String statesKey,
                                            View view, CustomView customView, WatchList watchList, User user,
                                            List<DataPointVO> dataPoints, String testing) {
        this.longPollRequest = longPollRequest.get();
        this.statesKey = statesKey;
        this.view = view;
        this.customView = customView;
        this.watchList = watchList;
        this.user = user;
        this.dataPoints = dataPoints;
    }

    private final int sessionId = 123351;
    private MiscDwr subject;
    private RuntimeManager runtimeManagerMock;

    @Before
    public void config() throws Exception {
        ViewService viewServiceMock = mock(ViewService.class);
        when(viewServiceMock.getView(anyInt())).thenReturn(view);

        whenNew(ViewService.class)
                .withAnyArguments()
                .thenReturn(viewServiceMock);

        PointValueCache pointValueCache = mock(PointValueCache.class);

        whenNew(PointValueCache.class)
                .withAnyArguments()
                .thenReturn(pointValueCache);

        ViewDwr viewDwr = new ViewDwr();
        whenNew(ViewDwr.class)
                .withAnyArguments()
                .thenReturn(viewDwr);

        mockStatic(SnippetContentGenerator.class);
        when(SnippetContentGenerator.generateContent(any(HttpServletRequest.class), anyString(), anyMap())).thenAnswer(a -> {
            Map<String, Object> params = (Map<String, Object>) a.getArguments()[2];
            PointValueTime pointValueTime = (PointValueTime) params.get("pointValue");
            return pointValueTime.toString();
        });

        runtimeManagerMock = mock(RuntimeManager.class);
        MockUtils.configMock(runtimeManagerMock, user);

        //WebContext mock
        HttpSession httpSession = new MockHttpSession();
        mockStatic(WebContextFactory.class);
        WebContext webContextMock = mock(WebContext.class);
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);

        if(longPollRequest.isView() && longPollRequest.getAnonViewId() != 0) {
            when(Common.getAnonymousView(eq(view.getId()))).thenReturn(view);
        }

        LocalizationContext localizationContextMock = mock(LocalizationContext.class);
        when(httpServletRequestMock.getAttribute(eq(Config.FMT_LOCALIZATION_CONTEXT + ".request"))).thenReturn(localizationContextMock);
        when(webContextMock.getHttpServletRequest()).thenReturn(httpServletRequestMock);

        when(webContextMock.getSession()).thenReturn(httpSession);
        when(WebContextFactory.get()).thenReturn(webContextMock);

        //Other
        when(Common.getCustomView()).thenReturn(customView);

        mockStatic(SystemSettingsDAO.class);

        EventService eventServiceMock = mock(EventService.class);
        whenNew(EventService.class)
                .withAnyArguments()
                .thenReturn(eventServiceMock);

        BaseDwr.initialize();

        subject = new MiscDwr();
    }

    @Test
    public void when_doLongPoll_with_five_other_values_each_two_points_then_five_list_with_states() throws Exception {

        //given:
        for(DataPointVO dataPoint: dataPoints) {
            DataPointRT dataPointRT = new ChangePointValueDataPointRtMock(Arrays.asList(
                    new PointValueTime(MangoValue.objectToValue(123.4), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.5), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.6), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.7), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.8), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.9), 12345L)
            ));
            when(runtimeManagerMock.getDataPoint(eq(dataPoint.getId()))).thenReturn(dataPointRT);
        }
        subject.initializeLongPoll(sessionId, longPollRequest);

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResult(5,
                subject::doLongPoll, sessionId,
                response -> response.get(statesKey));

        //then:
        assertEquals(5, result.size());
    }

    @Test
    public void when_doLongPoll_with_same_values_each_two_points_then_one_list_with_states() throws Exception {

        //given:
        for(DataPointVO dataPoint: dataPoints) {
            DataPointRT dataPointRT = new ChangePointValueDataPointRtMock(Arrays.asList(
                    new PointValueTime(MangoValue.objectToValue(123.13), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L)
            ));
            when(runtimeManagerMock.getDataPoint(eq(dataPoint.getId()))).thenReturn(dataPointRT);
        }
        subject.initializeLongPoll(sessionId, longPollRequest);

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResult(5,
                subject::doLongPoll, sessionId,
                response -> response.get(statesKey));

        //then:
        assertEquals(1, result.size());
    }

    @Test
    public void when_doLongPoll_with_five_other_values_each_two_points_then_five_list_each_two_states() throws Exception {

        //given:
        for(DataPointVO dataPoint: dataPoints) {
            DataPointRT dataPointRT = new ChangePointValueDataPointRtMock(Arrays.asList(
                    new PointValueTime(MangoValue.objectToValue(123.4), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.5), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.6), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.7), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.8), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.9), 12345L)
            ));
            when(runtimeManagerMock.getDataPoint(eq(dataPoint.getId()))).thenReturn(dataPointRT);
        }
        subject.initializeLongPoll(sessionId, longPollRequest);

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResult(5,
                subject::doLongPoll, sessionId,
                response -> response.get(statesKey));

        //then:
        for(Object object: result) {
            if (object instanceof List) {
                assertEquals(2, ((List<?>) object).size());
            }
        }
    }

    @Test
    public void when_doLongPoll_with_same_values_each_two_points_then_one_list_each_two_states() throws Exception {

        //given:
        for(DataPointVO dataPoint: dataPoints) {
            DataPointRT dataPointRT = new ChangePointValueDataPointRtMock(Arrays.asList(
                    new PointValueTime(MangoValue.objectToValue(123.13), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L),
                    new PointValueTime(MangoValue.objectToValue(123.12), 12345L)
            ));
            when(runtimeManagerMock.getDataPoint(eq(dataPoint.getId()))).thenReturn(dataPointRT);
        }
        subject.initializeLongPoll(sessionId, longPollRequest);

        //when:
        List<Object> result = TestConcurrentUtils.functionWithResult(5,
                subject::doLongPoll, sessionId,
                response -> response.get(statesKey));

        //then:
        for(Object object: result) {
            if (object instanceof List) {
                assertEquals(2, ((List<?>) object).size());
            }
        }
    }
}