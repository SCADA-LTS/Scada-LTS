package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

public interface MessagingService {

    String ATTR_UPDATE_ERROR_KEY = "DP_UPDATE_ERROR";

    boolean isOpen();
    void open() throws Exception;
    void close() throws Exception;

    void initReceiver(DataPointRT dataPoint) throws Exception;
    void removeReceiver(DataPointRT dataPoint) throws Exception;
    void publish(DataPointRT dataPoint, String message) throws Exception;
}
