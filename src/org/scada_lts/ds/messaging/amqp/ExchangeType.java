package org.scada_lts.ds.messaging.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public enum ExchangeType {
    NONE(null) {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message, AMQP.BasicProperties basicProperties) throws IOException {
            channel.basicPublish("", locator.getQueueName(), basicProperties, message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
        }

        @Override
        public void declare(Channel channel, AmqpPointLocatorVO locator, Map<String, Object> props) throws IOException {
            DurabilityType durabilityType = locator.getDurability();
            boolean durable = durabilityType == DurabilityType.DURABLE;
            channel.queueDeclare(locator.getQueueName(), durable, false, locator.isAutoDelete(), props);
        }
    },
    DIRECT(BuiltinExchangeType.DIRECT) {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message, AMQP.BasicProperties basicProperties) throws IOException {
            channel.basicPublish(locator.getExchangeName(), locator.getRoutingKey(), basicProperties, message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), locator.getExchangeName(), locator.getRoutingKey());
        }
    },
    TOPIC(BuiltinExchangeType.TOPIC) {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message, AMQP.BasicProperties basicProperties) throws IOException {
            DIRECT.basicPublish(channel, locator, message, basicProperties);
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            DIRECT.queueBind(channel, locator);
        }
    },
    FANOUT(BuiltinExchangeType.FANOUT) {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message, AMQP.BasicProperties basicProperties) throws IOException {
            channel.basicPublish(locator.getExchangeName(), "", basicProperties, message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), locator.getExchangeName(), "");
        }
    };

    public abstract void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message, AMQP.BasicProperties basicProperties) throws IOException;

    public abstract void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException;

    public void declare(Channel channel, AmqpPointLocatorVO locator, Map<String, Object> props) throws IOException {
        DurabilityType durabilityType = locator.getDurability();
        String exchangeName = locator.getExchangeName();
        boolean durable = durabilityType == DurabilityType.DURABLE;

        channel.queueDeclare(locator.getQueueName(), durable, false, locator.isAutoDelete(), props);
        channel.exchangeDeclare(exchangeName, getType(), durable, locator.isAutoDelete(), locator.isInternal(), props);
    }

    private final BuiltinExchangeType type;

    ExchangeType(BuiltinExchangeType type) {
        this.type = type;
    }

    public BuiltinExchangeType getType() {
        return type;
    }
}
