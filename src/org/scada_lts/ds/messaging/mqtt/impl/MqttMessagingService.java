package org.scada_lts.ds.messaging.mqtt.impl;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.MessagingService;
import org.scada_lts.ds.messaging.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.mqtt.MqttPointLocatorRT;
import org.scada_lts.ds.messaging.mqtt.MqttPointLocatorVO;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MqttMessagingService implements MessagingService {

    private static final Log LOG = LogFactory.getLog(MqttMessagingService.class);
    public static final String ATTR_UPDATE_ERROR_KEY = "DP_UPDATE_ERROR";

    private final MqttDataSourceVO vo;
    private final Map<Integer, MqttVClient> clients;
    private volatile boolean blocked;

    public MqttMessagingService(MqttDataSourceVO vo) {
        this.vo = vo;
        this.clients = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isOpen() {
        if(clients.isEmpty())
            return false;
        return clients.entrySet().stream()
                .anyMatch(a -> a.getValue().isConnected());
    }

    @Override
    public void open() throws Exception {}

    @Override
    public void close() throws Exception {
        blocked = true;
        for(MqttVClient client: clients.values()) {
            close(client);
        }
        clients.clear();
        blocked = false;
    }

    @Override
    public void initReceiver(DataPointRT dataPoint) throws Exception {
        if(blocked) {
            LOG.warn("Stop init Receiver: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", MQTT Service of shutting down: "  + LoggingUtils.dataSourceInfo(vo));
            return;
        }
        clients.computeIfAbsent(dataPoint.getId(), a -> createClient(dataPoint));
    }

    @Override
    public void removeReceiver(DataPointRT dataPoint) throws Exception {
        if(blocked) {
            LOG.warn("Stop remove Receiver: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", MQTT Service of shutting down: "  + LoggingUtils.dataSourceInfo(vo));
            return;
        }
        MqttVClient client = clients.get(dataPoint.getId());
        if(client != null) {
            MqttPointLocatorRT mqttV3PointLocatorRT = dataPoint.getPointLocator();
            MqttPointLocatorVO locator = mqttV3PointLocatorRT.getVO();
            try {
                client.unsubscribe(locator.getTopicFilter());
            } catch (Exception ex){
                LOG.warn(ex.getMessage(), ex);
            }
            try {
                close(client);
            } finally {
                clients.remove(dataPoint.getId());
            }
        } else {
            LOG.warn("Client is closed. Data Source: " + LoggingUtils.dataSourceInfo(vo) + ", Data Point: " + LoggingUtils.dataPointInfo(dataPoint.getVO()));
        }
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws Exception {
        if(blocked) {
            throw new IllegalStateException("Stop publish: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", MQTT Service of shutting down:  "  + LoggingUtils.dataSourceInfo(vo) + ", message: " + message);
        }
        MqttVClient client = clients.get(dataPoint.getId());
        if(client != null) {
            MqttPointLocatorRT mqttV3PointLocatorRT = dataPoint.getPointLocator();
            MqttPointLocatorVO locator = mqttV3PointLocatorRT.getVO();
            client.publish(locator.getTopicFilter(), message.getBytes(StandardCharsets.UTF_8),
                    locator.getQos(), locator.isRetained());
        } else {
            throw new IllegalStateException("Error publish: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", MQTT Service of shutting down: "  + LoggingUtils.dataSourceInfo(vo) + ", message: " + message);
        }
    }

    private MqttVClient createClient(DataPointRT dataPoint) throws RuntimeException {
        MqttPointLocatorRT pointLocator = dataPoint.getPointLocator();
        MqttPointLocatorVO locator = pointLocator.getVO();
        MqttVClient client;
        try {
            client = vo.getProtocolVersion().newClient(vo, locator);
        } catch (Exception e) {
            throw new RuntimeException("Error Create MQTT Client: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo) + ", Message: " + e.getMessage(), e);
        }
        try {
            client.connect();
        } catch (Exception e) {
            throw new RuntimeException("Error Connect MQTT Client: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo) + ", Message: " + e.getMessage(), e);
        }
        try {
            client.subscribe(locator.getTopicFilter(), locator.getQos(), (topic, mqttMessage) ->
                    new UpdatePointValueConsumer(dataPoint, locator::isWritable, ATTR_UPDATE_ERROR_KEY)
                            .accept(mqttMessage.getPayload()));
        } catch (Exception e) {
            try {
                close(client);
            } catch (Exception ex) {
                LOG.warn("Error Close MQTT Client: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo) + ", Message: " + ex.getMessage(), ex);
            }
            throw new RuntimeException("Error Subscribe MQTT Client: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo) + ", Message: " + e.getMessage(), e);
        }
        if(!client.isConnected())
            throw new RuntimeException("Error Connect MQTT Client: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo));
        return client;
    }

    private void close(MqttVClient client) throws Exception {
        try {
            client.disconnect(vo.getConnectionTimeout());
        } finally {
            client.close();
        }
    }
}
