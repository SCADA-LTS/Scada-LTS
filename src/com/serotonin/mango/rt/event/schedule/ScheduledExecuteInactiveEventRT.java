package com.serotonin.mango.rt.event.schedule;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelTypable;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import org.springframework.dao.EmptyResultDataAccessException;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

public class ScheduledExecuteInactiveEventRT implements ModelTimeoutClient<Boolean> {

    private static final Log log = LogFactory.getLog(ScheduledExecuteInactiveEventRT.class);

    private TimerTask task;
    private final CommunicationChannel communicationChannel;
    private final ScheduledExecuteInactiveEventService service;
    private final AtomicInteger limit;
    private final DataPointService dataPointService;
    private final DataSourceService dataSourceService;
    private volatile boolean communicatedLimit;
    private volatile boolean lock;

    private final Queue<Execute<?, ?>> toExecute;

    public ScheduledExecuteInactiveEventRT(CommunicationChannel communicationChannel,
                                           ScheduledExecuteInactiveEventService service,
                                           DataPointService dataPointService,
                                           DataSourceService dataSourceService) {
        this.communicationChannel = communicationChannel;
        this.limit = new AtomicInteger(communicationChannel.isDailyLimitSent() ?
                communicationChannel.getDailyLimitSentNumber() : Integer.MAX_VALUE);
        this.service = service;
        this.dataPointService = dataPointService;
        this.dataSourceService = dataSourceService;
        this.toExecute = new ConcurrentLinkedQueue<>();
        this.communicatedLimit = false;
        this.lock = false;
    }

    public void initialize() {
        try {
            CronTimerTrigger activeTrigger = new CronTimerTrigger(communicationChannel.getSendingActivationCron());
            task = new ModelTimeoutTask<>(activeTrigger, this, true);
        } catch (ParseException e) {
            log.error(e);
        }
    }

    public void terminate() {
        if (task != null)
            task.cancel();
    }

    @Override
    public void scheduleTimeout(Boolean model, long fireTime) {
        DateTime dateTime = new DateTime(fireTime);
        Set<String> addresses = communicationChannel.getActiveAdresses(dateTime);
        if(!addresses.isEmpty()) {
            schedule(addresses);
            execute(addresses, fireTime);
        }
    }

    private synchronized void schedule(Set<String> addresses) {
        if(isSchedule()) {
            this.lock = true;
            List<ScheduledEvent> scheduledEvents = service.getScheduledEvents(communicationChannel, limit.get());
            for (ScheduledEvent event : scheduledEvents) {
                toExecute.add(new Execute<>(this::send, event, addresses));
                if(communicationChannel.isDailyLimitSent())
                    limit.decrementAndGet();
            }
        }
    }

    private boolean isSchedule() {
        return (!communicationChannel.isDailyLimitSent() || limit.get() > 0) && !lock;
    }

    private void execute(Set<String> addresses, long fireTime) {
        Execute execute = toExecute.poll();
        while (execute != null) {
            boolean executed = execute.execute();
            if(communicationChannel.isDailyLimitSent()) {
                if (!executed && limit.get() < communicationChannel.getDailyLimitSentNumber()) {
                    limit.incrementAndGet();
                } else if (!communicatedLimit
                        && limit.get() == 0) {
                    communicatedLimit = true;
                    communicatedLimit = communicateLimit(addresses, fireTime);
                }
            }
            execute = toExecute.poll();
        }
        this.lock = false;
    }

    private boolean communicateLimit(Set<String> addresses, long fireTime) {
        LocalizableMessage dailyLimitExceededMsg = new LocalizableMessage("mailingLists.dailyLimitExceeded");
        EventType eventType = new DataPointEventType();
        EventInstance event = new EventInstance(eventType, fireTime,false,
                AlarmLevels.INFORMATION, dailyLimitExceededMsg, Collections.emptyMap());
        CommunicationChannelTypable type = communicationChannel.getType();

        boolean sent = type.sendMsg(event, addresses,"Limit");
        if(sent) {
            log.info("Last message sent today for a list of addresses id: " + communicationChannel.getChannelId() + ", type: " + communicationChannel.getType());
            return true;
        }
        return false;
    }

    private boolean send(ScheduledEvent scheduledEvent, Set<String> addresses) {

        EventInstance event = scheduledEvent.getEvent();
        EventType eventType = event.getEventType();
        EventHandlerVO eventHandler = scheduledEvent.getEventHandler();
        CommunicationChannelTypable type = communicationChannel.getType();

        if (isExists(eventType)) {
            String eventHandlerAlias = eventHandler.getAlias();
            String alias = eventHandlerAlias == null || eventHandlerAlias.isEmpty() ? "Delay email" : eventHandlerAlias;
            boolean sent = type.sendMsg(event, addresses, alias);
            if(sent) {
                service.unscheduleEvent(scheduledEvent, communicationChannel);
                return true;
            }
            return false;
        }
        service.unscheduleEvent(scheduledEvent, communicationChannel);
        return false;
    }

    private boolean isExists(EventType eventType) {
        return (!(eventType instanceof DataPointEventType) || (isDataSourceExists(eventType) && isDataPointExists(eventType)))
                && (!(eventType instanceof DataSourceEventType) || isDataSourceExists(eventType));
    }

    private boolean isDataPointExists(EventType eventType) {
        try {
            DataPointVO dataPoint = dataPointService.getDataPoint(eventType.getDataPointId());
            return dataPoint != null;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private boolean isDataSourceExists(EventType eventType) {
        try {
            DataSourceVO<?> dataSource = dataSourceService.getDataSource(eventType.getDataSourceId());
            return dataSource != null;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private static class Execute<N, M> {
        BiPredicate<N, M> predicate;
        N arg1;
        M arg2;

        public Execute(BiPredicate<N, M> predicate, N arg1, M arg2) {
            this.predicate = predicate;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        boolean execute() {
            return predicate.test(arg1, arg2);
        }
    }
}
