package utils;

import com.serotonin.mango.rt.event.ScheduledEvent;
import org.scada_lts.service.*;
import org.scada_lts.utils.EmailToSmsUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public final class ScheduledInactiveEventTestUtils {

    private ScheduledInactiveEventTestUtils() {}

    public static InactiveEventsProvider createProviderMock(boolean dailyLimitSentEmails, CommunicationChannel channel,
                                                                         ScheduledEvent scheduledEvent1, ScheduledEvent scheduledEvent2) {

        List<ScheduledEvent> events = Stream.of(scheduledEvent1, scheduledEvent2)
                .sorted(Comparator.comparingInt(a -> a.getEvent().getId()))
                .collect(Collectors.toList());

        InactiveEventsProvider service = mock(InactiveEventsProvider.class);
        when(service.getChannel()).thenReturn(channel);

        if(dailyLimitSentEmails) {
            when(service.getScheduledEvents(0)).thenReturn(Collections.emptyList());
            when(service.getScheduledEvents(1)).thenReturn(Arrays.asList(events.get(0)));
            when(service.getScheduledEvents(2)).thenReturn(events);
            when(service.getScheduledEvents(3)).thenReturn(events);
        } else {
            when(service.getScheduledEvents(anyInt())).thenReturn(events);
        }
        return service;
    }

    public static Set<String> addedAtDomain(Set<String> addresses, String domain, CommunicationChannelTypable type) {
        if(CommunicationChannelType.SMS.equals(type)) {
            return EmailToSmsUtils.addedAtDomain(addresses, domain);
        }
        return addresses;
    }
}
