package org.scada_lts.ds.messaging.protocol.amqp.client.impl;

import com.rabbitmq.client.Connection;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpChannel;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpConnection;

import java.io.IOException;

class RabbitConnection implements AmqpConnection {

    private final Connection connection;

    public RabbitConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AmqpChannel createChannel() throws IOException {
        return new RabbitChannel(connection.createChannel());
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    @Override
    public boolean isOpen() {
        return connection.isOpen();
    }
}
