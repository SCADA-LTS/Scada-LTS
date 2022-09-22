package org.scada_lts.ds.messaging.protocol;


import org.scada_lts.ds.messaging.protocol.amqp.AmqpVersion;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttVersion;

import java.io.Serializable;
import java.util.stream.Stream;

public interface ProtocolVersion extends Serializable {
    String getVersion();
    String getName();
    default String abc() {
        return "";
    }

    static ProtocolVersion protocolVersion(String value) {
        return Stream.concat(Stream.of(AmqpVersion.values()), Stream.of(MqttVersion.values()))
                .map(ProtocolVersion.class::cast)
                .filter(a -> a.getName().equalsIgnoreCase(value))
                .findFirst()
                .orElse(new ProtocolVersion() {
                    @Override
                    public String getVersion() {
                        return "unknown";
                    }

                    @Override
                    public String getName() {
                        return "unknown";
                    }
                });
    }
}
