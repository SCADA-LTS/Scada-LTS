package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.util.Optional;
import java.util.function.Supplier;

public interface MessagingChannels<T> {

    void open() throws Exception;

    void removeChannel(DataPointRT dataPoint, int timeout) throws Exception;

    boolean isOpenChannel(DataPointRT dataPoint);

    Optional<MessagingChannel<T>> initChannel(DataPointRT dataPoint, Supplier<MessagingChannel<T>> create);

    Optional<MessagingChannel<T>> getChannel(DataPointRT dataPoint);

    boolean isOpen();

    void closeChannels(int timeout) throws Exception;

    int size();
}
