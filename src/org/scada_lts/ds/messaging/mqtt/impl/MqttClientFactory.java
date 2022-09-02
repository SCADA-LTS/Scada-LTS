package org.scada_lts.ds.messaging.mqtt.impl;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.scada_lts.ds.messaging.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.mqtt.MqttPointLocatorVO;

public final class MqttClientFactory {

    private MqttClientFactory() {}

    public static MqttVClient v3(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception {
        return new MqttV3Client(new MqttClient("tcp://" + vo.getServerHost() + ":" + vo.getServerPortNumber(), locator.getClientId()), vo);
    }

    public static MqttVClient v5(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception {
        return new MqttV5Client(new org.eclipse.paho.mqttv5.client.MqttClient("tcp://" + vo.getServerHost() + ":" + vo.getServerPortNumber(), locator.getClientId()), vo);
    }
}
