package org.scada_lts.ds.messaging.amqp.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.MessagingChannel;
import org.scada_lts.ds.messaging.MessagingChannels;
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

import static com.serotonin.mango.util.LoggingUtils.*;


public class AmqpV091MessagingService implements MessagingService {

    private static final Log LOG = LogFactory.getLog(AmqpV091MessagingService.class);

    private final AmqpDataSourceVO vo;
    private final MessagingChannels<Channel> channels;
    private volatile boolean blocked;
    private final ReentrantReadWriteLock lock;
    private Connection connection;

    public AmqpV091MessagingService(AmqpDataSourceVO vo) {
        this.vo = vo;
        this.channels = new MessagingChannels<>(new HashMap<>());
        this.lock = new ReentrantReadWriteLock();
        this.blocked = false;
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws Exception {
        if(blocked) {
            throw new IllegalStateException("Stop publish: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Service of shutting down:  "  + LoggingUtils.dataSourceInfo(vo) + ", Value: " + message);
        }
        getConnectionIfOpen().ifPresent(conn -> {
            MessagingChannel<Channel> channel = channels.getChannel(dataPoint)
                    .orElseThrow(() -> new RuntimeException("Error Publish: " + dataSourcePointInfo(vo, dataPoint.getVO())
                            + ", Value: " + message + ", Message: channel is disconnect"));
            try {
                basicPublish(dataPoint, channel.toOrigin(), message);
            } catch (IOException e) {
                throw new RuntimeException("Error Publish: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", Value: " + message + ", " + causeInfo(e), e.getCause());
            } catch (Exception e) {
                throw new RuntimeException("Error Publish: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", Value: " + message + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public void initReceiver(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws Exception {
        if(blocked) {
            LOG.warn("Stop Init Channel: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Service of shutting down: "  + LoggingUtils.dataSourceInfo(vo));
            return;
        }
        Connection openedConnection = getConnectionIfOpen().orElseGet(() -> {
            try {
                Connection conn = openConnection();
                setConnection(conn);
                return conn;
            } catch (IOException e) {
                throw new RuntimeException("Error Open Connection: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
            } catch (Exception e) {
                throw new RuntimeException("Error Open Connection: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
        if(openedConnection != null && openedConnection.isOpen()) {
            channels.initChannel(dataPoint, () -> {
                try {
                    return new AmqpV091Channel(AmqpV091ChannelFactory.createReceiver(dataPoint, connection, exceptionHandler, updateErrorKey));
                } catch (IOException e) {
                    throw new RuntimeException("Error Create Channel: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
                } catch (Exception e) {
                    throw new RuntimeException("Error Create Channel: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
                }
            });
        }
    }

    @Override
    public void removeReceiver(DataPointRT dataPoint) throws Exception {
        if(blocked) {
            LOG.warn("Stop Remove Channel: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Service of shutting down: "  + LoggingUtils.dataSourceInfo(vo));
            return;
        }
        try {
            channels.removeChannel(dataPoint, vo.getConnectionTimeout());
        } catch (IOException e) {
            throw new RuntimeException("Error Remove Channel: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Error Remove Channel: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public boolean isOpen() {
        if(blocked)
            return false;
        Connection conn = getConnection();
        if(conn == null)
            return false;
        return conn.isOpen();
    }

    @Override
    public boolean isOpen(DataPointRT dataPoint) {
        return isOpen() && channels.isOpenChannel(dataPoint);
    }

    @Override
    public void open() throws Exception {
        try {
            if (!isOpen())
                setConnection(openConnection());
            blocked = false;
        } catch (IOException e) {
            throw new RuntimeException("Error Open Connection: " + dataSourceInfo(vo) + ", " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Error Open Connection: " + dataSourceInfo(vo) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void close() throws Exception {
        blocked = true;
        try {
            channels.closeChannels(vo.getConnectionTimeout());
        } catch (Exception ex) {
            LOG.warn("Error closeChannels: " + dataSourceInfo(vo) + ", " + exceptionInfo(ex), ex);
        }
        try {
            closeConnection();
        } catch (IOException e) {
            throw new RuntimeException("Error Close Connection: " + dataSourceInfo(vo) + ", " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Error Close Connection: " + dataSourceInfo(vo) + ", " + exceptionInfo(e), e);
        }
    }

    private void closeConnection() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (connection != null) {
                connection.close();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
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

    private static void basicPublish(DataPointRT dataPoint, Channel channel, String message) throws Exception {
        AmqpPointLocatorRT locatorRT = dataPoint.getPointLocator();
        AmqpPointLocatorVO locator = locatorRT.getVO();
        ExchangeType exchangeType = locator.getExchangeType();
        exchangeType.basicPublish(channel, locator, message, new AMQP.BasicProperties());
    }

    private Optional<Connection> getConnectionIfOpen() {
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

    private Connection getConnection() {
        this.lock.readLock().lock();
        try {
            return connection;
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
