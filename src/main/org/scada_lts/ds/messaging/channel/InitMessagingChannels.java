package org.scada_lts.ds.messaging.channel;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.function.Consumer;

public interface InitMessagingChannels extends MessagingChannels {
    void initChannel(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws MessagingChannelException;
}
