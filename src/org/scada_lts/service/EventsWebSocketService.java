package org.scada_lts.service;

import com.serotonin.mango.Common;
import org.scada_lts.web.ws.WebSocketEndpointListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventsWebSocketService implements WebSocketEndpointListener {

    private static EventsWebSocketService instance = null;
    private SimpMessagingTemplate template;

    @Autowired
    public EventsWebSocketService() {
        instance = this;
        Common.ctx.getEventManager().addWsEndpointObserver(this);
    }

    @Autowired
    public void setTemplate(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void sendWebSocketMessage(String message) {
        template.convertAndSend("/topic/alarm", message);
    }

    @Override
    public void sendWebSocketRefMessage(int referenceId, String message) {

    }

    public static EventsWebSocketService getInstance() {
        return instance;
    }

}
