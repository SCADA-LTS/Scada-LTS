package org.scada_lts.ds.messaging.amqp;

import com.rabbitmq.client.*;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.MessagingService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class AmqpMessagingService implements MessagingService {

    private Connection connection;
    private Channel channel;
    private final AmqpDataSourceVO vo;
    private volatile boolean amqpBindEstablished = false;

    public AmqpMessagingService(AmqpDataSourceVO vo) {
        this.vo = vo;
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws IOException {
        initDataPoint(dataPoint, channel);
        AmqpPointLocatorRT locatorVO = dataPoint.getPointLocator();
        ExchangeType exchangeType = locatorVO.getVO().getExchangeType();
        exchangeType.basicPublish(channel, locatorVO.getVO(), message);
    }

    @Override
    public void consume(DataPointRT dp) throws IOException {
        if (channel != null && channel.isOpen() && !amqpBindEstablished) {
            initDataPoint(dp, channel);
            basicConsume(dp, channel);
            amqpBindEstablished = true;
        }
    }

    @Override
    public boolean isOpened() {
        return channel != null;
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
        channel = connection.createChannel();
    }

    @Override
    public void close() throws IOException, TimeoutException {
        if(channel != null) {
            try {
                channel.close();
            } finally {
                if(connection != null)
                    connection.close();
                amqpBindEstablished = false;
            }
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

        boolean durable = vo.getQueueDurability().ordinal() == 1;

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
        boolean noAck = vo.getMessageAck() == MessageAckType.ACK;
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
