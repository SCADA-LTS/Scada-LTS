package org.scada_lts.web.ws.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Events WebSocket Service
 *
 * WebSocket service for sending a WebSocket messages to subscribers.
 * It is a concrete implementation of AbstractWebSocket.
 * This class will be initialized during @Autowired process.
 *
 * @author Radoslaw Jajko
 * @version 1.1.0
 */
@Service
public class EventsServiceWebSocket extends AbstractWebSocket {

    private static EventsServiceWebSocket instance = null;
    private static final String WS_DESTINATION = "/alarm";

    @Autowired
    public EventsServiceWebSocket() {
        instance = this;
    }

    public void notifyEventsSubscribers(String message) {
        sendWebSocketMessage(WS_DESTINATION, message);
    }

    public static EventsServiceWebSocket getInstance() {
        return instance;
    }

}
