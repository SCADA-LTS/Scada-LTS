package org.scada_lts.service;

import com.serotonin.mango.Common;
import org.scada_lts.web.ws.ScadaWebSocketListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventsServiceWebSocket implements ScadaWebSocketListener<String, Object> {

    private static EventsServiceWebSocket instance = null;
    private SimpMessagingTemplate template;
    private static final String WS_DESTINATION = "/topic/alarm";

    @Autowired
    public EventsServiceWebSocket() {
        instance = this;
        Common.ctx.getEventManager().addWebSocketListener(this);
    }

    @Autowired
    @Override
    public void setTemplate(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void sendWebSocketMessage(String message, Object... reference) {
        template.convertAndSend(WS_DESTINATION, message);
    }

    public static EventsServiceWebSocket getInstance() {
        return instance;
    }

}
