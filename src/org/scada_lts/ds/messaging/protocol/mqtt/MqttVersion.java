package org.scada_lts.ds.messaging.protocol.mqtt;

import org.scada_lts.ds.messaging.protocol.mqtt.impl.MqttVClient;
import org.scada_lts.ds.messaging.protocol.mqtt.impl.MqttClientFactory;
import org.scada_lts.ds.messaging.protocol.ProtocolVersion;

public enum MqttVersion implements ProtocolVersion {
    V3_1_1_MQTT("v3.1.1") {
        @Override
        public MqttVClient newClient(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception {
            return MqttClientFactory.v3(vo, locator);
        }
    },
    V5_0_MQTT("v5.0") {
        @Override
        public MqttVClient newClient(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception {
            return MqttClientFactory.v5(vo, locator);
        }
    };

    private final String version;

    MqttVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return this.name();
    }

    public abstract MqttVClient newClient(MqttDataSourceVO vo, MqttPointLocatorVO locator) throws Exception;
}
