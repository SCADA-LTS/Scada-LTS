package com.serotonin.mango.rt.event.schedule;

import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import org.springframework.dao.EmptyResultDataAccessException;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledExecuteInactiveEventRT implements ModelTimeoutClient<Boolean> {

    private final Log log = LogFactory.getLog(ScheduledExecuteInactiveEventRT.class);

    private TimerTask task;
    private final MailingList mailingList;
    private final ScheduledExecuteInactiveEventService service;
    private final EventManager eventManager;
    private final AtomicInteger limit;
    private final DataPointService dataPointService;
    private final DataSourceService dataSourceService;

    public ScheduledExecuteInactiveEventRT(MailingList mailingList,
                                           ScheduledExecuteInactiveEventService service,
                                           EventManager eventManager,
                                           DataPointService dataPointService,
                                           DataSourceService dataSourceService) {
        this.mailingList = mailingList;
        this.limit = new AtomicInteger(mailingList.getDailyLimitSentEmailsNumber());
        this.service = service;
        this.eventManager = eventManager;
        this.dataPointService = dataPointService;
        this.dataSourceService = dataSourceService;
    }

    public void initialize() {
        try {
            CronTimerTrigger activeTrigger = new CronTimerTrigger(mailingList.getCronPattern());
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
    public synchronized void scheduleTimeout(Boolean model, long fireTime) {
        DateTime dateTime = new DateTime(fireTime);
        if(mailingList.isActive(dateTime)) {
            List<ScheduledEvent> scheduledEvents = service.getScheduledEvents(mailingList);
            boolean dailyLimitSent = mailingList.isDailyLimitSentEmails();
            for (ScheduledEvent scheduledEvent : scheduledEvents) {
                if(dailyLimitSent)
                    executeWithLimit(scheduledEvent, dateTime);
                else
                    executeWithoutLimit(scheduledEvent);
            }
        }
    }

    private void executeWithLimit(ScheduledEvent scheduledEvent, DateTime dateTime) {
        if (limit.get() > 0) {
            if(raiseEvent(scheduledEvent)) {
                limit.decrementAndGet();
            }
        } else if(limit.get() == 0) {
            if(communicateLimit(scheduledEvent, dateTime)) {
                limit.decrementAndGet();
            }
        }
    }

    private void executeWithoutLimit(ScheduledEvent event) {
        raiseEvent(event);
    }

    private boolean communicateLimit(ScheduledEvent scheduledEvent, DateTime fireTime) {
        LocalizableMessage localizableMessage = new LocalizableMessage("mailingLists.dailyLimitExceeded");

        EventInstance event = scheduledEvent.getEvent();
        EventType eventType = event.getEventType();
        CommunicationChannel communicationChannel = CommunicationChannel
                .newChannel(mailingList, scheduledEvent.getEventHandler());
        ScheduledInactiveEventType type = new ScheduledInactiveEventType(eventType, communicationChannel);
        sleep();
        try {
            eventManager.raiseEvent(type, fireTime.getMillis(), false, AlarmLevels.URGENT,
                    localizableMessage, Collections.emptyMap());
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return false;
        }
        log.info("Last message sent today for a list of addresses:: " + mailingList.getId() + ", eventId: " + event.getId() + ", type: " + type);
        return true;
    }

    private boolean raiseEvent(ScheduledEvent scheduledEvent) {

        EventInstance event = scheduledEvent.getEvent();
        EventType eventType = event.getEventType();

        if (isDataSourceExists(eventType) && isDataPointExists(eventType)) {
            CommunicationChannel communicationChannel = CommunicationChannel
                    .newChannel(mailingList, scheduledEvent.getEventHandler());
            ScheduledInactiveEventType type = new ScheduledInactiveEventType(eventType, communicationChannel);
            sleep();
            try {
                eventManager.raiseEvent(type, event.getActiveTimestamp(), event.isRtnApplicable(), event.getAlarmLevel(),
                        event.getMessage(), event.getContext());
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
                return false;
            }
            service.unscheduleEvent(scheduledEvent, mailingList);
            return true;
        }
        service.unscheduleEvent(scheduledEvent, mailingList);
        return false;
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

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
    }
}
