package org.scada_lts.ds.messaging.channel;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface InitMessagingChannels extends MessagingChannels {
    void initChannel(DataPointRT dataPoint, Consumer<Throwable> exceptionHandler, Supplier<Void> returnToNormal) throws MessagingChannelException;
}
