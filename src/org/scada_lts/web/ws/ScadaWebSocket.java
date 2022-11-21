package org.scada_lts.web.ws;

/**
 * Scada Web Socket
 *
 * Manage web socket subscription by adding or removing
 * WebSocketEndpointListeners. Send the message to the
 * recipients with simple notification method.
 *
 * @author Radoslaw Jajko
 * @param <T>
 */
public interface ScadaWebSocket<T> {

    /**
     * Add specific Scada Web Socket Listener instance
     * @param listener ScadaWebSocketListener
     */
    void addWebSocketListener(ScadaWebSocketListener listener);

    /**
     * Remove Scada Web Socket Listener instance
     * @param listener ScadaWebSocketListener
     */
    void removeWebSocketListener(ScadaWebSocketListener listener);

    /**
     * Notify Web Socket Listeners
     * Send message to your ScadaWebSocketListeners.
     * Finally message must be a String.
     * @param message - message to send
     */
    void notifyWebSocketListeners(T message);

}
