package com.serotonin.mango.rt.publish.persistent;

public enum PacketType {
    VERSION((byte) 0), AUTH_KEY((byte) 1), POINT((byte) 2), DATA((byte) 3), CLOSE((byte) 4), ABORT((byte) 5), TEST(
            (byte) 6), RANGE_COUNT((byte) 7), POINT_UPDATE((byte) 8), POINT_HIERARCHY((byte) 9);

    public static PacketType getPacketType(byte id) throws PersistentProtocolException {
        if (VERSION.id == id)
            return VERSION;
        if (AUTH_KEY.id == id)
            return AUTH_KEY;
        if (POINT.id == id)
            return POINT;
        if (DATA.id == id)
            return DATA;
        if (CLOSE.id == id)
            return CLOSE;
        if (ABORT.id == id)
            return ABORT;
        if (TEST.id == id)
            return TEST;
        if (RANGE_COUNT.id == id)
            return RANGE_COUNT;
        if (POINT_UPDATE.id == id)
            return POINT_UPDATE;
        if (POINT_HIERARCHY.id == id)
            return POINT_HIERARCHY;
        throw new PersistentProtocolException("Unknown packet type: " + id);
    }

    private final byte id;

    private PacketType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
