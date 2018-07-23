package org.scada_lts.web.ws.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.service.UserHighestAlarmLevelService;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.config.WebSocketConfig;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;


@Controller
public class AlarmLevelController {
    private static final Log LOG = LogFactory.getLog(AlarmLevelController.class);     

    @Resource
    private SimpUserRegistry  userRegistry;
    
    @Resource
    private UserHighestAlarmLevelService userHighestAlarmLevelService;
    
    //@Resource
    //private WebSocketMessageBrokerStatsMonitor statsMonitor;
    
    @Resource 
    private WebSocketConfig  config;
    
    
    public void setUserHighestAlarmLevelService(UserHighestAlarmLevelService userHighestAlarmLevelService) {
    	this.userHighestAlarmLevelService = userHighestAlarmLevelService;
    }

    public void setWebSocketConfig(WebSocketConfig config) {
    	this.config = config;
    }
    
    @MessageMapping("/alarmLevel")
    @SendTo("/topic/alarmLevel")
    public String process(String message, ScadaPrincipal principal, StompHeaderAccessor accessor) throws Exception {
        String user = principal.getName() == null ? "<unknwn>" : principal.getName();
        LOG.info("process[" + user + "]" + "message: " + message);
        if( principal != null )
        	userHighestAlarmLevelService.update(principal.getId());
        return "Hello, " + user + "! Your msg was: " + message;
    }
    
    
    @SubscribeMapping("/alarmLevel/register")
    public String register(ScadaPrincipal principal) {
        String user = principal.getName();
        LOG.info("register: " + user + "["+principal.getId()+"]");
        userHighestAlarmLevelService.update(principal.getId());
        return user;
    }

    
    @SubscribeMapping("/listusers")
    //@MessageMapping("/listusers")
    //@SendTo("/ws/listusers")
    public String listUsers(ScadaPrincipal principal) {
        String user = principal.getName() == null ? "<unknwn>" : principal.getName();
        LOG.info("listUsers: " + user);
        
        String result = "";
        for( SimpUser u : userRegistry.getUsers() ) {
            result += u.getName() + "\n";
            if( u.hasSessions() ) {
                for( SimpSession s : u.getSessions() ) {
                    result += "\t" + s.getId() + "\n";
                    for( SimpSubscription sub : s.getSubscriptions()) {
                        result += "\t\t" +  sub.getId() + ": " + sub.getDestination() + "\n";
                    }
                }
            }
        }
        return result;
    }
    
    
    @SubscribeMapping("/session")
    public String listSessionAttributes(ScadaPrincipal principal, StompHeaderAccessor accessor) {
        String user = principal.getName() == null ? "<unknwn>" : principal.getName();
        LOG.info("listSessionAttributes: " + user);
        
        String result = "";
        Map<String, Object> attributes = accessor.getSessionAttributes();
        for( String key : attributes.keySet()) {
            result += key + ": " + attributes.get(key).toString() +"\n";
        }
        return result;
    }
    
    
    //@MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        LOG.warn("Exception caught: " + exception.getMessage());
        return exception.getMessage();
    }
    
    @MessageMapping("/websocketStats")
    public String processWebsocketStats(ScadaPrincipal principal, StompHeaderAccessor accessor) {
        String user = principal.getName() == null ? "<unknwn>" : principal.getName();
        LOG.info("processWebsocketStats: " + user);
        
        SubProtocolWebSocketHandler handler = (SubProtocolWebSocketHandler) config.subProtocolWebSocketHandler();
        String result = "Websocket Stats: \n";
        result += handler.getStatsInfo();
//        result += "\tCurrent sessions: " + statsMonitor.getCurrentSessions() + "\n";
//        result += "\tSendBufferSize: " + statsMonitor.getSendBufferSize() + "\n";
//        result += "\tOutboundPoolSize: " + statsMonitor.getOutboundPoolSize() + "\n";
//        result += "\tOutboundPoolSize: " + statsMonitor.getOutboundPoolSize() + "\n";
//        result += "\tOutboundLargestPoolSize: " + statsMonitor.getOutboundLargestPoolSize() + "\n";
//        result += "\tOutboundActiveThreads: " + statsMonitor.getOutboundActiveThreads() + "\n";
//        result += "\tOutboundQueuedTaskCount: " + statsMonitor.getOutboundQueuedTaskCount() + "\n";
//        result += "\tOutboundCompletedTaskCount: " + statsMonitor.getOutboundCompletedTaskCount() + "\n";
        LOG.debug(result);
        return result;
    }
   
}

