package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailToSmsHandlerRtTest {

    private EmailToSmsHandlerRT emailToSmsHandlerRT;
    private EventInstance event;

    private Set<String> addressesExpected;
    private String domainAt;
    private String tel1;
    private String tel2;

    @Before
    public void config() {
        String domain = "domainTest.com";
        tel1 = "123456789";
        tel2 = "0987654321";

        CommunicationChannelType type = CommunicationChannelType.SMS;

        event = mock(EventInstance.class);

        Set<String> addresses = new HashSet<>();
        addresses.add(tel1);
        addresses.add(tel2);

        MailingListService service = mock(MailingListService.class);
        when(service.getRecipientAddresses(any(),any(),eq(type)))
                .thenReturn(addresses);

        SystemSettingsService systemSettingsService = mock(SystemSettingsService.class);
        when(systemSettingsService.getSMSDomain()).thenReturn(domain);

        emailToSmsHandlerRT = new EmailToSmsHandlerRT(mock(EventHandlerVO.class),
                mock(ScheduledExecuteInactiveEventService.class),service,
                systemSettingsService);

        domainAt = "@" + domain;
        addressesExpected = new HashSet<>();

    }

    @Test
    public void test_getActiveRecipients() {

        //given:
        addressesExpected.add(tel1 + domainAt);
        addressesExpected.add(tel2 + domainAt);

        //when:
        Set<String> result = emailToSmsHandlerRT.getActiveRecipients(event);

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void test_getInactiveRecipients() {

        //given:
        addressesExpected.add(tel1 + domainAt);
        addressesExpected.add(tel2 + domainAt);

        //when:
        Set<String> result = emailToSmsHandlerRT.getInactiveRecipients(event);

        //then:
        assertEquals(addressesExpected, result);
    }
}