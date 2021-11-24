package org.scada_lts.web.ws.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserEventController {

    private static final Log LOG = LogFactory.getLog(UserEventController.class);

    @SubscribeMapping("/event/update/")
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
