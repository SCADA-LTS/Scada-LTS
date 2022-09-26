package org.scada_lts.ds.messaging.protocol.amqp.client;

import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;

import java.io.IOException;
import java.util.Optional;

public interface AmqpConnectionManager {
    void close() throws IOException;
    boolean isOpen();
    AmqpConnection open(AmqpDataSourceVO vo) throws Exception;
    Optional<AmqpConnection> getIfOpen();
}
