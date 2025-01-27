package org.scada_lts.ds.polling.protocol.opcua.vo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        IsPossibleSettableOpcUaDataTypeTest.class,
        ValidateOpcUaDataTypeTest.class,
        ValidateOpcUaDataType1Test.class,
})
public class OpcUaDataTypeTestsSuite {
}
