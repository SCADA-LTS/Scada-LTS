package org.scada_lts.web.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

/**
 * Web Socket Scada Listener Interface
 *
 * Send messages to destination hosts using
 * the SimpMessagingTemplate object.
 * Implement that method in services that will
 * use the WebSockets to send data to the WebApplicaiton
 *
 * @param <T> Type of the message to send
 * @param <R> Type of endpoint to identify
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
public interface ScadaWebSocketListener<T, R> {

    /**
     * Set SimpMessagingTemplate
     *
     * With Spring use the @Autowired annotation to bind this template
     * @param template - template to be used.
     */
    public void setTemplate(SimpMessagingTemplate template);

    /**
     * Send message through Web Socket
     *
     * Message can be an any object but in most cases it is converted into
     * String data form. Convert method can be implemented inside specific class.
     *
     * To send this message there is also a possibility  to specify the destination
     * location. To do that use the optional reference parameters.
     *
     * @param reference WebSocket endpoint refs to send message
     * @param message Simple message to parse.
     */
    public void sendWebSocketMessage(T message, R... reference);

}
