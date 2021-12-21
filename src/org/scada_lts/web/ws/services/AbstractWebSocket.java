package org.scada_lts.web.ws.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Abstract WebSocket
 *
 * Abstract class for WebSocket services.
 * Extend that class and implement domain-specific methods
 * to send messages and notify subscribers.
 *
 * Child class must be a type of singleton-pattern
 * that is initialized only once.
 *
 * @author Radoslaw Jajko
 * @version 1.1.0
 */
public abstract class AbstractWebSocket {

    private SimpMessagingTemplate template;

    @Autowired
    AbstractWebSocket() { }

    /**
     * Initialization of WebSocket broker
     *
     * Initialized by Spring during the application initialization.
     * @param template - Messaging template
     */
    @Autowired
    public void setTemplate(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * Send Message by WebSocket
     *
     * Send any type of the message to subscribers on a specific
     * "endpointUrl". Every message is sent with prefix 'topic'
     * to inform developers that the server is the initiator
     * of the request. Client that subscribe on "topic" channel
     * may suppose that some data will be transferred.
     *
     * @param endpointUrl - channel URL to be notified
     * @param message - Message Body object.
     */
    public void sendWebSocketMessage(String endpointUrl, Object message) {
        endpointUrl = "/topic" + endpointUrl;
        template.convertAndSend(endpointUrl, message);
    }

}
