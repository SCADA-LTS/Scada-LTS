package org.scada_lts.ds.messaging.protocol.mqtt.impl;

import org.eclipse.paho.client.mqttv3.*;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;

import java.util.function.BiConsumer;

class MqttV3Client implements MqttVClient {

    private final MqttClient client;
    private final MqttDataSourceVO vo;

    public MqttV3Client(MqttClient client, MqttDataSourceVO vo) {
        this.client = client;
        this.vo = vo;
    }

    @Override
    public void connect() throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(vo.isAutomaticReconnect());
        options.setExecutorServiceTimeout(vo.getExecutorServiceTimeout());
        options.setMaxReconnectDelay(vo.getMaxReconnectDelay());
        options.setConnectionTimeout(vo.getConnectionTimeout());
        options.setKeepAliveInterval(vo.getKeepAliveInterval());
        options.setCleanSession(vo.isCleanSession());
        //options.setHttpsHostnameVerificationEnabled(false);

        if (!vo.getServerUsername().isBlank()) {
            options.setUserName(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isBlank()) {
            options.setPassword(vo.getServerPassword().toCharArray());
        }
        client.connect(options);
    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {
        client.publish(topic, payload, qos, retained);
    }

    @Override
    public void subscribe(String topicFilter, int qos, BiConsumer<String, MqttVMessage> messageListener) throws Exception {
        client.subscribe(topicFilter, qos, (s, mqttMessage) -> messageListener.accept(s, new MqttV3Message(mqttMessage)));
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
    public void disconnect(int timeout) throws MqttException {
        client.disconnect(timeout);
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }
}
