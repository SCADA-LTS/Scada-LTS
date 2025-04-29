package org.scada_lts.ds.polling.service;

import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.ds.polling.exception.PollingServiceException;

import java.util.List;

public interface PollingService extends AutoCloseable {

    void initialize() throws PollingServiceException;
    void ping() throws PollingServiceException;
    DataPointReadResponse read(List<DataPointVO> dataPoints, long time) throws PollingServiceException;
    void write(DataPointVO dataPoint, Object value) throws PollingServiceException;
    void terminate() throws PollingServiceException;

    @Override
    default void close() throws PollingServiceException {
        terminate();
    }

    String getName();
}
