package org.scada_lts.ds.polling.protocol.opcua.vo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IsPossibleSettableOpcUaDataTypeTest {

    @Parameterized.Parameters(name= "{index}: opcDataType: {0}, expected: {1}")
    public static Object[][] data() {
        return new Object[][] {
            {OpcUaBaseDataType.UNKNOWN, false},
            {OpcUaBaseDataType.ALL, false},

            {OpcUaBaseDataType.UNUMBER, false},
            {OpcUaBaseDataType.NUMBER, false},

            {OpcUaBaseDataType.STRING, true},
            {OpcUaBaseDataType.BOOLEAN, true},

            {OpcUaBaseDataType.SBYTE, true},
            {OpcUaBaseDataType.BYTE, true},

            {OpcUaBaseDataType.FLOAT, true},
            {OpcUaBaseDataType.DOUBLE, true},

            {OpcUaBaseDataType.INT16, true},
            {OpcUaBaseDataType.UINT16, true},

            {OpcUaBaseDataType.INT32, true},
            {OpcUaBaseDataType.UINT32, true},

            {OpcUaBaseDataType.INT64, true},
            {OpcUaBaseDataType.UINT64, true},

            {OpcUaBaseDataType.DATE_TIME, true},
            {OpcUaBaseDataType.GUID, true},

            {OpcUaBaseDataType.BYTE_STRING, false},
            {OpcUaBaseDataType.DATA_VALUE, false},

            {OpcUaBaseDataType.DIAGNOSTIC_INFO, false},
            {OpcUaBaseDataType.EXPANDED_NODE_ID, false},

            {OpcUaBaseDataType.NODE_ID, false},
            {OpcUaBaseDataType.QUALIFIED_NAME, false},

            {OpcUaBaseDataType.STATUS_CODE, false},
            {OpcUaBaseDataType.EXTENSION_OBJECT, false},

            {OpcUaBaseDataType.VARIANT, false},
            {OpcUaBaseDataType.XML_ELEMENT, false},

            {OpcUaBaseDataType.LOCALIZED_TEXT, false},

        };
    }

    private final OpcUaDataType opcUaDataType;
    private final boolean expected;

    public IsPossibleSettableOpcUaDataTypeTest(OpcUaDataType opcUaDataType, boolean expected) {
        this.opcUaDataType = opcUaDataType;
        this.expected = expected;
    }

    @Test
    public void when_isPossibleSettable() throws Exception {

        //when
        boolean result = opcUaDataType.isPossibleSettable();

        //then:
        Assert.assertEquals(expected, result);
    }
}