package org.scada_lts.ds.messaging.protocol.amqp.client;


import com.rabbitmq.client.Consumer;
import org.scada_lts.ds.messaging.channel.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.protocol.amqp.ExchangeType;
import org.scada_lts.ds.messaging.protocol.amqp.client.impl.ScadaConsumer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface AmqpChannel {
    int getChannelNumber();
    void close() throws IOException, TimeoutException;
    void basicPublish(String s, String s1, byte[] bytes) throws IOException;
    void exchangeDeclare(String s, ExchangeType exchangeType, boolean b, boolean b1, boolean b2,
                         Map<String, Object> map) throws IOException;
    void queueDeclare(String s, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException;
    void queueBind(String s, String s1, String s2) throws IOException;
    String basicConsume(String s, boolean b, Consumer consumer) throws IOException;
    void basicQos(int i) throws IOException;
    ScadaConsumer consumer(UpdatePointValueConsumer updatePointValueConsumer);
    boolean isOpen();
}
