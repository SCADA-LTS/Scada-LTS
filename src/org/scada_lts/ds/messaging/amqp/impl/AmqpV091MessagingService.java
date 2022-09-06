package org.scada_lts.ds.messaging.amqp.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.MessagingService;
import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.amqp.AmqpPointLocatorRT;
import org.scada_lts.ds.messaging.amqp.AmqpPointLocatorVO;
import org.scada_lts.ds.messaging.amqp.ExchangeType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;


public class AmqpV091MessagingService implements MessagingService {

    private final AmqpDataSourceVO vo;
    private final AmqpV091Channels channels;

    private final ReentrantReadWriteLock lock;
    private Connection connection;

    public AmqpV091MessagingService(AmqpDataSourceVO vo) {
        this.vo = vo;
        this.channels = new AmqpV091Channels(new HashMap<>());
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws Exception {
        getConnectionIfOpen().ifPresent(conn -> {
            Channel channel = channels.getChannel(dataPoint)
                    .orElseThrow(() -> new RuntimeException("Error publish: " + LoggingUtils.dataPointInfo(dataPoint.getVO())
                    + ", Data Source: " + LoggingUtils.dataSourceInfo(vo) + ", Message: channel is disconnect"));
            try {
                basicPublish(dataPoint, channel, message);
            } catch (Exception e) {
                throw new RuntimeException("Error publish: " + LoggingUtils.dataPointInfo(dataPoint.getVO())
                        + ", Data Source: " + LoggingUtils.dataSourceInfo(vo) + LoggingUtils.causeInfo(e), e);
            }
        });
    }

    @Override
    public void initReceiver(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws Exception {
        Connection openedConnection = getConnectionIfOpen().orElseGet(() -> {
            try {
                Connection conn = openConnection();
                setConnection(conn);
                return conn;
            } catch (Exception e) {
                throw new RuntimeException("Error Connect AMQP Client: " + LoggingUtils.dataPointInfo(dataPoint.getVO())
                        + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo) + LoggingUtils.causeInfo(e), e);
            }
        });
        if(openedConnection != null && openedConnection.isOpen()) {
            channels.getOrInitChannel(dataPoint, vo, openedConnection, exceptionHandler, updateErrorKey);
        }
    }

    @Override
    public void removeReceiver(DataPointRT dataPoint) throws Exception {
        channels.removeChannel(dataPoint);
    }

    @Override
    public boolean isOpen() {
        this.lock.readLock().lock();
        try {
            if(connection == null)
                return false;
            return connection.isOpen();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean isOpen(DataPointRT dataPoint) {
        return isOpen() && channels.isOpenChannel(dataPoint);
    }

    @Override
    public void open() throws Exception {
        if(!isOpen())
            setConnection(openConnection());
    }

    private Connection openConnection() throws Exception {
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

    @Override
    public void close() throws Exception {
        this.lock.writeLock().lock();
        try {
            if (connection != null) {
                connection.close();
            }
        } finally {
            this.lock.writeLock().unlock();
            channels.clearChannels();
        }
    }

    private static void basicPublish(DataPointRT dataPoint, Channel channel, String message) throws IOException {
        AmqpPointLocatorRT locatorRT = dataPoint.getPointLocator();
        AmqpPointLocatorVO locator = locatorRT.getVO();
        ExchangeType exchangeType = locator.getExchangeType();

        exchangeType.basicPublish(channel, locator, message, new AMQP.BasicProperties());
    }

    private void setConnection(Connection connection) {
        this.lock.writeLock().lock();
        try {
            this.connection = connection;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private Optional<Connection> getConnectionIfOpen() {
        this.lock.readLock().lock();
        try {
            if(connection != null && connection.isOpen())
                return Optional.of(connection);
            return Optional.empty();
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
