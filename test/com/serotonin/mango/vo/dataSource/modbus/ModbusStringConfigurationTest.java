package com.serotonin.mango.vo.dataSource.modbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.modbus.ModbusDataSource;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.SerializationData;
import org.scada_lts.web.mvc.api.datasources.modbusip.ModbusIpPointLocatorJson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ModbusStringConfigurationTest {
    @Parameterized.Parameters(name = "type={0}, swapped={1}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {DataType.CHAR, false}, {DataType.CHAR, true},
                {DataType.VARCHAR, false}, {DataType.VARCHAR, true}
        });
    }

    private final int dataType;
    private final boolean swapped;

    public ModbusStringConfigurationTest(int dataType, boolean swapped) {
        this.dataType = dataType;
        this.swapped = swapped;
    }

    @Test
    public void exportImportPreservesConfigurationAndDecoding() throws Exception {
        ModbusPointLocatorVO original = point();
        String json = new JsonWriter().write(original);
        assertTrue(json.contains("\"swapStringBytes\":" + swapped));
        assertTrue(json.contains("\"modbusDataType\":\"" + typeCode() + "\""));
        assertConfiguration(new JsonReader(json).read(ModbusPointLocatorVO.class));
    }

    @Test
    public void oldJsonKeepsOriginalByteOrder() throws Exception {
        ModbusPointLocatorVO restored = new JsonReader("{\"range\":\"HOLDING_REGISTER\","
                + "\"modbusDataType\":\"" + typeCode() + "\",\"registerCount\":3,\"charset\":\"ASCII\"}")
                .read(ModbusPointLocatorVO.class);
        assertFalse(restored.isSwapStringBytes());
        assertEquals("iGhtbu", decode(restored));
    }

    @Test
    public void databaseBlobRoundTripPreservesConfigurationAndDecoding() {
        DataPointVO point = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE, Common.TimePeriods.DAYS, 1);
        point.setPointLocator(point());
        SerializationData serialization = new SerializationData();
        DataPointVO restored = (DataPointVO) serialization.readObject(serialization.writeObject(point));
        assertConfiguration(restored.getPointLocator());
    }

    @Test
    public void readsGenuineVersion6BlobWithOriginalDefaults() throws Exception {
        Properties fixtures = new Properties();
        try (InputStream stream = getClass().getResourceAsStream("modbus-v6.properties")) {
            assertNotNull(stream);
            fixtures.load(stream);
        }
        ModbusPointLocatorVO restored = (ModbusPointLocatorVO) new SerializationData().readObject(
                new ByteArrayInputStream(Base64.getDecoder().decode(fixtures.getProperty(typeCode()))));
        assertFalse(restored.isSwapStringBytes());
        assertEquals(dataType, restored.getModbusDataType());
        assertEquals(7, restored.getOffset());
        assertEquals(3, restored.getRegisterCount());
        assertEquals("ASCII", restored.getCharset());
        assertEquals(2.5, restored.getMultiplier(), 0);
        assertEquals(1.25, restored.getAdditive(), 0);
        assertEquals("iGhtbu", decode(restored));
    }

    @Test
    public void restEditorRoundTripPreservesConfigurationAndDecoding() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new ModbusIpPointLocatorJson(point()));
        ModbusIpPointLocatorJson restored = mapper.readValue(json, ModbusIpPointLocatorJson.class);
        assertConfiguration(restored.parsePointLocatorData());
    }

    @Test
    public void oldRestPayloadDefaultsToOriginalByteOrder() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ModbusIpPointLocatorJson restored = mapper.readValue("{\"dataSourceTypeId\":\"3\",\"range\":3,\"modbusDataType\":"
                + dataType + ",\"registerCount\":3,\"charset\":\"ASCII\"}", ModbusIpPointLocatorJson.class);
        assertFalse(restored.isSwapStringBytes());
        assertEquals("iGhtbu", decode(restored.parsePointLocatorData()));
    }

    private ModbusPointLocatorVO point() {
        ModbusPointLocatorVO vo = new ModbusPointLocatorVO();
        vo.setRange(RegisterRange.HOLDING_REGISTER);
        vo.setModbusDataType(dataType);
        vo.setRegisterCount(3);
        vo.setSwapStringBytes(swapped);
        return vo;
    }

    private void assertConfiguration(ModbusPointLocatorVO restored) {
        assertEquals(swapped, restored.isSwapStringBytes());
        assertEquals(dataType, restored.getModbusDataType());
        assertEquals(RegisterRange.HOLDING_REGISTER, restored.getRange());
        assertEquals(3, restored.getRegisterCount());
        assertEquals("ASCII", restored.getCharset());
        assertEquals(swapped ? "Github" : "iGhtbu", decode(restored));
    }

    private String typeCode() {
        return dataType == DataType.CHAR ? "CHAR" : "VARCHAR";
    }

    private static Object decode(ModbusPointLocatorVO vo) {
        return ModbusDataSource.createModbusLocator(vo)
                .bytesToValue(new byte[] {0x69, 0x47, 0x68, 0x74, 0x62, 0x75}, vo.getOffset());
    }
}
