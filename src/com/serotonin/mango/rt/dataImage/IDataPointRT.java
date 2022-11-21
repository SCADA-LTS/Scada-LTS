package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.util.timeout.TimeoutClient;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.ILifecycle;
import org.scada_lts.web.ws.ScadaWebSocket;

import java.util.Map;


public interface IDataPointRT extends IDataPoint, ILifecycle, TimeoutClient,
        ScadaWebSocket<String>, IDataPointCache {
    int getId();
    int getDataSourceId();
    void setAttribute(String key, Object value);
    Object getAttribute(String key);
    Map<String, Object> getAttributes();
    void initializeHistorical();
    void terminateHistorical();
    <T extends PointLocatorRT> T getPointLocator();
    DataPointVO getVO();

    @Override
    void initialize();
    @Override
    void terminate();
}
