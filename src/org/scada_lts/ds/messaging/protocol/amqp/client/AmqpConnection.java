package org.scada_lts.ds.messaging.protocol.amqp.client;

import java.io.IOException;

public interface AmqpConnection {
    AmqpChannel createChannel() throws IOException;
    void close() throws IOException;
    boolean isOpen();
}
