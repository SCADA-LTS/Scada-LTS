package org.scada_lts.cache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.scada_lts.ds.polling.protocol.opcua.vo.IsPossibleSettableOpcUaDataTypeTest;
import org.scada_lts.ds.polling.protocol.opcua.vo.ValidateOpcUaDataType1Test;
import org.scada_lts.ds.polling.protocol.opcua.vo.ValidateOpcUaDataTypeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DeletePointHierarchyCacheTest.class,
        OnBaseParentIdPointHierarchyCacheTest.class,
        PointHierarchyCacheTest.class,
})
public class PointHierarchyCacheTestsSuite {
}
