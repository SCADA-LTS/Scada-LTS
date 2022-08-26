package org.scada_lts.ds.messaging.amqp;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.MessagingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import static org.scada_lts.ds.messaging.amqp.ChannelsFactory.createChannels;

public class AmqpMessagingService implements MessagingService {

    private Connection connection;
    private final Map<Integer, ChannelsFactory.ChannelLocator> channels = new ConcurrentHashMap<>();
    private final AmqpDataSourceVO vo;
    private volatile boolean closed = true;

    public AmqpMessagingService(AmqpDataSourceVO vo) {
        this.vo = vo;
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws IOException {
        Channel channel = getChannels(dataPoint).getChannel();
        if (isOpenChannel(channel) && !closed) {
            basicPublish(dataPoint, channel, message);
        }
    }

    @Override
    public void initReceiver(DataPointRT dataPoint) {
        if(!closed)
            getChannels(dataPoint);
    }

    @Override
    public boolean isOpen() {
        return connection != null && connection.isOpen();
    }

    @Override
    public void open() throws IOException, TimeoutException {

        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(vo.getServerIpAddress());
        rabbitFactory.setPort(vo.getServerPortNumber());

        rabbitFactory.setConnectionTimeout(vo.getConnectionTimeout());
        rabbitFactory.setNetworkRecoveryInterval(vo.getNetworkRecoveryInterval());
        rabbitFactory.setChannelRpcTimeout(vo.getChannelRpcTimeout());
        rabbitFactory.setAutomaticRecoveryEnabled(vo.isAutomaticRecoveryEnabled());
        //rabbitFactory.setWorkPoolTimeout(vo.getConnectionTimeout());
        //rabbitFactory.setShutdownTimeout(vo.getConnectionTimeout());
        //rabbitFactory.setHandshakeTimeout(vo.getConnectionTimeout());

        if (!vo.getServerUsername().isBlank()) {
            rabbitFactory.setUsername(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isBlank()) {
            rabbitFactory.setPassword(vo.getServerPassword());
        }

        if (!vo.getServerVirtualHost().isBlank()) {
            rabbitFactory.setVirtualHost(vo.getServerVirtualHost());
        }

        connection = rabbitFactory.newConnection();
        closed = false;
    }

    @Override
    public void close() throws IOException {
        try {
            closed = true;
            Map<Integer, ChannelsFactory.ChannelLocator> temp = new HashMap<>(this.channels);
            for (ChannelsFactory.ChannelLocator channel : temp.values()) {
                if (channel != null) {
                    channel.close();
                }
            }
            temp.clear();
        } finally {
            channels.clear();
            if (connection != null)
                connection.close();
        }
    }

    @Override
    public void resetBrokerConfig() {
        Map<Integer, ChannelsFactory.ChannelLocator> temp = new HashMap<>(this.channels);
        for (ChannelsFactory.ChannelLocator channel : temp.values()) {
            if (channel != null) {
                channel.resetBrokerConfig();
            }
        }
        temp.clear();
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

    private ChannelsFactory.ChannelLocator getChannels(DataPointRT dataPoint) {
        return channels.computeIfAbsent(dataPoint.getId(), a -> createChannels(dataPoint, connection).orElse(null));
    }
}
