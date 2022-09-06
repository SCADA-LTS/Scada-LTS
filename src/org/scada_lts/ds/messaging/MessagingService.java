package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.util.function.Consumer;

public interface MessagingService {

    boolean isOpen();
    boolean isOpen(DataPointRT dataPoint);
    void open() throws Exception;
    void close() throws Exception;

    void initReceiver(DataPointRT dataPoint, Consumer<Exception> updateExceptionHandler, String updateErrorKey) throws Exception;
    void removeReceiver(DataPointRT dataPoint) throws Exception;
    void publish(DataPointRT dataPoint, String message) throws Exception;
}
