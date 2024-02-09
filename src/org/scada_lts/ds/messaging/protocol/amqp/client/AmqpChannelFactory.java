package org.scada_lts.ds.messaging.protocol.amqp.client;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.channel.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.protocol.amqp.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class AmqpChannelFactory {

    private AmqpChannelFactory() {}

    static AmqpChannel createReceiver(DataPointRT dataPoint, AmqpConnection connection, Consumer<Exception> exceptionHandler, Supplier<Void> returnToNormal) throws IOException {
        AmqpChannel receive = configChannel(dataPoint, connection);
        if(receive == null)
            return null;
        basicConsume(dataPoint, receive, exceptionHandler, returnToNormal);
        return receive;
    }

    private static AmqpChannel configChannel(DataPointRT dataPointRT, AmqpConnection connection) throws IOException {
        AmqpChannel channel = createChannel(connection);
        initDataPoint(dataPointRT, channel);
        return channel;
    }

    private static AmqpChannel createChannel(AmqpConnection connection) throws IOException {
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
    private static void initDataPoint(DataPointRT dataPoint, AmqpChannel channel) throws IOException {
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        if (channel != null) {
            basicQos(channel, vo);
            declare(channel, vo);
            bind(channel, vo);
        }
    }

    private static void basicQos(AmqpChannel channel, AmqpPointLocatorVO vo) throws IOException {
        channel.basicQos(vo.getQos());
    }

    private static void basicConsume(DataPointRT dataPoint, AmqpChannel channel, Consumer<Exception> exceptionHandler, Supplier<Void> returnToNormal) throws IOException {
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        boolean noAck = vo.getMessageAck() == MessageAckType.NO_ACK;
        channel.basicConsume(vo.getQueueName(), noAck, new UpdatePointValueConsumer(dataPoint, vo::isWritable, exceptionHandler, returnToNormal));
    }

    private static void declare(AmqpChannel channel, AmqpPointLocatorVO vo) throws IOException {
        ExchangeType exchangeType = vo.getExchangeType();
        Map<String, Object> arguments = new HashMap<>();
        exchangeType.declare(channel, vo, arguments);
    }

    private static void bind(AmqpChannel channel, AmqpPointLocatorVO vo) throws IOException {
        ExchangeType exchangeType = vo.getExchangeType();
        exchangeType.queueBind(channel, vo);
    }
}
