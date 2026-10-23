package com.serotonin.mango.rt.dataSource.modbus;

import com.serotonin.json.JsonReader;
import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.locator.StringLocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ModbusStringByteOrderTest {
    @Parameterized.Parameters(name = "type={0}, range={1}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {DataType.CHAR, RegisterRange.HOLDING_REGISTER},
                {DataType.VARCHAR, RegisterRange.HOLDING_REGISTER},
                {DataType.CHAR, RegisterRange.INPUT_REGISTER},
                {DataType.VARCHAR, RegisterRange.INPUT_REGISTER}
        });
    }

    private final int dataType;
    private final int range;

    public ModbusStringByteOrderTest(int dataType, int range) {
        this.dataType = dataType;
        this.range = range;
    }

    @Test
    public void swapsRegisterBytesBeforeDecoding() throws Exception {
        String json = "{\"range\":\"" + (range == RegisterRange.HOLDING_REGISTER
                ? "HOLDING_REGISTER" : "INPUT_REGISTER") + "\",\"modbusDataType\":\""
                + (dataType == DataType.CHAR ? "CHAR" : "VARCHAR")
                + "\",\"registerCount\":3,\"charset\":\"ASCII\",\"swapStringBytes\":true}";
        ModbusPointLocatorVO vo = new JsonReader(json).read(ModbusPointLocatorVO.class);

        // Modbus transmits the high byte of each 16-bit register first.
        byte[] response = registers(0x6947, 0x6874, 0x6275);
        assertEquals("Github", ModbusDataSource.createModbusLocator(vo).bytesToValue(response, 0));
        assertEquals("Githbu", ModbusDataSource.createModbusLocator(vo)
                .bytesToValue(registers(0x6947, 0x6874, 0x7562), 0));
    }

    @Test
    public void defaultUsesOriginalLocatorAndByteOrder() {
        ModbusPointLocatorVO vo = point(false, 3, "ASCII");
        BaseLocator<?> locator = ModbusDataSource.createModbusLocator(vo);
        assertEquals(StringLocator.class, locator.getClass());
        assertEquals("Github", locator.bytesToValue(registers(0x4769, 0x7468, 0x7562), 0));
        assertEquals("iGhtbu", locator.bytesToValue(registers(0x6947, 0x6874, 0x6275), 0));
    }

    @Test
    public void swapsOnlyThisPointsRegistersInSharedBatchResponse() {
        ModbusPointLocatorVO vo = point(true, 3, "ASCII");
        vo.setOffset(7);
        BaseLocator<?> locator = ModbusDataSource.createModbusLocator(vo);
        byte[] response = registers(0x1234, 0xabcd, 0x6947, 0x6874, 0x6275, 0xdead);
        byte[] original = response.clone();
        assertEquals("Github", locator.bytesToValue(response, 5));
        assertArrayEquals(original, response);
        assertEquals(7, locator.getOffset());
        assertEquals(9, locator.getEndOffset());
    }

    @Test
    public void preservesPaddingAndFindsNullAfterSwappingAtBatchOffset() {
        ModbusPointLocatorVO vo = point(true, 3, "ASCII");
        vo.setOffset(11);
        BaseLocator<?> locator = ModbusDataSource.createModbusLocator(vo);
        assertEquals(dataType == DataType.CHAR ? "ABC\0XY" : "ABC",
                locator.bytesToValue(registers(0xffff, 0x4241, 0x0043, 0x5958), 10));
        assertEquals("ABC   ", locator.bytesToValue(registers(0xffff, 0x4241, 0x2043, 0x2020), 10));
        assertEquals(dataType == DataType.CHAR ? "\0\0\0\0\0\0" : "",
                locator.bytesToValue(registers(0xffff, 0, 0, 0), 10));
        assertEquals(dataType == DataType.CHAR ? "ABCDE\0" : "ABCDE",
                locator.bytesToValue(registers(0xffff, 0x4241, 0x4443, 0x0045), 10));
    }

    @Test
    public void decodesUtf8BeforeInterpretingCharacters() {
        BaseLocator<?> locator = ModbusDataSource.createModbusLocator(point(true, 3, "UTF-8"));
        assertEquals(dataType == DataType.CHAR ? "\u00e9\u4e2d\0" : "\u00e9\u4e2d",
                locator.bytesToValue(registers(0xa9c3, 0xb8e4, 0x00ad), 0));
        // The three bytes of the euro sign straddle a register boundary.
        locator = ModbusDataSource.createModbusLocator(point(true, 2, "UTF-8"));
        assertEquals("A\u20ac", locator.bytesToValue(registers(0xe241, 0xac82), 0));
    }

    @Test
    public void preservesCharsetReplacementAndByteTerminatorSemantics() {
        BaseLocator<?> locator = ModbusDataSource.createModbusLocator(point(true, 1, "UTF-8"));
        assertEquals("A\ufffd", locator.bytesToValue(registers(0xc341), 0));
        locator = ModbusDataSource.createModbusLocator(point(true, 1, "ASCII"));
        assertEquals("A\ufffd", locator.bytesToValue(registers(0xff41), 0));
        locator = ModbusDataSource.createModbusLocator(point(true, 1, "ISO-8859-1"));
        assertEquals("A\u00e9", locator.bytesToValue(registers(0xe941), 0));

        // VARCHAR already terminates at a zero BYTE, including in multibyte encodings.
        locator = ModbusDataSource.createModbusLocator(point(true, 2, "UTF-16BE"));
        assertEquals(dataType == DataType.CHAR ? "AB" : "",
                locator.bytesToValue(registers(0x4100, 0x4200), 0));
    }

    @Test
    public void readsFullModbusRegisterLimitWithoutTerminator() {
        int[] values = new int[125];
        Arrays.fill(values, 0x4241);
        BaseLocator<?> locator = ModbusDataSource.createModbusLocator(point(true, 125, "ASCII"));
        assertEquals("AB".repeat(125), locator.bytesToValue(registers(values), 0));
        assertEquals(125, locator.getRegisterCount());
    }

    @Test
    public void writesSwappedRegistersWithOriginalPaddingAndTruncation() {
        StringLocator locator = (StringLocator) ModbusDataSource.createModbusLocator(point(true, 2, "ASCII"));
        assertArrayEquals(new short[] {0x4241, (short) (dataType == DataType.CHAR ? 0x2043 : 0x0043)},
                locator.valueToShorts("ABC"));
        assertEquals(dataType == DataType.CHAR ? "ABC " : "ABC",
                locator.bytesToValue(registersFromShorts(locator.valueToShorts("ABC")), 0));
        assertArrayEquals(dataType == DataType.CHAR ? new short[] {0x2020, 0x2020} : new short[] {0, 0},
                locator.valueToShorts(""));
        assertArrayEquals(locator.valueToShorts(""), locator.valueToShorts(null));
        assertArrayEquals(new short[] {0x4241, (short) (dataType == DataType.CHAR ? 0x4443 : 0x0043)},
                locator.valueToShorts("ABCDE"));
    }

    @Test
    public void writesUtf8BytesAcrossRegisters() {
        StringLocator locator = (StringLocator) ModbusDataSource.createModbusLocator(point(true, 3, "UTF-8"));
        assertArrayEquals(new short[] {(short) 0xe241, (short) 0xac82,
                        (short) (dataType == DataType.CHAR ? 0x2020 : 0)},
                locator.valueToShorts("A\u20ac"));
    }

    @Test
    public void leavesNumericAndBinaryTypesUnchangedEvenWithFlagSet() {
        ModbusPointLocatorVO vo = point(true, 3, "ASCII");
        vo.setModbusDataType(DataType.TWO_BYTE_INT_UNSIGNED);
        assertEquals(0x1234, ModbusDataSource.createModbusLocator(vo).bytesToValue(registers(0x1234), 0));
        vo.setModbusDataType(DataType.BINARY);
        vo.setBit((byte) 3);
        assertEquals(true, ModbusDataSource.createModbusLocator(vo).bytesToValue(registers(0x0008), 0));
    }

    private ModbusPointLocatorVO point(boolean swapped, int count, String charset) {
        ModbusPointLocatorVO vo = new ModbusPointLocatorVO();
        vo.setRange(range);
        vo.setModbusDataType(dataType);
        vo.setRegisterCount(count);
        vo.setCharset(charset);
        vo.setSwapStringBytes(swapped);
        return vo;
    }

    private static byte[] registersFromShorts(short[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(values.length * 2);
        for (short value : values)
            buffer.putShort(value);
        return buffer.array();
    }

    private static byte[] registers(int... values) {
        ByteBuffer buffer = ByteBuffer.allocate(values.length * 2);
        for (int value : values)
            buffer.putShort((short) value);
        return buffer.array();
    }
}
