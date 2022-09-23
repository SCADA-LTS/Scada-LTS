package org.scada_lts.ds.messaging.protocol.amqp.client.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.scada_lts.ds.messaging.channel.UpdatePointValueConsumer;

import java.io.IOException;

public class ScadaConsumer extends DefaultConsumer {

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
