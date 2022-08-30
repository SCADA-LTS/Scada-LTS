package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

public interface MessagingService {

    boolean isOpen();
    void open() throws Exception;
    void close() throws Exception;

    void initReceiver(DataPointRT dataPoint) throws Exception;
    void removeReceiver(DataPointRT dataPoint) throws Exception;
    void publish(DataPointRT dataPoint, String message) throws Exception;
}
