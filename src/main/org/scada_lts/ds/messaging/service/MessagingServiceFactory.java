package org.scada_lts.ds.messaging.service;

import org.scada_lts.ds.messaging.channel.InitMessagingChannels;
import org.scada_lts.ds.messaging.channel.MessagingChannels;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.client.MqttMessagingChannels;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpMessagingChannels;
import org.scada_lts.ds.messaging.protocol.amqp.client.impl.SyncAmqpConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

public final class MessagingServiceFactory {

    private MessagingServiceFactory() {}

    public static MessagingService newService(AmqpDataSourceVO vo) {
        MessagingChannels messagingChannels = MessagingChannels.nonSync(new ConcurrentHashMap<>(), vo.getConnectionTimeout());
        InitMessagingChannels initMessagingChannels = new AmqpMessagingChannels(vo, messagingChannels, new SyncAmqpConnectionManager());
        return new MessagingServiceImpl(vo, initMessagingChannels);
    }

    public static MessagingService newService(MqttDataSourceVO vo) {
        MessagingChannels messagingChannels = MessagingChannels.nonSync(new ConcurrentHashMap<>(), vo.getConnectionTimeout());
        InitMessagingChannels initMessagingChannels = new MqttMessagingChannels(vo, messagingChannels);
        return new MessagingServiceImpl(vo, initMessagingChannels);
    }
}
