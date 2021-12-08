package org.scada_lts.web.ws.controller;


import java.util.Map;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.config.WebSocketMessageBrokerStatsMonitor;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;


@Controller
public class AlarmLevelController {
    private static final Log LOG = LogFactory.getLog(AlarmLevelController.class);

    private final SimpUserRegistry  userRegistry;
    private final IHighestAlarmLevelService highestAlarmLevelService;
    private final UserEventServiceWebSocket userEventService;
    private final WebSocketMessageBrokerStatsMonitor statsMonitor;
    private final MangoUser userService;

    public AlarmLevelController(SimpUserRegistry userRegistry,
                                IHighestAlarmLevelService highestAlarmLevelService,
                                UserEventServiceWebSocket userEventService,
                                WebSocketMessageBrokerStatsMonitor statsMonitor) {
        this.userRegistry = userRegistry;
        this.highestAlarmLevelService = highestAlarmLevelService;
        this.userEventService = userEventService;
        this.statsMonitor = statsMonitor;
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
        
        StringBuilder result = new StringBuilder();
        for( SimpUser u : userRegistry.getUsers() ) {
            result.append(u.getName()).append("\n");
            if( u.hasSessions() ) {
                for( SimpSession s : u.getSessions() ) {
                    result.append("\t").append(s.getId()).append("\n");
                    for( SimpSubscription sub : s.getSubscriptions()) {
                        result.append("\t\t").append(sub.getId()).append(": ").append(sub.getDestination()).append("\n");
                    }
                }
            }
        }
        return result.toString();
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
    /*
    @SubscribeMapping("/websocketStats")
    public String processWebsocketStats(UsernamePasswordAuthenticationToken principal, StompHeaderAccessor accessor) {
        String user = getName(principal);
        LOG.debug("processWebsocketStats: " + user);

        String result = "Websocket Stats: \n";
        result += handler.getStatsInfo();
        result += "\tCurrent sessions: " + statsMonitor.getCurrentSessions() + "\n";
        result += "\tSendBufferSize: " + statsMonitor.getSendBufferSize() + "\n";
        result += "\tOutboundPoolSize: " + statsMonitor.getOutboundPoolSize() + "\n";
        result += "\tOutboundPoolSize: " + statsMonitor.getOutboundPoolSize() + "\n";
        result += "\tOutboundLargestPoolSize: " + statsMonitor.getOutboundLargestPoolSize() + "\n";
        result += "\tOutboundActiveThreads: " + statsMonitor.getOutboundActiveThreads() + "\n";
        result += "\tOutboundQueuedTaskCount: " + statsMonitor.getOutboundQueuedTaskCount() + "\n";
        result += "\tOutboundCompletedTaskCount: " + statsMonitor.getOutboundCompletedTaskCount() + "\n";
        LOG.debug(result);
        return result;
    }*/

    private static String getName(ScadaPrincipal principal) {
        return principal == null || principal.getName() == null ? "<unknwn>" : principal.getName();
    }
   
}

