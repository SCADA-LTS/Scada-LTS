package org.scada_lts.web.ws.controller;


import java.util.Map;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.web.ws.config.WebSocketStatsMonitor;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;


@Controller
public class AlarmLevelController {
    private static final Log LOG = LogFactory.getLog(AlarmLevelController.class);

    private final IHighestAlarmLevelService highestAlarmLevelService;
    private final UserEventServiceWebSocket userEventService;
    private final WebSocketStatsMonitor webSocketStatsMonitor;
    private final MangoUser userService;

    public AlarmLevelController(IHighestAlarmLevelService highestAlarmLevelService,
                                UserEventServiceWebSocket userEventService,
                                WebSocketStatsMonitor webSocketStatsMonitor) {
        this.highestAlarmLevelService = highestAlarmLevelService;
        this.userEventService = userEventService;
        this.webSocketStatsMonitor = webSocketStatsMonitor;
        this.userService = new UserService();
    }

    @MessageMapping("/alarmLevel")
    public String process(String message, UsernamePasswordAuthenticationToken principal, StompHeaderAccessor accessor) {
        LOG.debug("process[" + principal.getName() + "]" + "message: " + message);
        highestAlarmLevelService.doSendAlarmLevel(userService.getUser(principal.getName()), userEventService::sendAlarmLevel);
        return "user: " + principal.getName()  + ", message: " + message;
    }

    @SubscribeMapping("/alarmLevel/register")
    public String register(UsernamePasswordAuthenticationToken principal) {
        User user = userService.getUser(principal.getName());
        LOG.debug("register: " + user + "["+user.getId()+"]");
        return user.getUsername();
    }

    @SubscribeMapping("/listusers")
    public String listUsers(UsernamePasswordAuthenticationToken principal) {
        LOG.debug("listUsers: " + principal.getName());
        String result = webSocketStatsMonitor.toPrintUsers();
        LOG.debug(result);
        return result;
    }
    
    @SubscribeMapping("/session")
    public String listSessionAttributes(UsernamePasswordAuthenticationToken principal, StompHeaderAccessor accessor) {
        LOG.debug("listSessionAttributes: " + principal.getName());
        
        StringBuilder result = new StringBuilder();
        Map<String, Object> attributes = accessor.getSessionAttributes();
        for( String key : attributes.keySet()) {
            result.append(key).append(": ").append(attributes.get(key).toString()).append("\n");
        }
        return result.toString();
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        LOG.warn("Exception caught: " + exception.getMessage());
        return exception.getMessage();
    }

    @SubscribeMapping("/websocketStats")
    public String processWebsocketStats(UsernamePasswordAuthenticationToken principal, StompHeaderAccessor accessor) {
        LOG.debug("processWebsocketStats: " + principal);
        String result = webSocketStatsMonitor.toPrintStats();
        LOG.debug(result);
        return result;
    }

}

