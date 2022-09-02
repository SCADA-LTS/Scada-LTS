package org.scada_lts.ds.messaging.mqtt;

import org.scada_lts.ds.messaging.mqtt.impl.MqttVClient;
import org.scada_lts.ds.messaging.mqtt.impl.MqttClientFactory;

public enum MqttVersion {
    V3_1_1 {
        @Override
        public MqttVClient newClient(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception {
            return MqttClientFactory.v3(vo, locator);
        }
    },
    V5_0 {
        @Override
        public MqttVClient newClient(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception {
            return MqttClientFactory.v5(vo, locator);
        }
    };

    public abstract MqttVClient newClient(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception;
}
