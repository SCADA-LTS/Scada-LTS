package org.scada_lts.ds.polling.service;

import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.ds.polling.exception.MasterException;

import java.util.List;

public interface IMaster {

    void init() throws MasterException;
    void ping() throws MasterException;
    DataPointReadResponse read(List<DataPointVO> dataPoints, long time) throws MasterException;
    void write(DataPointVO dataPoint, Object value) throws MasterException;
    void terminate() throws MasterException;

    String getName();
}
