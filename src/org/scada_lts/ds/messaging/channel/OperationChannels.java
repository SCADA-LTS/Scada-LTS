package org.scada_lts.ds.messaging.channel;

import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.Map;
import java.util.function.Function;

public interface OperationChannels {
    void closeChannels(int timeout) throws MessagingChannelException;
    Map<Integer, MessagingChannel> getChannels();
    void removeChannel(int dataPointId);
    MessagingChannel createChannelIfNotExists(int dataPointId, Function<? super Integer, ? extends MessagingChannel> mappingFunction);
}
