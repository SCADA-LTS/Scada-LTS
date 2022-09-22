package org.scada_lts.ds.messaging.protocol.amqp.impl;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.channel.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpPointLocatorRT;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpPointLocatorVO;
import org.scada_lts.ds.messaging.protocol.amqp.ExchangeType;
import org.scada_lts.ds.messaging.protocol.amqp.MessageAckType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class AmqpV091ChannelFactory {

    private AmqpV091ChannelFactory() {}

    public static Channel createReceiver(DataPointRT dataPoint, Connection connection,
                                         Consumer<Exception> exceptionHandler, String updateErrorKey) throws IOException {
        Channel receive = configChannel(dataPoint, connection);
        if(receive == null)
            return null;
        basicConsume(dataPoint, receive, exceptionHandler, updateErrorKey);
        return receive;
    }

    private static Channel configChannel(DataPointRT dataPointRT, Connection connection) throws IOException {
        Channel channel = createChannel(connection);
        initDataPoint(dataPointRT, channel);
        return channel;
    }

    private static Channel createChannel(Connection connection) throws IOException {
        if(connection == null)
            return null;
        return connection.createChannel();
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

    private static void basicConsume(DataPointRT dataPoint, Channel channel, Consumer<Exception> exceptionHandler,
                                     String updateErrorKey) throws IOException {
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        boolean noAck = vo.getMessageAck() == MessageAckType.NO_ACK;
        channel.basicConsume(vo.getQueueName(), noAck, new ScadaConsumer(channel,
                new UpdatePointValueConsumer(dataPoint, vo::isWritable, updateErrorKey,
                        exceptionHandler)));
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

        private final UpdatePointValueConsumer updatePointValueConsumer;

        public ScadaConsumer(Channel channel, UpdatePointValueConsumer updatePointValueConsumer) {
            super(channel);
            this.updatePointValueConsumer = updatePointValueConsumer;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            super.handleDelivery(consumerTag, envelope, properties, body);
            updatePointValueConsumer.accept(body);
        }
    }
}
