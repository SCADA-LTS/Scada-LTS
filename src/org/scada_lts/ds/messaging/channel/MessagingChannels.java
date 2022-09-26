package org.scada_lts.ds.messaging.channel;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.Map;
import java.util.function.Supplier;

public interface MessagingChannels {

    default void openConnection() throws MessagingChannelException {}

    boolean isOpenConnection();

    void removeChannel(DataPointRT dataPoint) throws MessagingChannelException;

    boolean isOpenChannel(DataPointRT dataPoint);

    void initChannel(DataPointRT dataPoint, Supplier<MessagingChannel> create) throws MessagingChannelException;

    void closeChannels() throws MessagingChannelException;

    void publish(DataPointRT dataPoint, String message) throws MessagingChannelException;

    int size();

    static MessagingChannels sync(Map<Integer, MessagingChannel> initChannels, int timeout) {
        return new MessagingChannelsImpl(new SyncOperationChannels(initChannels), timeout);
    }

    static MessagingChannels nonSync(Map<Integer, MessagingChannel> initChannels, int timeout) {
        return new MessagingChannelsImpl(new NonSyncOperationChannels(initChannels), timeout);
    }
}
