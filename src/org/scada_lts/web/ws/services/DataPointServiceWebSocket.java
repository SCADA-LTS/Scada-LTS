package org.scada_lts.web.ws.services;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Point WebSocket Service
 *
 * WebSocket service for sending a WebSocket messages to subscribers.
 * It is a concrete implementation of AbstractWebSocket.
 * This class will be initialized during @Autowired process.
 *
 * @author Radoslaw Jajko
 * @version 1.1.0
 */
@Service
public class DataPointServiceWebSocket extends AbstractWebSocket {

    public DataPointServiceWebSocket(SimpMessagingTemplate template) {
        super(template);
    }

    /**
     * Notify Subscribers about PointValue change
     *
     * Send a message to specific channel regard to
     * the provided pointId value. Message will contain the
     * point value and the id od that point.
     *
     * @param pointValue Mango Point Value
     * @param pointId ID number of point destination
     */
    public void notifyValueSubscribers(MangoValue pointValue, int pointId) {
        Map<String, Object> response = new HashMap<>();
        response.put("pointId", pointId);
        response.put("value", pointValue.toString());
        String url = "/datapoint/" + pointId + "/value";
        sendWebSocketMessage(url, response);
    }

    /**
     * Notify Subscribers about PointValue change
     *
     * Send a message to specific channel regard to
     * the provided pointId value. Message will contain the
     * point value and the id od that point.
     *
     * @param pointValue Mango Point Value
     * @param pointXid XID identifier of point destination
     */
    public void notifyValueSubscribers(MangoValue pointValue, String pointXid) {
        Map<String, Object> response = new HashMap<>();
        response.put("pointXid", pointXid);
        response.put("value", pointValue.toString());
        String url = "/datapoint/" + pointXid + "/value";
        sendWebSocketMessage(url, response);
    }

    public void notifyStateSubscribers(boolean enabled, int pointId) {
        Map<String, Object> response = new HashMap<>();
        response.put("pointId", pointId);
        response.put("enabled", enabled);
        String url = "/datapoint/" + pointId + "/enabled";
        sendWebSocketMessage(url, response);
    }

    public void notifyStateSubscribers(boolean enabled, String pointXid) {
        Map<String, Object> response = new HashMap<>();
        response.put("pointXid", pointXid);
        response.put("enabled", enabled);
        String url = "/datapoint/" + pointXid + "/enabled";
        sendWebSocketMessage(url, response);
    }

}
