package org.scada_lts.ds.messaging;

import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.amqp.impl.AmqpV091MessagingService;
import org.scada_lts.ds.messaging.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.mqtt.impl.MqttMessagingService;

public final class MessagingServiceFactory {

    private MessagingServiceFactory() {}

    public static MessagingService newService(AmqpDataSourceVO vo) {
        return new AmqpV091MessagingService(vo);
    }

    public static MessagingService newService(MqttDataSourceVO vo) {
        return new MqttMessagingService(vo);
    }
}
