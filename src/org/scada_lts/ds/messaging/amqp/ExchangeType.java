package org.scada_lts.ds.messaging.amqp;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public enum ExchangeType {
    NONE {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            channel.basicPublish("", locator.getQueueName(), null, message.getBytes());
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), "", "");
        }
    },
    DIRECT {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            channel.basicPublish(locator.getExchangeName(), locator.getRoutingKey(), null, message.getBytes());
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), locator.getExchangeName(), locator.getRoutingKey());
        }
    },
    TOPIC {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            DIRECT.basicPublish(channel, locator, message);
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            DIRECT.queueBind(channel, locator);
        }
    },
    FANOUT {
        @Override
        public void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message) throws IOException {
            channel.basicPublish(locator.getExchangeName(), locator.getQueueName(), null, message.getBytes());
        }

        @Override
        public void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException {
            channel.queueBind(locator.getQueueName(), locator.getExchangeName(), "");
        }
    };

    public abstract void basicPublish(Channel channel, AmqpPointLocatorVO locator, String message) throws IOException;

    public abstract void queueBind(Channel channel, AmqpPointLocatorVO locator) throws IOException;
}
