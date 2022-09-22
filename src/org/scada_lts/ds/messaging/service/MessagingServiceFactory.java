package org.scada_lts.ds.messaging.service;

import org.scada_lts.ds.messaging.channel.InitMessagingChannels;
import org.scada_lts.ds.messaging.channel.MessagingChannels;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.impl.MqttMessagingChannels;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.amqp.impl.AmqpV091MessagingChannels;

import java.util.concurrent.ConcurrentHashMap;

public final class MessagingServiceFactory {

    private MessagingServiceFactory() {}

    public static MessagingService newService(AmqpDataSourceVO vo) {
        MessagingChannels messagingChannels = MessagingChannels.nonSync(new ConcurrentHashMap<>(), vo.getConnectionTimeout());
        InitMessagingChannels initMessagingChannels = new AmqpV091MessagingChannels(vo, messagingChannels);
        return new MessagingServiceImpl(vo, initMessagingChannels);
    }

    public static MessagingService newService(MqttDataSourceVO vo) {
        MessagingChannels messagingChannels = MessagingChannels.nonSync(new ConcurrentHashMap<>(), vo.getConnectionTimeout());
        InitMessagingChannels initMessagingChannels = new MqttMessagingChannels(vo, messagingChannels);
        return new MessagingServiceImpl(vo, initMessagingChannels);
    }
}
