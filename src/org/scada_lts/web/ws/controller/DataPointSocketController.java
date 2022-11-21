package org.scada_lts.web.ws.controller;

import org.scada_lts.service.DataPointServiceWebSocket;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class DataPointSocketController {

    @Resource
    private DataPointServiceWebSocket dpWsService;

    @MessageMapping("/datapoint/{id}/value")
    public void subscribeDataPoint(@DestinationVariable int id) {
        dpWsService.subscribeDataPoint(id);
    }

    @MessageMapping("/datapoint/{id}/value/unsub")
    public void unSubscribeDataPoint(@DestinationVariable int id) {
        dpWsService.unSubscribeDataPoint(id);
    }

}
