package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessagingService {

    boolean isOpened();
    void open() throws IOException, TimeoutException;
    void close() throws IOException, TimeoutException;

    void publish(DataPointRT dataPoint, String message) throws IOException;
    void consume(DataPointRT dataPoint) throws IOException;
}
