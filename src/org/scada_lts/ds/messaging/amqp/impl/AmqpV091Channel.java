package org.scada_lts.ds.messaging.amqp.impl;

import com.rabbitmq.client.Channel;
import org.scada_lts.ds.messaging.MessagingChannel;

public class AmqpV091Channel implements MessagingChannel<Channel> {

    private final Channel channel;

    public AmqpV091Channel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close(int timeout) throws Exception {
        channel.close();
    }

    @Override
    public Channel toOrigin() {
        return channel;
    }
}
