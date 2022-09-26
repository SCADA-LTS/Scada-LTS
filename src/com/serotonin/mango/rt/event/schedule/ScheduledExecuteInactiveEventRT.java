package com.serotonin.mango.rt.event.schedule;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.maint.work.AfterWork;
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
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class ScheduledExecuteInactiveEventRT implements ModelTimeoutClient<Boolean> {

    private static final Log LOG = LogFactory.getLog(ScheduledExecuteInactiveEventRT.class);

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
                                           DataSourceService dataSourceService,
                                           int defaultLimit) {
        this.communicationChannel = inactiveEventsProvider.getCommunicationChannel();
        this.limit = communicationChannel.isDailyLimitSent() ?
                communicationChannel.getDailyLimitSentNumber() : defaultLimit;
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
            LOG.error(e);
        }
    }

    public void terminate() {
        if (task != null)
            task.cancel();
        toExecute.clear();
        inactiveEventsProvider.clear();
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
        if(limit <= 0) {
            return;
        }
        AtomicInteger oneExecuteLimit = new AtomicInteger(limit);
        Execute execute;
        while ((execute = toExecute.poll()) != null) {
            execute.execute();
            currentExecutedCounter.incrementAndGet();
            if(oneExecuteLimit.decrementAndGet() < 1)
                break;
        }
    }

    private static void communicateLimit(Set<String> addresses, long fireTime, CommunicationChannel channel,
                                         AtomicInteger communicateLimitLock) {
        LocalizableMessage dailyLimitExceededMsg = new LocalizableMessage("mailingLists.dailyLimitExceeded");
        EventType eventType = new DataPointEventType();
        EventInstance event = new EventInstance(eventType, fireTime,false,
                AlarmLevels.INFORMATION, dailyLimitExceededMsg, Collections.emptyMap());
        CommunicationChannelTypable type = channel.getType();

        type.sendLimit(event, addresses,"Limit", new AfterWork() {

            @Override
            public void workSuccess() {
                LOG.info("Last message sent today for a list of addresses id: " + channel.getChannelId() + ", type: "
                        + channel.getType());
            }

            @Override
            public void workFail(Exception exception) {
                LOG.error(exception.getMessage());
                communicateLimitLock.set(0);
            }
        });
    }

    private void send(ScheduledEvent scheduledEvent, Set<String> addresses) {
        EventInstance event = scheduledEvent.getEvent();
        EventType eventType = event.getEventType();
        EventHandlerVO eventHandler = scheduledEvent.getEventHandler();
        CommunicationChannelTypable type = communicationChannel.getType();
        Optional<DataPointVO> dataPoint = getDataPoint(eventType);
        if (dataPoint.isPresent() && isExists(eventType)) {

            Map<String, Object> context = createContext(dataPoint.get());
            EventInstance toSend = event.copyWithContext(context);

            String eventHandlerAlias = eventHandler.getAlias();
            String alias = eventHandlerAlias == null || eventHandlerAlias.isEmpty() ? "Delay msg" : eventHandlerAlias;
            type.sendMsg(toSend, addresses, alias, new AfterWork() {

                @Override
                public void workSuccess() {
                    service.unscheduleEvent(scheduledEvent, communicationChannel);
                    inactiveEventsProvider.confirm(scheduledEvent);
                }

                @Override
                public void workFail(Exception exception) {
                    LOG.error(exception.getMessage());
                    inactiveEventsProvider.repeat(scheduledEvent);
                    failsCounter.incrementAndGet();
                }
            });
        } else {
            service.unscheduleEvent(scheduledEvent, communicationChannel);
            inactiveEventsProvider.confirm(scheduledEvent);
        }
    }

    private Map<String, Object> createContext(DataPointVO dataPoint) {
        Map<String,Object> context = new HashMap<>();
        context.put("point", dataPoint);
        return context;
    }

    private boolean isExists(EventType eventType) {
        return (!(eventType instanceof DataPointEventType) || isDataSourceExists(eventType))
                && (!(eventType instanceof DataSourceEventType) || isDataSourceExists(eventType));
    }

    private Optional<DataPointVO> getDataPoint(EventType eventType) {
        try {
            DataPointVO dataPoint = dataPointService.getDataPoint(eventType.getDataPointId());
            return Optional.ofNullable(dataPoint);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
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
        BiConsumer<N, Set<String>> consumer;
        ExecuteData executeData;
        N arg1;

        public Execute(BiConsumer<N, Set<String>> consumer, N arg1, ExecuteData executeData) {
            this.consumer = consumer;
            this.executeData = executeData;
            this.arg1 = arg1;
        }

        void execute() {
            Set<String> addresses = executeData.getAddresses();
            consumer.accept(arg1, addresses);

            CommunicationChannel channel = executeData.getCommunicationChannel();

            if(channel.isDailyLimitSent()) {
                AtomicInteger limit = executeData.getLimit();
                AtomicInteger fails = executeData.getFails();
                AtomicInteger lock = executeData.getLock();
                long fireTime = executeData.getFireTime();

                if (limit.get() == 0 && fails.get() == 0 && lock.getAndDecrement() == 0) {
                    communicateLimit(addresses, fireTime, channel, lock);
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
