package com.serotonin.mango.rt.event.schedule;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import org.springframework.dao.EmptyResultDataAccessException;


import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledExecuteInactiveEventRT implements ModelTimeoutClient<Boolean> {

    private final Log log = LogFactory.getLog(ScheduledExecuteInactiveEventRT.class);

    private TimerTask task;
    private final CommunicationChannel communicationChannel;
    private final ScheduledExecuteInactiveEventService service;
    private final EventManager eventManager;
    private final AtomicInteger limit;

    public ScheduledExecuteInactiveEventRT(CommunicationChannel communicationChannel,
                                           ScheduledExecuteInactiveEventService service,
                                           EventManager eventManager) {
        this.communicationChannel = communicationChannel;
        this.limit = new AtomicInteger(communicationChannel.getDailyLimitSentNumber());
        this.service = service;
        this.eventManager = eventManager;
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
        if(communicationChannel.isActiveFor(dateTime)) {
            List<EventInstance> events = service.getScheduledEvents(communicationChannel);
            boolean dailyLimitSent = communicationChannel.isDailyLimitSent();
            for (EventInstance event : events) {
                if(dailyLimitSent)
                    executeWithLimit(event, dateTime);
                else
                    executeWithoutLimit(event);
            }
        }
    }

    private void executeWithLimit(EventInstance event, DateTime dateTime) {
        if ((limit.get() > 0 && raiseEvent(event))
                || (limit.get() == 0 && communicateLimit(event, dateTime))) {
            limit.decrementAndGet();
        }
    }

    private void executeWithoutLimit(EventInstance event) {
        raiseEvent(event);
    }

    private boolean communicateLimit(EventInstance event, DateTime fireTime) {
        LocalizableMessage localizableMessage = new LocalizableMessage("mailingLists.dailyLimitExceeded");

        EventType eventType = event.getEventType();
        ScheduledInactiveEventType type = new ScheduledInactiveEventType(eventType, communicationChannel);
        sleep();
        try {
            eventManager.raiseEvent(type, fireTime.getMillis(), false, AlarmLevels.URGENT,
                    localizableMessage, Collections.emptyMap());
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return false;
        }
        log.info("Last message sent today for a list of addresses:: " + communicationChannel.getChannelId() + ", eventId: " + event.getId() + ", type: " + type);
        return true;
    }

    private boolean raiseEvent(EventInstance event) {

        EventType eventType = event.getEventType();

        if (isDataPointExists(eventType)) {
            ScheduledInactiveEventType type = new ScheduledInactiveEventType(eventType, communicationChannel);
            sleep();
            try {
                eventManager.raiseEvent(type, event.getActiveTimestamp(), event.isRtnApplicable(), event.getAlarmLevel(),
                        event.getMessage(), event.getContext());
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
                return false;
            }
            service.unscheduleEvent(communicationChannel, event);
            return true;
        }
        service.unscheduleEvent(communicationChannel, event);
        return false;
    }

    private boolean isDataPointExists(EventType eventType) {
        try {
            eventType.getDataSourceId();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
    }
}
