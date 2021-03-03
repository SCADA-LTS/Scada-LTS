package org.scada_lts.web.ws.controller;

import org.scada_lts.service.DataPointWebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class DataPointSocketController {

    @Resource
    private DataPointWebSocketService dpWsService;

    @MessageMapping("/datapoint/{id}/value")
    public void subscribeDataPoint(@DestinationVariable int id) {
        dpWsService.subscribeDataPoint(id);
    }

    @MessageMapping("/datapoint/{id}/value/unsub")
    public void unSubscribeDataPoint(@DestinationVariable int id) {
        dpWsService.unSubscribeDataPoint(id);
    }

}
