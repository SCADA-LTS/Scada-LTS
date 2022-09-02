package org.scada_lts.ds.messaging;

import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.amqp.impl.AmqpV091MessagingService;

public final class MessagingServiceFactory {

    private MessagingServiceFactory() {}

    public static MessagingService newService(AmqpDataSourceVO vo) {
        return new AmqpV091MessagingService(vo);
    }
}
