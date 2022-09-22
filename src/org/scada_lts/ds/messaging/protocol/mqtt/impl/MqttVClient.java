package org.scada_lts.ds.messaging.protocol.mqtt.impl;

import java.util.function.BiConsumer;

public interface MqttVClient {
    void connect() throws Exception;
    boolean isConnected();
    void disconnect(int timeout) throws Exception;
    void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception;
    void subscribe(String topicFilter, int qos, BiConsumer<String, MqttVMessage> messageListener) throws Exception;
    void unsubscribe(String topicFilter) throws Exception;
    void close() throws Exception;
}
