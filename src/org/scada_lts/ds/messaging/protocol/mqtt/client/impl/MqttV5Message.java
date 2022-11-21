package org.scada_lts.ds.messaging.protocol.mqtt.client.impl;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.scada_lts.ds.messaging.protocol.mqtt.client.MqttVMessage;

class MqttV5Message implements MqttVMessage {

    private final MqttMessage message;

    public MqttV5Message(MqttMessage message) {
        this.message = message;
    }

    @Override
    public byte[] getPayload() {
        return message.getPayload();
    }

    public void clearPayload() {
        message.clearPayload();
    }

    public void setPayload(byte[] payload) {
        message.setPayload(payload);
    }

    @Override
    public boolean isRetained() {
        return message.isRetained();
    }

    public void setRetained(boolean retained) {
        message.setRetained(retained);
    }

    @Override
    public int getQos() {
        return message.getQos();
    }

    public void setQos(int qos) {
        message.setQos(qos);
    }

    @Override
    public boolean isDuplicate() {
        return message.isDuplicate();
    }

    public void setId(int messageId) {
        message.setId(messageId);
    }

    @Override
    public int getMessageId() {
        return message.getId();
    }

    @Override
    public MqttMessage origin() {
        return message;
    }
}
