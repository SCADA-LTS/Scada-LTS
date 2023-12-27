package com.serotonin.mango.rt.publish.persistent;

import java.io.IOException;

public class PayloadReadTimeoutException extends IOException {
    private static final long serialVersionUID = 1L;

    private final PacketType type;
    private final int length;
    private final byte[] payload;

    public PayloadReadTimeoutException(PacketType type, int length, byte[] payload) {
        this.type = type;
        this.length = length;
        this.payload = payload;
    }

    public PacketType getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public byte[] getPayload() {
        return payload;
    }
}
