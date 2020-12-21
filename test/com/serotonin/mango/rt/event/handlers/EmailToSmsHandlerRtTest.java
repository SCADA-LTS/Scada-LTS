package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

public class EmailToSmsHandlerRtTest {

    private EmailToSmsHandlerRT emailToSmsHandlerRT;
    private CommunicationChannel channelSms;
    private CommunicationChannel channelEmail;
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

        MailingList mailingList= mock(MailingList.class);
        channelSms = CommunicationChannel.newChannel(mailingList, CommunicationChannelType.SMS);
        channelEmail = CommunicationChannel.newChannel(mailingList, CommunicationChannelType.EMAIL);

        event = mock(EventInstance.class);

        Set<String> addresses = new HashSet<>();
        addresses.add(tel1);
        addresses.add(tel2);

        MailingListService service = mock(MailingListService.class);
        when(service.getRecipientAddresses(any(),any(),eq(CommunicationChannelType.SMS)))
                .thenReturn(addresses);

        when(service.getRecipientAddresses(any(),eq(channelSms)))
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
        Set<String> address = emailToSmsHandlerRT.getInactiveRecipients(event);

        //then:
        assertEquals(addressesExpected, address);
    }

    @Test
    public void test_getInactiveRecipients_with_communicationChannel_sms_then_not_empty_list() {

        //given:
        addressesExpected.add(tel1 + domainAt);
        addressesExpected.add(tel2 + domainAt);

        //when:
        Set<String> result = emailToSmsHandlerRT.getActiveRecipients(event, channelSms);

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void test_getInactiveRecipients_with_communicationChannel_email_then_empty_list() {

        //when:
        Set<String> result = emailToSmsHandlerRT.getActiveRecipients(event, channelEmail);

        //then:
        assertEquals(Collections.emptySet(), result);
    }

}