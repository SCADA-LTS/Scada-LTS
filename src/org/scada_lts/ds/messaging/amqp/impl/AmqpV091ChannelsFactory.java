package org.scada_lts.ds.messaging.amqp.impl;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.amqp.AmqpPointLocatorRT;
import org.scada_lts.ds.messaging.amqp.AmqpPointLocatorVO;
import org.scada_lts.ds.messaging.amqp.ExchangeType;
import org.scada_lts.ds.messaging.amqp.MessageAckType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class AmqpV091ChannelsFactory {

    private AmqpV091ChannelsFactory() {}

    private static final Log LOG = LogFactory.getLog(AmqpV091ChannelsFactory.class);

    public static Channel createChannel(DataPointRT dataPoint, Connection connection) throws IOException {
        return configReceiver(dataPoint, connection);
    }

    private static Channel configReceiver(DataPointRT dataPoint, Connection connection) throws IOException {
        Channel receive = configChannel(dataPoint, connection);
        if(receive == null)
            return null;
        basicConsume(dataPoint, receive);
        return receive;
    }

    private static Channel createChannel(Connection connection) throws IOException {
        if(connection == null)
            return null;
        return connection.createChannel();
    }

    private static Channel configChannel(DataPointRT dataPointRT, Connection connection) throws IOException {
        Channel channel = createChannel(connection);
        initDataPoint(dataPointRT, channel);
        return channel;
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
    private static void initDataPoint(DataPointRT dataPoint, Channel channel) throws IOException {

        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        if (channel != null) {
            basicQos(channel, vo);
            declare(channel, vo);
            bind(channel, vo);
        }
    }

    private static void basicQos(Channel channel, AmqpPointLocatorVO vo) throws IOException {
        channel.basicQos(vo.getQos());
    }

    private static void basicConsume(DataPointRT dataPoint, Channel channel) throws IOException {
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        boolean noAck = vo.getMessageAck() == MessageAckType.NO_ACK;
        channel.basicConsume(vo.getQueueName(), noAck, new ScadaConsumer(channel, dataPoint, vo));
    }

    private static void declare(Channel channel, AmqpPointLocatorVO vo) throws IOException {
        ExchangeType exchangeType = vo.getExchangeType();
        Map<String, Object> arguments = new HashMap<>();
        exchangeType.declare(channel, vo, arguments);
    }

    private static void bind(Channel channel, AmqpPointLocatorVO vo) throws IOException {
        ExchangeType exchangeType = vo.getExchangeType();
        exchangeType.queueBind(channel, vo);
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
}
