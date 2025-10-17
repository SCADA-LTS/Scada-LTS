package org.scada_lts.ds.messaging.channel;

import net.bull.javamelody.internal.common.LOG;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.serotonin.mango.util.LoggingUtils.exceptionInfo;

class NonSyncOperationChannels implements OperationChannels {

    private final Map<Integer, MessagingChannel> channels;

    NonSyncOperationChannels(Map<Integer, MessagingChannel> initChannels) {
        this.channels = initChannels;
    }

    @Override
    public void closeChannels(int timeout) throws MessagingChannelException {
        Map<Integer, MessagingChannel> chs = new HashMap<>(this.channels);
        for(MessagingChannel channel: chs.values()) {
            try {
                channel.close(timeout);
            } catch (Throwable ex) {
                LOG.warn("Error Init Channel: " + channel.getClass().getName() + ", " + exceptionInfo(ex), ex);
            }
        }
        chs.clear();
        this.channels.clear();
    }
    @Override
    public Map<Integer, MessagingChannel> getChannels() {
        return channels;
    }
    @Override
    public void removeChannel(int dataPointId) {
        channels.remove(dataPointId);
    }
    @Override
    public MessagingChannel createChannelIfNotExists(int dataPointId, Function<? super Integer, ? extends MessagingChannel> mappingFunction) {
        return channels.computeIfAbsent(dataPointId, mappingFunction);
    }

    @Override
    public String toString() {
        return "NonSyncOperationChannels{" +
                "channels=" + channels.getClass().getSimpleName() +
                '}';
    }
}
