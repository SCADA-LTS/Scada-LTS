package com.serotonin.mango.rt.dataSource.http;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.dataSource.http.HttpRetrieverDataSourceVO;
import com.serotonin.web.i18n.LocalizableException;
import org.apache.commons.httpclient.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

        
@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpRetrieverDataSourceRT.class, Common.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class HttpRetrieverDataSourceRTTest {

    private HttpClient clientMock;

    @Before
    public void config() {
        mockStatic(Common.class);
        clientMock = mock(HttpClient.class);
        when(Common.getHttpClient(anyInt())).thenReturn(clientMock);
    }

    @Test
    public void when_getData_for_executeMethod_return_401_then_retries() throws LocalizableException, IOException {
        //given:
        HttpRetrieverDataSourceVO dataSourceVO = new HttpRetrieverDataSourceVO();
        dataSourceVO.setId(123);

        StopSleepRT stopSleepRT = mock(StopSleepRT.class);
        HttpRetrieverDataSourceRT dataSourceRT = new HttpRetrieverDataSourceRT(dataSourceVO, stopSleepRT);

        when(clientMock.executeMethod(any())).thenReturn(401);

        //when:
        String data = dataSourceRT.getData("url", 1000, 20, true, dataSourceVO.getReactivation(), Collections.emptyList());

        //then:
        verify(clientMock, times(21)).executeMethod(any());
    }

    @Test
    public void when_getDataTest_then_not_invoke_sleep() throws IOException {

        //given:
        HttpRetrieverDataSourceVO dataSourceVO = new HttpRetrieverDataSourceVO();
        dataSourceVO.setId(123);

        StopSleepRT stopSleepRTMock = mock(StopSleepRT.class);
        HttpRetrieverDataSourceRT dataSourceRT = new HttpRetrieverDataSourceRT(dataSourceVO, stopSleepRTMock);

        when(clientMock.executeMethod(any())).thenReturn(401);

        //when:
        try {
            String data = dataSourceRT.getDataTest("url", 1000, 20, Collections.emptyList());
        } catch (LocalizableException e) {
            e.printStackTrace();
        }

        //then:
        verify(stopSleepRTMock, times(0)).sleep(any());
    }

    @Test
    public void when_getDataTest_then_not_invoke_stop() throws IOException {

        //given:
        HttpRetrieverDataSourceVO dataSourceVO = new HttpRetrieverDataSourceVO();
        dataSourceVO.setId(123);

        StopSleepRT stopSleepRTMock = mock(StopSleepRT.class);
        HttpRetrieverDataSourceRT dataSourceRT = new HttpRetrieverDataSourceRT(dataSourceVO, stopSleepRTMock);

        when(clientMock.executeMethod(any())).thenReturn(401);

        //when:
        try {
            String data = dataSourceRT.getDataTest("url", 1000, 20, Collections.emptyList());
        } catch (LocalizableException e) {
            e.printStackTrace();
        }

        //then:
        verify(stopSleepRTMock, times(0)).stop();
    }

    @Test(expected = LocalizableException.class)
    public void when_getDataTest_then_throw_LocalizableException() throws IOException, LocalizableException {
        //given:
        HttpRetrieverDataSourceVO dataSourceVO = new HttpRetrieverDataSourceVO();
        dataSourceVO.setId(123);

        StopSleepRT stopSleepRTMock = mock(StopSleepRT.class);
        HttpRetrieverDataSourceRT dataSourceRT = new HttpRetrieverDataSourceRT(dataSourceVO, stopSleepRTMock);

        when(clientMock.executeMethod(any())).thenReturn(401);

        //when:
        String data = dataSourceRT.getDataTest("url", 1000, 20, Collections.emptyList());

        //then: exception
    }

    @Test
    public void when_getData_for_executeMethod_return_200_then_no_retries() throws IOException, LocalizableException {
        //given:
        HttpRetrieverDataSourceVO dataSourceVO = new HttpRetrieverDataSourceVO();
        dataSourceVO.setId(123);

        StopSleepRT stopSleepRTMock = mock(StopSleepRT.class);
        HttpRetrieverDataSourceRT dataSourceRT = new HttpRetrieverDataSourceRT(dataSourceVO, stopSleepRTMock);

        when(clientMock.executeMethod(any())).thenReturn(200);

        //when:
        String data = dataSourceRT.getData("url", 1000, 20, true, dataSourceVO.getReactivation(), Collections.emptyList());

        //then:
        verify(clientMock, times(1)).executeMethod(any());
    }



}