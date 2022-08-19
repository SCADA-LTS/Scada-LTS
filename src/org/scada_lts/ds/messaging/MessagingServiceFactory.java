package org.scada_lts.ds.messaging;

import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.amqp.AmqpMessagingService;

public final class MessagingServiceFactory {

    private MessagingServiceFactory() {}

    public static MessagingService amqp(AmqpDataSourceVO vo) {
        return new AmqpMessagingService(vo);
    }
}
