package org.scada_lts.ds.messaging.protocol.amqp.impl;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AmqpV091Connection {

    private final ReentrantReadWriteLock lock;
    private Connection connection;

    public AmqpV091Connection() {
        this.lock = new ReentrantReadWriteLock();
    }

    public Connection getConnection() {
        this.lock.readLock().lock();
        try {
            return connection;
        } finally {
            this.lock.readLock().unlock();
        }
    }

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

    public Connection open(AmqpDataSourceVO vo) throws Exception {
        if(!isOpen()) {
            setConnection(openConnection(vo));
        }
        return getConnection();
    }

    public boolean isOpen() {
        Connection conn = getConnection();
        if(conn == null)
            return false;
        return conn.isOpen();
    }

    public Optional<Connection> getIfOpen() {
        Connection conn = getConnection();
        if(conn != null && conn.isOpen())
            return Optional.of(conn);
        return Optional.empty();
    }

    private void setConnection(Connection connection) {
        this.lock.writeLock().lock();
        try {
            this.connection = connection;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private Connection openConnection(AmqpDataSourceVO vo) throws Exception {
        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(vo.getServerHost());
        rabbitFactory.setPort(vo.getServerPortNumber());

        rabbitFactory.setChannelRpcTimeout(vo.getChannelRpcTimeout());
        rabbitFactory.setAutomaticRecoveryEnabled(vo.isAutomaticRecoveryEnabled());
        rabbitFactory.setNetworkRecoveryInterval(vo.getNetworkRecoveryInterval());

        rabbitFactory.setConnectionTimeout(vo.getConnectionTimeout());
        rabbitFactory.setShutdownTimeout(vo.getConnectionTimeout());
        rabbitFactory.setHandshakeTimeout(vo.getConnectionTimeout());

        if (!vo.getServerUsername().isBlank()) {
            rabbitFactory.setUsername(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isBlank()) {
            rabbitFactory.setPassword(vo.getServerPassword());
        }

        if (!vo.getServerVirtualHost().isBlank()) {
            rabbitFactory.setVirtualHost(vo.getServerVirtualHost());
        }
        return rabbitFactory.newConnection();
    }
}
