package com.serotonin.mango.rt.publish.persistent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.io.StreamUtils;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.i18n.LocalizableMessageParseException;

public class Packet {
    public static final int PUBLISHER_SOCKET_TIMEOUT = 30000;
    public static final int DATA_SOURCE_SOCKET_TIMEOUT = 5000;
    public static final int TEST_PACKET_SEND_DELAY = 60000;

    private static final int PAYLOAD_READ_TIMEOUT = 60000;
    private static final byte[] PAYLOAD_READ_BUFFER = new byte[1024];
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static final byte[] EMPTY = new byte[0];

    //
    //
    // Packet pool
    //
    static final ObjectPool packetPool = new GenericObjectPool(new BasePoolableObjectFactory() {
        @Override
        public Object makeObject() {
            return new Packet();
        }
    }, 1000);

    public static Packet borrowPacket(PacketType type, ByteQueue payload) {
        try {
            Packet packet = (Packet) packetPool.borrowObject();
            packet.type = type;
            packet.payload.push(payload);
            return packet;
        }
        catch (Exception e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    //
    //
    // Read packets
    //
    public static Packet readPacketNoBlock(InputStream in, int version) throws IOException, PersistentAbortException,
            PersistentProtocolException {
        if (in.available() > 0)
            return readPacket(in, version);
        return null;
    }

    public static Packet readPacket(InputStream in, int version) throws IOException, PersistentAbortException,
            PersistentProtocolException {
        PacketType packetType;
        int length;
        Packet packet = null;

        try {
            // Get the packet info
            if (version == 1) {
                length = StreamUtils.read4ByteSigned(in);
                packetType = PacketType.getPacketType(StreamUtils.readByte(in));
            }
            else if (version >= 2) {
                int i;
                while (true) {
                    // First byte must be DA
                    i = in.read();
                    if (i == -1)
                        throw new IOException("EOS");
                    if (i != 0xda)
                        continue;

                    // Second byte must start with D
                    i = in.read();
                    if (i == -1)
                        throw new IOException("EOS");
                    if ((i >> 4) != 0xd)
                        continue;

                    // The rest of the second byte is the packet type
                    packetType = PacketType.getPacketType((byte) (i & 0xf));
                    length = read4ByteSigned(in);
                    break;
                }
            }
            else
                throw new PersistentProtocolException("Unknown version " + version);

            // Create the packet.
            try {
                packet = (Packet) packetPool.borrowObject();
            }
            catch (Exception e) {
                throw new ShouldNeverHappenException(e);
            }
            packet.type = packetType;

            if (length > 0)
                readPayload(in, packet, length);

            if (packet.type == PacketType.ABORT) {
                String message = packet.popString();
                try {
                    throw new PersistentAbortException(LocalizableMessage.deserialize(message));
                }
                catch (LocalizableMessageParseException e) {
                    throw new PersistentAbortException(new LocalizableMessage("common.default", message));
                }
            }

            return packet;
        }
        catch (IOException e) {
            if (packet != null)
                packet.release();
            throw e;
        }
        catch (PersistentAbortException e) {
            if (packet != null)
                packet.release();
            throw e;
        }
    }

    /**
     * Data may be getting sent very regularly from the publisher, so if there is corruption in the stream an SO timeout
     * may not get thrown. Having a payload read timeout ensures that unusually long read times - such as when the
     * length is suspiciously long - don't cause the communication to cease up, at least not for very long.
     * 
     * @param in
     *            the socket input stream
     * @param packet
     *            the packet we're trying to read
     * @param length
     *            the length of the payload to read
     * @throws IOException
     *             if a read error occurs, including an SO timeout
     * @throws PayloadReadTimeoutException
     *             if a payload read timeout occurs
     */
    private static void readPayload(InputStream in, Packet packet, int length) throws IOException,
            PayloadReadTimeoutException {
        long timeout = System.currentTimeMillis() + PAYLOAD_READ_TIMEOUT;

        while (length > 0) {
            if (System.currentTimeMillis() > timeout)
                throw new PayloadReadTimeoutException(packet.getType(), length, packet.payload.popAll());

            int readLen = length;
            if (readLen > PAYLOAD_READ_BUFFER.length)
                readLen = PAYLOAD_READ_BUFFER.length;

            int readCount = in.read(PAYLOAD_READ_BUFFER, 0, readLen);
            packet.payload.push(PAYLOAD_READ_BUFFER, 0, readCount);
            length -= readCount;
        }
    }

    //
    //
    // Write packets
    //
    public static void writePacket(OutputStream out, int version, Packet packet) throws IOException {
        writeHeader(out, version, packet.type, packet.payload.size());
        packet.payload.write(out);
    }

    public static void writePacket(OutputStream out, int version, PacketType type, byte[] payload) throws IOException {
        writeHeader(out, version, type, payload.length);
        out.write(payload);
    }

    public static void writePacket(OutputStream out, int version, PacketType type, ByteQueue payload)
            throws IOException {
        writeHeader(out, version, type, payload.size());
        payload.write(out);
    }

    private static void writeHeader(OutputStream out, int version, PacketType type, int length) throws IOException {
        if (version == 1) {
            StreamUtils.write4ByteSigned(out, length);
            StreamUtils.writeByte(out, type.getId());
        }
        else if (version >= 2) {
            out.write(0xda);
            out.write(0xd0 | type.getId());
            write4ByteSigned(out, length);
        }
    }

    //
    //
    // Utility methods
    //
    public static void pushString(ByteQueue queue, String s) {
        byte[] b = s.getBytes(CHARSET);
        queue.pushU2B(b.length);
        queue.push(b);
    }

    public static void pushLong(ByteQueue queue, long l) {
        queue.pushU4B(l >> 32);
        queue.pushU4B(l);
    }

    public static void pushDouble(ByteQueue queue, double d) {
        pushLong(queue, Double.doubleToLongBits(d));
    }

    private static void write4ByteSigned(OutputStream out, int i) throws IOException {
        out.write((byte) ((i >> 24) & 0xFF));
        out.write((byte) ((i >> 16) & 0xFF));
        out.write((byte) ((i >> 8) & 0xFF));
        out.write((byte) (i & 0xFF));
    }

    private static int read4ByteSigned(InputStream in) throws IOException {
        return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
    }

    //
    //
    // Non-static stuff
    //
    private PacketType type;
    private final ByteQueue payload = new ByteQueue();

    public PacketType getType() {
        return type;
    }

    public ByteQueue getPayload() {
        return payload;
    }

    public String popString() {
        return payload.popString(payload.popU2B(), CHARSET);
    }

    public long popLong() {
        return (payload.popU4B() << 32) | payload.popU4B();
    }

    public double popDouble() {
        return Double.longBitsToDouble(popLong());
    }

    public void release() {
        payload.clear();
        try {
            packetPool.returnObject(this);
        }
        catch (Exception e) {
            throw new ShouldNeverHappenException(e);
        }
    }
}
