package org.scada_lts.web.ws;

public interface WebSocketEndpointListener {

    public void sendWebSocketMessage(String message);

    public void sendWebSocketRefMessage(int referenceId, String message);

}
