package org.scada_lts.ds.messaging.mqtt.impl;

public interface MqttVMessage {
    byte[] getPayload();
    boolean isRetained();
    int getQos();
    boolean isDuplicate();
    int getMessageId();
    Object origin();
}
