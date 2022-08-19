package org.scada_lts.ds.messaging.amqp;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.MessagingService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class AmqpMessagingService implements MessagingService {

    private static final Log LOG = LogFactory.getLog(AmqpMessagingService.class);

    private Connection connection;
    private final Map<Integer, Channel> channels = new ConcurrentHashMap<>();
    private final AmqpDataSourceVO vo;
    private volatile boolean amqpBindEstablished = false;

    public AmqpMessagingService(AmqpDataSourceVO vo) {
        this.vo = vo;
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws IOException {
        Channel channel = getChannel(dataPoint);
        if (isOpenChannel(channel)) {
            AmqpPointLocatorRT locatorVO = dataPoint.getPointLocator();
            ExchangeType exchangeType = locatorVO.getVO().getExchangeType();
            exchangeType.basicPublish(channel, locatorVO.getVO(), message);
        }
    }

    @Override
    public void consume(DataPointRT dataPoint) throws IOException {
        Channel channel = getChannel(dataPoint);
        if (isOpenChannel(channel)) {
            basicConsume(dataPoint, channel);
            amqpBindEstablished = true;
        }
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
        rabbitFactory.setWorkPoolTimeout(10000);

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
    }

    @Override
    public void close() throws IOException {
        try {
            for (Channel channel : channels.values()) {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (Exception ex) {
                        LOG.warn(ex.getMessage(), ex);
                    }
                }
            }
        } finally {
            if (connection != null)
                connection.close();
            amqpBindEstablished = false;
        }
    }

    private boolean isOpenChannel(Channel channel) {
        return channel != null && channel.isOpen() && !amqpBindEstablished;
    }

    private Channel getChannel(DataPointRT dataPoint) {
        return channels.computeIfAbsent(dataPoint.getId(), a -> createChannel(dataPoint, connection).orElse(null));
    }

    private static Optional<Channel> createChannel(DataPointRT dataPointRT, Connection connection) {
        if(dataPointRT == null || connection == null)
            return Optional.empty();
        try {
            Channel channel = connection.createChannel();
            initDataPoint(dataPointRT, channel);
            return Optional.of(channel);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Initialize AMQP Data Point
     * Before initializing make sure you have got created exchange type,
     * exchange name, queue name and bindings between them.
     * <p>
     * Any connection error breaks the connection between ScadaLTS
     * and RabbitMQ server
     *
     * @param dp      - single AMQP DataPoint
     * @param channel - RabbitMQ channel (with prepared connection)
     * @throws IOException - Errors
     */
    private static void initDataPoint(DataPointRT dp, Channel channel) throws IOException {

        AmqpPointLocatorRT locator = dp.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        ExchangeType exchangeType = vo.getExchangeType();

        boolean durable = vo.getQueueDurability() == DurabilityType.DURABLE;

        if (channel != null) {
            if(exchangeType != ExchangeType.NONE) {
                setQueueName(channel, vo, exchangeType, durable);
                exchangeType.queueBind(channel, vo);
            }
        }
    }

    private static void basicConsume(DataPointRT dp, Channel channel) throws IOException {
        AmqpPointLocatorRT locator = dp.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        boolean noAck = vo.getMessageAck() == MessageAckType.NO_ACK;
        channel.basicConsume(vo.getQueueName(), noAck, new ScadaConsumer(channel, dp, vo));
    }

    private static void setQueueName(Channel channel, AmqpPointLocatorVO vo, ExchangeType exchangeType, boolean durable) throws IOException {
        channel.exchangeDeclare(vo.getExchangeName(), exchangeType.toString(), durable);
        vo.setQueueName(channel.queueDeclare().getQueue());
    }

    static class ScadaConsumer extends DefaultConsumer {

        private final DataPointRT dataPoint;
        private final AmqpPointLocatorVO locator;

        ScadaConsumer(Channel channel, DataPointRT dataPoint, AmqpPointLocatorVO locator) {
            super(channel);
            this.dataPoint = dataPoint;
            this.locator = locator;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            super.handleDelivery(consumerTag, envelope, properties, body);

            if (locator.isWritable()) {
                String message = new String(body, StandardCharsets.UTF_8);
                dataPoint.updatePointValue(message);
            }
        }
    }
}
