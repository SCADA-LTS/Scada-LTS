package com.serotonin.mango.util;

import com.serotonin.mango.view.View;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.mango.service.ViewService;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

public class ViewControllerUtilsTest {

    private static final int VIEW_ID = 123;
    private static final String VIEW_XID = "VIEW_TEXT_XID";

    private static final String VIEW_XID_PARAMETER_NAME = "viewXid";
    private static final String VIEW_ID_PARAMETER_NAME = "viewId";

    private View viewExpected;
    private HttpServletRequest requestMock;
    private ViewService viewServiceMock;

    @Before
    public void config() {
        viewExpected = new View();
        viewExpected.setId(VIEW_ID);
        viewExpected.setXid(VIEW_XID);
        viewServiceMock = mock(ViewService.class);
        when(viewServiceMock.getView(eq(VIEW_ID))).thenReturn(viewExpected);
        when(viewServiceMock.getViewByXid(eq(VIEW_XID))).thenReturn(viewExpected);
        requestMock = mock(HttpServletRequest.class);
    }

    @Test
    public void when_getViewCurrent_for_parameter_viewId_as_not_number_then_null() {

        //given:
        when(requestMock.getParameter(eq(VIEW_ID_PARAMETER_NAME))).thenReturn("abc");

        //when
        View result = ViewControllerUtils.getViewCurrent(requestMock, viewServiceMock);

        //then:
        assertNull(result);
    }

    @Test
    public void when_getViewCurrent_for_parameter_viewId_then_view() {

        //given:
        when(requestMock.getParameter(eq(VIEW_ID_PARAMETER_NAME))).thenReturn(String.valueOf(VIEW_ID));

        //when
        View result = ViewControllerUtils.getViewCurrent(requestMock, viewServiceMock);

        //then:
        assertEquals(viewExpected.getId(), result.getId());
    }

    @Test
    public void when_getViewCurrent_for_parameter_viewId_and_viewXid_then_view() {

        //given:
        when(requestMock.getParameter(eq(VIEW_ID_PARAMETER_NAME))).thenReturn(String.valueOf(VIEW_ID));
        when(requestMock.getParameter(eq(VIEW_XID_PARAMETER_NAME))).thenReturn(VIEW_XID);

        //when
        View result = ViewControllerUtils.getViewCurrent(requestMock, viewServiceMock);

        //then:
        assertEquals(viewExpected.getId(), result.getId());
    }

    @Test
    public void when_getViewCurrent_for_parameter_viewXid_then_view() {

        //given:
        when(requestMock.getParameter(eq(VIEW_XID_PARAMETER_NAME))).thenReturn(VIEW_XID);

        //when
        View result = ViewControllerUtils.getViewCurrent(requestMock, viewServiceMock);

        //then:
        assertEquals(viewExpected.getId(), result.getId());
    }

    @Test
    public void when_getViewCurrent_for_parameter_viewId_not_exist_then_null() {

        //given:
        when(requestMock.getParameter(eq(VIEW_ID_PARAMETER_NAME))).thenReturn("321");

        //when
        View result = ViewControllerUtils.getViewCurrent(requestMock, viewServiceMock);

        //then:
        assertNull(result);
    }

    @Test
    public void when_getViewCurrent_for_parameter_viewXid_not_exist_then_null() {

        //given:
        when(requestMock.getParameter(eq(VIEW_XID_PARAMETER_NAME))).thenReturn("321");

        //when
        View result = ViewControllerUtils.getViewCurrent(requestMock, viewServiceMock);

        //then:
        assertNull(result);
    }
}