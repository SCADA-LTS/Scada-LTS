package org.scada_lts.service;

import com.serotonin.mango.Common;
import org.scada_lts.web.ws.WebSocketEndpointListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataPointWebSocketService implements WebSocketEndpointListener {

    private static DataPointWebSocketService instance = null;
    private SimpMessagingTemplate template;

    @Autowired
    public DataPointWebSocketService() {
        instance = this;
    }

    @Autowired
    public void setTemplate(SimpMessagingTemplate template) {
        this.template = template;
    }


    public void subscribeDataPoint(int datapointId) {
        Common.ctx.getRuntimeManager().getDataPoint(datapointId).addWsEndpointObserver(this);
    }

    public void unSubscribeDataPoint(int datapointId) {
        Common.ctx.getRuntimeManager().getDataPoint(datapointId).removeWsEndpointObserver(this);
    }

    @Override
    public void sendWebSocketMessage(String message) {

    }

    @Override
    public void sendWebSocketRefMessage(int referenceId, String message) {
        String destination = "/topic/datapoint/" + referenceId + "/value";
        template.convertAndSend(destination, message);
    }

    public static DataPointWebSocketService getInstance() {
        return instance;
    }
}
