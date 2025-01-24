package org.scada_lts.ds.polling.protocol.opcua.client;

import org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaMaster;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaDataSourceVO;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaPointLocatorVO;
import org.scada_lts.ds.polling.service.IMaster;

import java.util.Comparator;
import java.util.List;

public interface IOpcUaMaster extends IMaster, AutoCloseable {

    List<OpcUaPointLocatorVO> browse(OpcUaPointLocatorVO root, int searchDepth, Comparator<OpcUaPointLocatorVO> comparator);
    boolean validate(OpcUaPointLocatorVO dataPoint);

    static IOpcUaMaster newMaster(OpcUaDataSourceVO dataSource) {
        return new OpcUaMaster(dataSource);
    }
}
