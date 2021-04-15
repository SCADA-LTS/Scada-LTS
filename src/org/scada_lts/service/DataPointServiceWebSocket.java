package org.scada_lts.service;

import com.serotonin.mango.Common;
import org.scada_lts.web.ws.ScadaWebSocketListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataPointServiceWebSocket implements ScadaWebSocketListener<String, Integer> {

    private static DataPointServiceWebSocket instance = null;
    private SimpMessagingTemplate template;

    @Autowired
    public DataPointServiceWebSocket() {
        instance = this;
    }

    public void subscribeDataPoint(int datapointId) {
        Common.ctx.getRuntimeManager().getDataPoint(datapointId).addWebSocketListener(this);
    }

    public void unSubscribeDataPoint(int datapointId) {
        Common.ctx.getRuntimeManager().getDataPoint(datapointId).removeWebSocketListener(this);
    }

    @Autowired
    @Override
    public void setTemplate(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void sendWebSocketMessage(String message, Integer... reference) {
        if(reference.length > 0) {
            String dest = "/topic/datapoint/" + reference[0] + "/value";
            template.convertAndSend(dest, message);
        }
    }

    public static DataPointServiceWebSocket getInstance() {
        return instance;
    }
}
