package org.scada_lts.ds.messaging.amqp;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ChannelsFactory {

    private ChannelsFactory() {}

    private static final Log LOG = LogFactory.getLog(ChannelsFactory.class);

    public static Optional<ChannelLocator> createChannels(DataPointRT dataPoint, Connection connection) {
        Channel receive = configReceiver(dataPoint, connection);
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        try {
            return Optional.of(new ChannelLocator(receive, locator.getVO()));

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private static Channel configReceiver(DataPointRT dataPoint, Connection connection) {
        Channel receive = configChannel(dataPoint, connection);
        if(receive == null)
            return null;
        try {
            basicConsume(dataPoint, receive);
            return receive;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            close(receive);
            return null;
        }
    }

    private static Channel createChannel(Connection connection) {
        if(connection == null)
            return null;
        try {
            return connection.createChannel();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    private static Channel configChannel(DataPointRT dataPointRT, Connection connection) {
        Channel channel = createChannel(connection);
        try {
            initDataPoint(dataPointRT, channel);
            return channel;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
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
     * @param dataPoint - single DataPoint
     * @param channel - channel (with prepared connection)
     */
    private static void initDataPoint(DataPointRT dataPoint, Channel channel) {

        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        if (channel != null) {
            basicQos(channel, vo);
            declare(channel, vo);
            bind(channel, vo);
        }
    }

    private static void basicQos(Channel channel, AmqpPointLocatorVO vo) {
        try {
            channel.basicQos(vo.getQos());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static void basicConsume(DataPointRT dataPoint, Channel channel) {
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        boolean noAck = vo.getMessageAck() == MessageAckType.NO_ACK;
        try {
            channel.basicConsume(vo.getQueueName(), noAck, new ScadaConsumer(channel, dataPoint, vo));
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void declare(Channel channel, AmqpPointLocatorVO vo) {
        ExchangeType exchangeType = vo.getExchangeType();
        try {

            Map<String, Object> arguments = new HashMap<>();
            exchangeType.declare(channel, vo, arguments);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void bind(Channel channel, AmqpPointLocatorVO vo) {
        ExchangeType exchangeType = vo.getExchangeType();
        try {
            exchangeType.queueBind(channel, vo);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void close(Channel channel) {
        try {
            if(channel != null)
                channel.close();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
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
            if(locator.isWritable()) {
                String message = new String(body, StandardCharsets.UTF_8);
                try {
                    dataPoint.updatePointValue(message);
                } catch (Exception ex1) {
                    LOG.error(ex1.getMessage(), ex1);
                }
            }
        }
    }

    public static class ChannelLocator {
        private final Channel channel;
        private final AmqpPointLocatorVO locator;

        public ChannelLocator(Channel channel, AmqpPointLocatorVO locator) {
            this.channel = channel;
            this.locator = locator;
        }

        public Channel getChannel() {
            return channel;
        }

        public void resetBrokerConfig() {
            try {
                locator.getExchangeType().resetBrokerConfig(channel, locator);
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        public void close()  {
            ChannelsFactory.close(channel);
        }
    }
}
