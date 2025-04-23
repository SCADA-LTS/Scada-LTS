package org.scada_lts.ds.polling.protocol.opcua.client;

import org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaService;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaDataSourceVO;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaPointLocatorVO;
import org.scada_lts.ds.polling.service.PollingService;

import java.util.Comparator;
import java.util.List;

public interface IOpcUaService extends PollingService {

    List<OpcUaPointLocatorVO> browse(OpcUaPointLocatorVO root, int searchDepth, Comparator<OpcUaPointLocatorVO> comparator);
    boolean validate(OpcUaPointLocatorVO dataPoint);

    static IOpcUaService newService(OpcUaDataSourceVO dataSource) {
        return new OpcUaService(dataSource);
    }
}
