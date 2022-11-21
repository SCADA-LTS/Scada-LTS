package org.scada_lts.ds.messaging.protocol.amqp.client.impl;

import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpConnection;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpConnectionManager;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SyncAmqpConnectionManager implements AmqpConnectionManager {

    private final ReentrantReadWriteLock lock;
    private AmqpConnection connection;

    public SyncAmqpConnectionManager() {
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void close() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (connection != null) {
                connection.close();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isOpen() {
        AmqpConnection conn = getConnection();
        if(conn == null)
            return false;
        return conn.isOpen();
    }

    @Override
    public AmqpConnection open(AmqpDataSourceVO vo) throws Exception {
        if(!isOpen()) {
            setConnection(RabbitConnectionFactory.openConnection(vo));
        }
        return getConnection();
    }

    @Override
    public Optional<AmqpConnection> getIfOpen() {
        AmqpConnection conn = getConnection();
        if(conn != null && conn.isOpen())
            return Optional.of(conn);
        return Optional.empty();
    }

    private AmqpConnection getConnection() {
        this.lock.readLock().lock();
        try {
            return connection;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void setConnection(AmqpConnection connection) {
        this.lock.writeLock().lock();
        try {
            this.connection = connection;
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
