package com.serotonin.mango.rt.dataSource.modbus;

import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.FixedModbusMaster;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ModbusStringTcpTest {
    @Test
    public void productionBatchMasterReadsSwappedStringsFromLoopbackDevice() throws Exception {
        for (int range : new int[] {RegisterRange.HOLDING_REGISTER, RegisterRange.INPUT_REGISTER}) {
            for (int type : new int[] {DataType.CHAR, DataType.VARCHAR})
                readBatch(range, type);
        }
    }

    private void readBatch(int range, int type) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ModbusMaster master = null;
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
            server.setSoTimeout(5000);
            Future<?> device = executor.submit(() -> {
                try (Socket socket = server.accept()) {
                    socket.setSoTimeout(5000);
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    int transaction = in.readUnsignedShort();
                    assertEquals(0, in.readUnsignedShort()); // Modbus protocol identifier
                    assertEquals(6, in.readUnsignedShort()); // unit + read request PDU
                    int unit = in.readUnsignedByte();
                    int function = in.readUnsignedByte();
                    assertEquals(range == RegisterRange.HOLDING_REGISTER ? 3 : 4, function);
                    assertEquals(4, in.readUnsignedShort());
                    assertEquals(4, in.readUnsignedShort());

                    out.writeShort(transaction);
                    out.writeShort(0);
                    out.writeShort(11); // unit + function + byte count + four registers
                    out.writeByte(unit);
                    out.writeByte(function);
                    out.writeByte(8);
                    for (int register : new int[] {0x1234, 0x6947, 0x6874, 0x6275})
                        out.writeShort(register);
                    out.flush();
                    return null;
                }
            });

            IpParameters ip = new IpParameters();
            ip.setHost(server.getInetAddress().getHostAddress());
            ip.setPort(server.getLocalPort());
            master = new FixedModbusMaster(new ModbusFactory().createTcpMaster(ip, false));
            master.setTimeout(3000);
            master.setRetries(0);
            master.init();

            ModbusPointLocatorVO vo = new ModbusPointLocatorVO();
            vo.setRange(range);
            vo.setModbusDataType(type);
            vo.setOffset(5);
            vo.setRegisterCount(3);
            vo.setSwapStringBytes(true);
            BatchRead<String> batch = new BatchRead<>();
            batch.setContiguousRequests(true);
            batch.addLocator("number", BaseLocator.createLocator(1, range, 4,
                    DataType.TWO_BYTE_INT_UNSIGNED, 0, 0));
            batch.addLocator("string", ModbusDataSource.createModbusLocator(vo));
            BatchResults<String> result = master.send(batch);
            assertEquals("Github", result.getValue("string"));
            assertEquals(0x1234, result.getValue("number"));
            device.get(5, TimeUnit.SECONDS);
        } finally {
            if (master != null)
                master.destroy();
            executor.shutdownNow();
        }
    }
}
