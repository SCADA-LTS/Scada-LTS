package com.serotonin.mango.util;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.ws.model.WsEventMessage;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;

public final class NotifyEventUtils {

    private NotifyEventUtils() {}

    public static void notifyEventReset(IHighestAlarmLevelService highestAlarmLevelService, UserEventServiceWebSocket userEventService) {
        highestAlarmLevelService.doResetAlarmLevels(userEventService::sendAlarmLevel);
        ApplicationBeans.Lazy.getLoggedUsersBean().ifPresent(loggedUsers -> {
            for(User user: loggedUsers.getUsers())
                notifyEventUpdate(user, WsEventMessage.reset(), userEventService);
        });
    }

    public static void notifyEventRaise(IHighestAlarmLevelService highestAlarmLevelService, EventInstance evt, User user, UserEventServiceWebSocket userEventService) {
        if((evt.getAlarmLevel() > AlarmLevels.NONE) && evt.isActive()) {
            highestAlarmLevelService.doUpdateAlarmLevel(user, evt, userEventService::sendAlarmLevel);
        }
    }

    public static void notifyEventToggle(IHighestAlarmLevelService highestAlarmLevelService, EventInstance evt, User user, UserEventServiceWebSocket userEventService) {
        if(evt.getAlarmLevel() > AlarmLevels.NONE) {
            highestAlarmLevelService.doRemoveAlarmLevel(user, evt, userEventService::sendAlarmLevel);
            notifyEventUpdate(user, WsEventMessage.update(evt), userEventService);
        }
    }

    public static void notifyEventRtn(IHighestAlarmLevelService highestAlarmLevelService, EventInstance evt, User user, UserEventServiceWebSocket userEventService) {
        if(evt.getAlarmLevel() > AlarmLevels.NONE) {
            highestAlarmLevelService.doRemoveAlarmLevel(user, evt, userEventService::sendAlarmLevel);
            notifyEventUpdate(user, WsEventMessage.update(evt), userEventService);
        }
    }

    public static void notifyEventAck(IHighestAlarmLevelService highestAlarmLevelService, EventInstance evt, User user, UserEventServiceWebSocket userEventService) {
        if(evt.getAlarmLevel() > AlarmLevels.NONE) {
            highestAlarmLevelService.doRemoveAlarmLevel(user, evt, userEventService::sendAlarmLevel);
            notifyEventUpdate(user, WsEventMessage.delete(evt), userEventService);
        }
    }

    public static void notifyEventCreate(EventInstance evt, User user, UserEventServiceWebSocket userEventService) {
        if(evt.getAlarmLevel() > AlarmLevels.NONE) {
            notifyEventUpdate(user, WsEventMessage.create(evt), userEventService);
        }
    }

    private static void notifyEventUpdate(User user, WsEventMessage message, UserEventServiceWebSocket userEventService) {
        userEventService.sendEventUpdate(user, message);
    }
}
