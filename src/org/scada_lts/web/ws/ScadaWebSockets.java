package org.scada_lts.web.ws;

/**
 * Scada Web Sockets Interface
 *
 * Interface to be assigned to specific classes
 * that will be using the WebSocket messaging communication.
 *
 * That class which will be using WebSocket should use
 * an implementation of  "AbstractWebSocket" service
 * that will contain the methods for managing the messages.
 * @see org.scada_lts.web.ws.services.AbstractWebSocket
 *
 * @param <T> - Type of message
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.1.0
 */
public interface ScadaWebSockets<T> {

    /**
     * Notify WebSocket Subscribers
     *
     * Method that will contain the logic that
     * notify the specific WebSocket subscribers.
     * It could have different implementations.
     * For example, you can check that class:
     * @see com.serotonin.mango.rt.dataImage.DataPointRT
     *
     * @param message Message to be sent
     */
    void notifyWebSocketSubscribers(T message);

}
