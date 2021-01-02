package utils;

import com.serotonin.mango.rt.event.ScheduledEvent;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public final class ScheduledInactiveEventTestUtils {

    private ScheduledInactiveEventTestUtils() {}

    public static ScheduledExecuteInactiveEventService createServiceMock(boolean dailyLimitSentEmails, CommunicationChannel channel,
                                                                         ScheduledEvent scheduledEvent1, ScheduledEvent scheduledEvent2) {

        List<ScheduledEvent> events = Stream.of(scheduledEvent1, scheduledEvent2)
                .sorted(Comparator.comparingInt(a -> a.getEvent().getId()))
                .collect(Collectors.toList());

        ScheduledExecuteInactiveEventService service = mock(ScheduledExecuteInactiveEventService.class);

        if(dailyLimitSentEmails) {
            when(service.getScheduledEvents(channel, 0)).thenReturn(Collections.emptyList());
            when(service.getScheduledEvents(channel, 1)).thenReturn(Arrays.asList(events.get(0)));
            when(service.getScheduledEvents(channel, 2)).thenReturn(events);
            when(service.getScheduledEvents(channel, 3)).thenReturn(events);
        } else {
            when(service.getScheduledEvents(eq(channel), anyInt())).thenReturn(events);
        }
        return service;
    }
}
