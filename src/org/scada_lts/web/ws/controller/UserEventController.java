package org.scada_lts.web.ws.controller;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.ws.model.WsEventMessage;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
public class UserEventController {

    private static final Log LOG = LogFactory.getLog(UserEventController.class);

    private final UserEventServiceWebSocket userEventServiceWebSocket;
    private final MangoUser userService;

    public UserEventController(UserEventServiceWebSocket userEventServiceWebSocket) {
        this.userEventServiceWebSocket = userEventServiceWebSocket;
        this.userService = new UserService();
    }

    @MessageMapping("/event/update")
    public String process(String message, UsernamePasswordAuthenticationToken principal, StompHeaderAccessor accessor) {
        String username = principal.getName();
        User user = userService.getUser(username);
        LOG.debug("process[" + username + "]" + "message: " + message);
        userEventServiceWebSocket.sendEventUpdate(user, WsEventMessage.reset());
        return "user: " + username + ", message: " + message;
    }

    @SubscribeMapping("/event/update/register")
    public String register(UsernamePasswordAuthenticationToken principal) {
        User user = userService.getUser(principal.getName());
        LOG.debug("register: " + user + "["+user.getId()+"]");
        return user.getUsername();
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        LOG.warn("Exception caught: " + exception.getMessage());
        return exception.getMessage();
    }
}
