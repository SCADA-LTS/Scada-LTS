package org.scada_lts.web.ws.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.model.WsEventMessage;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class UserEventController {

    private static final Log LOG = LogFactory.getLog(UserEventController.class);

    private final UserEventServiceWebSocket userEventServiceWebSocket;

    public UserEventController(UserEventServiceWebSocket userEventServiceWebSocket) {
        this.userEventServiceWebSocket = userEventServiceWebSocket;
    }

    @MessageMapping("/event/update")
    public String process(String message, ScadaPrincipal principal, StompHeaderAccessor accessor) {
        String user = principal.getName();
        LOG.debug("process[" + user + "]" + "message: " + message);
        userEventServiceWebSocket.sendEventUpdate(principal.getUser(), WsEventMessage.reset());
        return "user: " + user + ", message: " + message;
    }

    @SubscribeMapping("/event/update/register")
    public String register(ScadaPrincipal principal) {
        String user = principal.getName();
        LOG.debug("register: " + user + "["+principal.getId()+"]");
        return user;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        LOG.warn("Exception caught: " + exception.getMessage());
        return exception.getMessage();
    }
}
