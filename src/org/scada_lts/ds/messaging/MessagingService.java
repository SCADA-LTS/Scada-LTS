package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessagingService {

    boolean isOpen();
    void open() throws IOException, TimeoutException;
    void close() throws IOException;

    void publish(DataPointRT dataPoint, String message) throws IOException;
    void consume(DataPointRT dataPoint) throws IOException;
}
