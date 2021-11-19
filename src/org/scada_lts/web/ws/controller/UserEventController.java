package org.scada_lts.web.ws.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserEventController {

    private static final Log LOG = LogFactory.getLog(UserEventController.class);

    @MessageMapping("/event")
    public String event(ScadaPrincipal principal) {
        String user = principal.getName();
        LOG.debug("register: " + user + "["+principal.getId()+"]");
        return user;
    }

    @SubscribeMapping("/event/update/register")
    public String register(ScadaPrincipal principal) {
        String user = principal.getName();
        LOG.debug("register: " + user + "["+principal.getId()+"]");
        return user;
    }
}
