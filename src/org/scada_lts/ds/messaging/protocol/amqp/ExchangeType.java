package org.scada_lts.ds.messaging.protocol.amqp;

import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public enum ExchangeType {
    NONE {
        @Override
        public void basicPublish(AmqpChannel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            channel.basicPublish("", locator.getQueueName(), message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void queueBind(AmqpChannel channel, AmqpPointLocatorVO locator) throws IOException {
        }

        @Override
        public void declare(AmqpChannel channel, AmqpPointLocatorVO locator, Map<String, Object> props) throws IOException {
            DurabilityType durabilityType = locator.getDurability();
            boolean durable = durabilityType == DurabilityType.DURABLE;
            channel.queueDeclare(locator.getQueueName(), durable, false, locator.isAutoDelete(), props);
        }
    },
    DIRECT {
        @Override
        public void basicPublish(AmqpChannel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            channel.basicPublish(locator.getExchangeName(), locator.getRoutingKey(), message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void queueBind(AmqpChannel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), locator.getExchangeName(), locator.getRoutingKey());
        }
    },
    TOPIC {
        @Override
        public void basicPublish(AmqpChannel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            DIRECT.basicPublish(channel, locator, message);
        }

        @Override
        public void queueBind(AmqpChannel channel, AmqpPointLocatorVO locator) throws IOException {
            DIRECT.queueBind(channel, locator);
        }
    },
    FANOUT {
        @Override
        public void basicPublish(AmqpChannel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            channel.basicPublish(locator.getExchangeName(), "", message.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void queueBind(AmqpChannel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), locator.getExchangeName(), "");
        }
    };

    public abstract void basicPublish(AmqpChannel channel, AmqpPointLocatorVO locator, String message) throws IOException;

    public abstract void queueBind(AmqpChannel channel, AmqpPointLocatorVO locator) throws IOException;

    public void declare(AmqpChannel channel, AmqpPointLocatorVO locator, Map<String, Object> props) throws IOException {
        DurabilityType durabilityType = locator.getDurability();
        String exchangeName = locator.getExchangeName();
        boolean durable = durabilityType == DurabilityType.DURABLE;

        channel.queueDeclare(locator.getQueueName(), durable, false, locator.isAutoDelete(), props);
        channel.exchangeDeclare(exchangeName, this, durable, locator.isAutoDelete(), locator.isInternal(), props);
    }
}
