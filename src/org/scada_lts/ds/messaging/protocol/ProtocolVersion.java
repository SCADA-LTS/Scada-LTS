package org.scada_lts.ds.messaging.protocol;


import org.scada_lts.ds.messaging.protocol.amqp.AmqpVersion;

import java.io.Serializable;
import java.util.stream.Stream;

public interface ProtocolVersion extends Serializable {
    String getVersion();
    String getName();

    static ProtocolVersion protocolVersion(String value) {
        return Stream.of(AmqpVersion.values())
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
