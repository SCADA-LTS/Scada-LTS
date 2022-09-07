package org.scada_lts.ds.messaging.mqtt.impl;

import org.scada_lts.ds.messaging.MessagingChannel;

public class MqttChannel implements MessagingChannel<MqttVClient> {

    private final MqttVClient client;

    public MqttChannel(MqttVClient client) {
        this.client = client;
    }

    @Override
    public boolean isOpen() {
        return client.isConnected();
    }

    @Override
    public void close(int timeout) throws Exception {
        try {
            client.disconnect(timeout);
        } finally {
            client.close();
        }
    }

    @Override
    public MqttVClient toOrigin() {
        return client;
    }
}
