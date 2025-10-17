package org.scada_lts.ds.messaging.protocol.amqp.client.impl;

import com.rabbitmq.client.*;
import org.scada_lts.ds.messaging.channel.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.protocol.amqp.ExchangeType;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpChannel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

class RabbitChannel implements AmqpChannel {

    private final Channel channel;

    public RabbitChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public int getChannelNumber() {
        return channel.getChannelNumber();
    }

    @Override
    public void close() throws IOException, TimeoutException {
        channel.close();
    }

    @Override
    public void basicPublish(String s, String s1, byte[] bytes) throws IOException {
        channel.basicPublish(s, s1, new AMQP.BasicProperties(), bytes);
    }

    @Override
    public void exchangeDeclare(String s, ExchangeType exchangeType, boolean b, boolean b1,
                                boolean b2, Map<String, Object> map) throws IOException {
        channel.exchangeDeclare(s, BuiltinExchangeType.valueOf(exchangeType.name()), b, b1, b2, map);
    }

    @Override
    public void queueDeclare(String s, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
        channel.queueDeclare(s, b, b1, b2, map);
    }

    @Override
    public void queueBind(String s, String s1, String s2) throws IOException {
        channel.queueBind(s, s1, s2);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void basicQos(int i) throws IOException {
        channel.basicQos(i);
    }

    @Override
    public void basicConsume(String s, boolean b, UpdatePointValueConsumer updatePointValueConsumer) throws IOException {
        channel.basicConsume(s, b, new ScadaConsumer(channel, updatePointValueConsumer));
    }
}
