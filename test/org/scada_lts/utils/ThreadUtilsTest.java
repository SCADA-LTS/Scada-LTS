package org.scada_lts.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.mango.service.SystemSettingsService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThreadUtilsTest {

    private SystemSettingsService systemSettingsServiceMock;

    @Before
    public void config() {
        this.systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getThreadsNameAdditionalLength()).thenReturn(5);
    }

    @Test
    public void when_reduceName_for_abc12345_and_limit_5_then_abc12_() {
        //given:
        String expected = "abc12..";

        //when:
        String result = ThreadUtils.reduceName("abc12345", systemSettingsServiceMock);

        //then:
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_reduceName_for_abc123_and_limit_5_then_abc12_() {
        //given:
        String expected = "abc12..";

        //when:
        String result = ThreadUtils.reduceName("abc123", systemSettingsServiceMock);

        //then:
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_reduceName_for_abc12_and_limit_5_then_abc12() {
        //given:
        String expected = "abc12";

        //when:
        String result = ThreadUtils.reduceName("abc12", systemSettingsServiceMock);

        //then:
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_reduceName_for_abc1_and_limit_5_then_abc1() {
        //given:
        String expected = "abc1";

        //when:
        String result = ThreadUtils.reduceName("abc1", systemSettingsServiceMock);

        //then:
        Assert.assertEquals(expected, result);
    }
}