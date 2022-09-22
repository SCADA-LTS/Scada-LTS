package org.scada_lts.ds.messaging.protocol.mqtt.impl;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

class MqttV5Client implements MqttVClient {

    private final MqttClient client;
    private final MqttDataSourceVO vo;

    public MqttV5Client(MqttClient client, MqttDataSourceVO vo) {
        this.client = client;
        this.vo = vo;
    }

    @Override
    public void connect() throws Exception {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(vo.isAutomaticReconnect());
        options.setExecutorServiceTimeout(vo.getExecutorServiceTimeout());
        options.setMaxReconnectDelay(vo.getMaxReconnectDelay());
        options.setConnectionTimeout(vo.getConnectionTimeout());
        options.setKeepAliveInterval(vo.getKeepAliveInterval());
        options.setCleanStart(vo.isCleanSession());
        //options.setHttpsHostnameVerificationEnabled(false);

        if (!vo.getServerUsername().isBlank()) {
            options.setUserName(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isBlank()) {
            options.setPassword(vo.getServerPassword().getBytes(StandardCharsets.UTF_8));
        }
        client.connect(options);
    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {
        client.publish(topic, payload, qos, retained);
    }

    @Override
    public void subscribe(String topicFilter, int qos, BiConsumer<String, MqttVMessage> messageListener) throws Exception {
        MqttSubscription mqttSubscription = new MqttSubscription(topicFilter, qos);
        client.subscribe(new MqttSubscription[]{mqttSubscription}, new IMqttMessageListener[] {(s, mqttMessage) -> messageListener.accept(s, new MqttV5Message(mqttMessage))});
    }

    @Override
    public void unsubscribe(String topicFilter) throws Exception {
        client.unsubscribe(topicFilter);
    }

    @Override
    public void close() throws Exception {
        client.close(true);
    }

    @Override
    public void disconnect(int timeout) throws Exception {
        client.disconnect(timeout);
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }
}
