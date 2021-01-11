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
import org.scada_lts.service.InactiveEventsProvider;
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
    private final InactiveEventsProvider inactiveEventsProvider;
    private final int limit;

    private final AtomicInteger nonBlockingLock;
    private final AtomicInteger failsCounter;
    private final AtomicInteger limitLock;
    private final AtomicInteger communicateLimitLock;

    private final AtomicInteger currentExecutedCounter;
    private final AtomicInteger currentScheduledCounter;
    private final DataPointService dataPointService;
    private final DataSourceService dataSourceService;
    private final Queue<Execute<ScheduledEvent>> toExecute;

    public ScheduledExecuteInactiveEventRT(ScheduledExecuteInactiveEventService service,
                                           InactiveEventsProvider inactiveEventsProvider,
                                           DataPointService dataPointService,
                                           DataSourceService dataSourceService) {
        this.communicationChannel = inactiveEventsProvider.getCommunicationChannel();
        this.limit = communicationChannel.isDailyLimitSent() ?
                communicationChannel.getDailyLimitSentNumber() : 300;
        this.limitLock = new AtomicInteger(limit);
        this.service = service;
        this.inactiveEventsProvider = inactiveEventsProvider;
        this.dataPointService = dataPointService;
        this.dataSourceService = dataSourceService;
        this.toExecute = new ConcurrentLinkedQueue<>();
        this.failsCounter = new AtomicInteger(0);
        this.communicateLimitLock = new AtomicInteger(0);
        this.currentExecutedCounter = new AtomicInteger(0);
        this.currentScheduledCounter = new AtomicInteger(0);
        this.nonBlockingLock = new AtomicInteger(0);
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
        toExecute.clear();
    }

    @Override
    public void scheduleTimeout(Boolean model, long fireTime) {
        DateTime dateTime = new DateTime(fireTime);
        if (communicationChannel.isActiveFor(dateTime)) {
            Set<String> addresses = communicationChannel.getActiveAdresses(dateTime);
            if(!addresses.isEmpty()) {
                schedule(addresses, fireTime);
                execute();
            }
        }
    }

    public int getCurrentExecutedNumber() {
        return currentExecutedCounter.get();
    }

    public int getCurrentScheduledNumber() {
        return currentScheduledCounter.get();
    }

    private void schedule(Set<String> addresses, long fireTime) {
        if(isSchedule() && nonBlockingLock.getAndDecrement() == 0) {
            try {
                List<ScheduledEvent> scheduledEvents = inactiveEventsProvider
                        .getScheduledEvents(limitLock.get());
                for (ScheduledEvent event : scheduledEvents) {
                    toExecute.offer(new Execute<>(this::send, event, new ExecuteData(communicationChannel, limitLock,
                            failsCounter, communicateLimitLock, addresses, fireTime)));
                    currentScheduledCounter.incrementAndGet();
                    if (communicationChannel.isDailyLimitSent()) {
                        if (limitLock.get() > 0)
                            limitLock.decrementAndGet();
                    }
                }
            } finally {
                nonBlockingLock.set(0);
            }
        }
    }

    private boolean isSchedule() {
        return toExecute.peek() == null && (!communicationChannel.isDailyLimitSent() || limitLock.get() > 0);
    }

    private void execute() {
        Execute execute = toExecute.poll();
        AtomicInteger oneExecuteLimit = new AtomicInteger(limit);
        while (execute != null && oneExecuteLimit.getAndDecrement() > 0) {
            execute.execute();
            execute = toExecute.poll();
            currentExecutedCounter.incrementAndGet();
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
                inactiveEventsProvider.confirm(scheduledEvent);
                return true;
            }
            inactiveEventsProvider.repeat(scheduledEvent);
            return false;
        }
        service.unscheduleEvent(scheduledEvent, communicationChannel);
        inactiveEventsProvider.confirm(scheduledEvent);
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

            if(channel.isDailyLimitSent()) {
                AtomicInteger limit = executeData.getLimit();
                AtomicInteger fails = executeData.getFails();
                AtomicInteger lock = executeData.getLock();
                if (!executed)
                    fails.incrementAndGet();
                else if (limit.get() == 0 && fails.get() == 0 && lock.getAndDecrement() == 0) {
                    boolean communicated = false;
                    try {
                        communicated = ScheduledExecuteInactiveEventRT.communicateLimit(addresses, fireTime, channel);
                    } finally {
                        if(!communicated)
                            lock.set(0);
                    }
                }
            }
        }
    }

    private static class ExecuteData {
        CommunicationChannel communicationChannel;
        AtomicInteger limit;
        AtomicInteger fails;
        AtomicInteger lock;
        Set<String> addresses;
        long fireTime;

        public ExecuteData(CommunicationChannel communicationChannel, AtomicInteger limit, AtomicInteger fails,
                           AtomicInteger lock, Set<String> addresses, long fireTime) {
            this.communicationChannel = communicationChannel;
            this.limit = limit;
            this.fails = fails;
            this.lock = lock;
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

        public AtomicInteger getLock() {
            return lock;
        }
    }

}
