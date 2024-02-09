package org.scada_lts.ds.messaging.service;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.exception.MessagingServiceException;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface MessagingService {

    boolean isOpen();
    boolean isOpen(DataPointRT dataPoint);
    void open() throws MessagingServiceException;
    void close() throws MessagingServiceException;

    void initReceiver(DataPointRT dataPoint, Consumer<Exception> updateExceptionHandler, Supplier<Void> returnToNormal) throws MessagingServiceException;
    void removeReceiver(DataPointRT dataPoint) throws MessagingServiceException;
    void publish(DataPointRT dataPoint, String message) throws MessagingServiceException;
}
