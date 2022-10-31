package org.scada_lts.ds.messaging.service;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.exception.MessagingServiceException;

import java.util.function.Consumer;

public interface MessagingService {

    boolean isOpen();
    boolean isOpen(DataPointRT dataPoint);
    void open() throws MessagingServiceException;
    void close() throws MessagingServiceException;

    void initReceiver(DataPointRT dataPoint, Consumer<Exception> updateExceptionHandler, String updateErrorKey) throws MessagingServiceException;
    void removeReceiver(DataPointRT dataPoint) throws MessagingServiceException;
    void publish(DataPointRT dataPoint, String message) throws MessagingServiceException;
}
