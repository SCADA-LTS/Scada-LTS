package org.scada_lts.ds.polling.protocol.opcua.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.web.beans.ApplicationBeans;

import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({ApplicationBeans.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class ValidateOpcUaDataTypeTest {

    @Parameterized.Parameters(name= "{index}: opcDataType: {0}, value: {1}, expected: {2}, type: {3}")
    public static Object[][] data() {

        Object[][] tests = new Object[][] {

                {OpcUaBaseDataType.UNUMBER, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Double.MAX_VALUE, true, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Float.MAX_VALUE, true, Float.class.getSimpleName()},

                {OpcUaBaseDataType.UNUMBER, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Long.MAX_VALUE, true, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, ULong.MAX_VALUE, true, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.UNUMBER, Byte.MIN_VALUE, false, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.UNUMBER, Short.MIN_VALUE, false, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, UShort.MAX_VALUE, true, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.UNUMBER, Integer.MIN_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Integer.MAX_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, UInteger.MAX_VALUE, true, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.UNUMBER, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.UNUMBER, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.SBYTE, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.SBYTE, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Long.MAX_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, ULong.MAX_VALUE, false, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.SBYTE, Byte.MIN_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, UByte.MAX_VALUE, false, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.SBYTE, Short.MIN_VALUE, false, Short.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Short.MAX_VALUE, false, Short.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, UShort.MAX_VALUE, false, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.SBYTE, Integer.MIN_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Integer.MAX_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, UInteger.MAX_VALUE, false, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.SBYTE, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.SBYTE, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.INT16, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.INT16, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Long.MAX_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, ULong.MAX_VALUE, false, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.INT16, Byte.MIN_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.INT16, Short.MIN_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, UShort.MAX_VALUE, false, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.INT16, Integer.MIN_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Integer.MAX_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, UInteger.MAX_VALUE, false, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.INT16, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.INT16, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.INT32, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.INT32, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Long.MAX_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, ULong.MAX_VALUE, false, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.INT32, Byte.MIN_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.INT32, Short.MIN_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, UShort.MAX_VALUE, true, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.INT32, Integer.MIN_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Integer.MAX_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, UInteger.MAX_VALUE, false, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.INT32, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.INT32, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.INT64, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.INT64, Long.MIN_VALUE, true, Long.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Long.MAX_VALUE, true, Long.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, ULong.MAX_VALUE, false, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.INT64, Byte.MIN_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.INT64, Short.MIN_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, UShort.MAX_VALUE, true, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.INT64, Integer.MIN_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Integer.MAX_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, UInteger.MAX_VALUE, true, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.INT64, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.INT64, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.UINT16, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.UINT16, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Long.MAX_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, ULong.MAX_VALUE, false, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.UINT16, Byte.MIN_VALUE, false, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.UINT16, Short.MIN_VALUE, false, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, UShort.MAX_VALUE, true, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.UINT16, Integer.MIN_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Integer.MAX_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, UInteger.MAX_VALUE, false, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.UINT16, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.UINT16, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.UINT32, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.UINT32, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Long.MAX_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, ULong.MAX_VALUE, false, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.UINT32, Byte.MIN_VALUE, false, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.UINT32, Short.MIN_VALUE, false, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, UShort.MAX_VALUE, true, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.UINT32, Integer.MIN_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Integer.MAX_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, UInteger.MAX_VALUE, true, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.UINT32, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.UINT32, Boolean.TRUE, false, Boolean.class.getSimpleName()},

                ///

                {OpcUaBaseDataType.UINT64, -Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Double.MAX_VALUE, false, Double.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, -Float.MAX_VALUE, false, Float.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Float.MAX_VALUE, false, Float.class.getSimpleName()},

                {OpcUaBaseDataType.UINT64, Long.MIN_VALUE, false, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Long.MAX_VALUE, true, Long.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, ULong.MIN_VALUE, true, ULong.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, ULong.MAX_VALUE, true, ULong.class.getSimpleName()},

                {OpcUaBaseDataType.UINT64, Byte.MIN_VALUE, false, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Byte.MAX_VALUE, true, Byte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, UByte.MIN_VALUE, true, UByte.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, UByte.MAX_VALUE, true, UByte.class.getSimpleName()},

                {OpcUaBaseDataType.UINT64, Short.MIN_VALUE, false, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Short.MAX_VALUE, true, Short.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, UShort.MIN_VALUE, true, UShort.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, UShort.MAX_VALUE, true, UShort.class.getSimpleName()},

                {OpcUaBaseDataType.UINT64, Integer.MIN_VALUE, false, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Integer.MAX_VALUE, true, Integer.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, UInteger.MIN_VALUE, true, UInteger.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, UInteger.MAX_VALUE, true, UInteger.class.getSimpleName()},

                {OpcUaBaseDataType.UINT64, "abc", false, String.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Boolean.FALSE, false, Boolean.class.getSimpleName()},
                {OpcUaBaseDataType.UINT64, Boolean.TRUE, false, Boolean.class.getSimpleName()},
        };
        return tests;
    }

    private final OpcUaDataType opcUaDataType;
    private final boolean expected;
    private final Object value;

    public ValidateOpcUaDataTypeTest(OpcUaDataType opcUaDataType, Object value,
                                     boolean expected, String className) {
        this.value = value;
        this.opcUaDataType = opcUaDataType;
        this.expected = expected;
    }

    @Before
    public void config() {
        mockStatic(ApplicationBeans.class);
        ObjectMapper objectMapper = new ObjectMapper();
        when(ApplicationBeans.getBean(eq("objectMapper"), eq(ObjectMapper.class))).thenReturn(objectMapper);
    }

    @Test
    public void when_validate() throws Exception {

        //given:
        MangoValue mangoValue;
        try {
            mangoValue = opcUaDataType.convertToRead(value);
        } catch (Exception ex) {
            mangoValue = new AlphanumericValue(String.valueOf(value));
        }

        //when
        boolean result = opcUaDataType.validate(mangoValue);

        //then:
        Assert.assertEquals(expected, result);
    }
}