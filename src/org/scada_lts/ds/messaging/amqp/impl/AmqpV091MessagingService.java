package org.scada_lts.ds.messaging.amqp.impl;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.MessagingService;
import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.amqp.AmqpPointLocatorRT;
import org.scada_lts.ds.messaging.amqp.AmqpPointLocatorVO;
import org.scada_lts.ds.messaging.amqp.ExchangeType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.scada_lts.ds.messaging.amqp.impl.AmqpV091ChannelsFactory.createChannel;

public class AmqpV091MessagingService implements MessagingService {

    private final Map<Integer, Channel> channels;
    private final ReentrantReadWriteLock lock;
    private final AmqpDataSourceVO vo;

    private Connection connection;

    public AmqpV091MessagingService(AmqpDataSourceVO vo) {
        this.vo = vo;
        this.channels = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws IOException {
        Channel channel = initChannel(dataPoint);
        if (isOpenChannel(channel)) {
            basicPublish(dataPoint, channel, message);
        }
    }

    @Override
    public void initReceiver(DataPointRT dataPoint) throws IOException, TimeoutException {
        if(isOpen()) {
            initChannel(dataPoint);
        } else {
            open();
        }
    }

    @Override
    public void removeReceiver(DataPointRT dataPoint) throws IOException, TimeoutException {
        Channel channel = channels.get(dataPoint.getId());
        if(channel != null) {
            channels.remove(dataPoint.getId());
            channel.close();
        }
    }

    @Override
    public boolean isOpen() {
        this.lock.readLock().lock();
        try {
            return connection != null && connection.isOpen();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void open() throws IOException, TimeoutException {
        if(!isOpen()) {
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

            setConnection(rabbitFactory);
        }
    }

    @Override
    public void close() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (connection != null) {
                connection.close();
            }
            channels.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private static void basicPublish(DataPointRT dataPoint, Channel channel, String message) throws IOException {
        AmqpPointLocatorRT locatorRT = dataPoint.getPointLocator();
        AmqpPointLocatorVO locator = locatorRT.getVO();
        ExchangeType exchangeType = locator.getExchangeType();

        exchangeType.basicPublish(channel, locator, message, new AMQP.BasicProperties());
    }

    private boolean isOpenChannel(Channel channel) {
        return channel != null && channel.isOpen();
    }

    private Channel initChannel(DataPointRT dataPoint) {
        Connection conn = getConnection();
        return channels.computeIfAbsent(dataPoint.getId(), a -> {
            try {
                return createChannel(dataPoint, conn);
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        });
    }

    private void setConnection(ConnectionFactory rabbitFactory) throws IOException, TimeoutException {
        this.lock.writeLock().lock();
        try {
            this.connection = rabbitFactory.newConnection();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private Connection getConnection() {
        this.lock.readLock().lock();
        try {
            return connection;
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
