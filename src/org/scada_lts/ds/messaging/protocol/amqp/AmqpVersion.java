package org.scada_lts.ds.messaging.protocol.amqp;

import org.scada_lts.ds.messaging.protocol.ProtocolVersion;

public enum AmqpVersion implements ProtocolVersion {
    V0_9_1_EXT_AMQP("v0.9.1 (incl. ext)");

    private final String version;

    AmqpVersion(String version) {
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
}
