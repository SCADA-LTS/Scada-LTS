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
    private final AtomicInteger fails;
    private final AtomicInteger currentNumberExecuted;
    private final DataPointService dataPointService;
    private final DataSourceService dataSourceService;

    private final Queue<Execute<ScheduledEvent>> toExecute;

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
        this.fails = new AtomicInteger(0);
        this.currentNumberExecuted = new AtomicInteger(0);

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
        if (communicationChannel.isActiveFor(dateTime)) {
            Set<String> addresses = communicationChannel.getActiveAdresses(dateTime);
            if(!addresses.isEmpty() && isSchedule()) {
                schedule(addresses, fireTime);
                execute();
            }
        }
    }

    public int getCurrentNumberExecuted() {
        return currentNumberExecuted.get();
    }

    private synchronized void schedule(Set<String> addresses, long fireTime) {
        if(isSchedule()) {
            List<ScheduledEvent> scheduledEvents = service.getScheduledEvents(communicationChannel, limit.get());
            for (ScheduledEvent event : scheduledEvents) {
                toExecute.offer(new Execute<>(this::send, event,
                        new ExecuteData(communicationChannel, limit, fails, addresses, fireTime)));
                if(fails.get() > 0)
                    fails.decrementAndGet();
                if (communicationChannel.isDailyLimitSent()) {
                    if(limit.get() > 0)
                        limit.decrementAndGet();
                }
                currentNumberExecuted.incrementAndGet();
            }
        }
    }

    private boolean isSchedule() {
        return toExecute.isEmpty() && ((!communicationChannel.isDailyLimitSent()
                || limit.get() > 0) || fails.get() > 0);
    }

    private void execute() {
        Execute execute = toExecute.poll();
        while (execute != null) {
            execute.execute();
            execute = toExecute.poll();
        }
    }

    private static boolean communicateLimit(Set<String> addresses, long fireTime, CommunicationChannel communicationChannel) {
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
            String alias = eventHandlerAlias == null || eventHandlerAlias.isEmpty() ? "Delay msg" : eventHandlerAlias;
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

    private static class Execute<N> {
        BiPredicate<N, Set<String>> predicate;
        ExecuteData executeData;
        N arg1;

        public Execute(BiPredicate<N, Set<String>> predicate, N arg1, ExecuteData executeData) {
            this.predicate = predicate;
            this.executeData = executeData;
            this.arg1 = arg1;
        }

        void execute() {
            Set<String> addresses = executeData.getAddresses();
            boolean executed = predicate.test(arg1, addresses);

            long fireTime = executeData.getFireTime();
            CommunicationChannel channel = executeData.getCommunicationChannel();
            AtomicInteger limit = executeData.getLimit();
            AtomicInteger fails = executeData.getFails();

            if (!executed)
                fails.incrementAndGet();

            if(channel.isDailyLimitSent()) {
                if (limit.get() == 0 && fails.get() == 0) {
                    boolean communicated = ScheduledExecuteInactiveEventRT.communicateLimit(addresses, fireTime, channel);
                    if(communicated)
                        limit.decrementAndGet();
                }
            }
        }
    }

    private class ExecuteData {
        CommunicationChannel communicationChannel;
        AtomicInteger limit;
        AtomicInteger fails;
        Set<String> addresses;
        long fireTime;

        public ExecuteData(CommunicationChannel communicationChannel, AtomicInteger limit, AtomicInteger fails,
                           Set<String> addresses, long fireTime) {
            this.communicationChannel = communicationChannel;
            this.limit = limit;
            this.fails = fails;
            this.addresses = addresses;
            this.fireTime = fireTime;
        }

        public CommunicationChannel getCommunicationChannel() {
            return communicationChannel;
        }

        public AtomicInteger getLimit() {
            return limit;
        }

        public Set<String> getAddresses() {
            return addresses;
        }

        public long getFireTime() {
            return fireTime;
        }

        public AtomicInteger getFails() {
            return fails;
        }
    }

}
