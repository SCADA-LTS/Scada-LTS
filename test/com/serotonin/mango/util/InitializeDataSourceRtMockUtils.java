package com.serotonin.mango.util;

import br.org.scadabr.RealOPCMaster;
import br.org.scadabr.rt.dataSource.alpha2.Alpha2DataSource;
import br.org.scadabr.rt.dataSource.asciiSerial.ASCIISerialDataSource;
import br.org.scadabr.rt.dataSource.dnp3.DNP3Master;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3DataSource;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3IpDataSource;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3SerialDataSource;
import br.org.scadabr.rt.dataSource.drStorageHt5b.DrStorageHt5bDataSource;
import br.org.scadabr.rt.dataSource.iec101.IEC101DataSource;
import br.org.scadabr.rt.dataSource.iec101.IEC101EthernetDataSource;
import br.org.scadabr.rt.dataSource.iec101.IEC101Master;
import br.org.scadabr.rt.dataSource.iec101.IEC101SerialDataSource;
import br.org.scadabr.rt.dataSource.opc.OPCDataSource;
import br.org.scadabr.vo.dataSource.alpha2.Alpha2DataSourceVO;
import br.org.scadabr.vo.dataSource.asciiSerial.ASCIISerialDataSourceVO;
import br.org.scadabr.vo.dataSource.dnp3.Dnp3IpDataSourceVO;
import br.org.scadabr.vo.dataSource.dnp3.Dnp3SerialDataSourceVO;
import br.org.scadabr.vo.dataSource.drStorageHt5b.DrStorageHt5bDataSourceVO;
import br.org.scadabr.vo.dataSource.iec101.IEC101EthernetDataSourceVO;
import br.org.scadabr.vo.dataSource.iec101.IEC101SerialDataSourceVO;
import br.org.scadabr.vo.dataSource.opc.OPCDataSourceVO;
import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoEventDataSource;
import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoPollingDataSource;
import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoDataSourceVO;
import com.i2msolucoes.alpha24j.layer.user.Alpha2Master;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.bacnet.BACnetIPDataSourceRT;
import com.serotonin.mango.rt.dataSource.ebro.EBI25Constants;
import com.serotonin.mango.rt.dataSource.ebro.EBI25DataSourceRT;
import com.serotonin.mango.rt.dataSource.galil.GalilDataSourceRT;
import com.serotonin.mango.rt.dataSource.galil.GalilRequest;
import com.serotonin.mango.rt.dataSource.galil.GalilResponse;
import com.serotonin.mango.rt.dataSource.modbus.ModbusDataSource;
import com.serotonin.mango.rt.dataSource.modbus.ModbusIpDataSource;
import com.serotonin.mango.rt.dataSource.modbus.ModbusSerialDataSource;
import com.serotonin.mango.rt.dataSource.nmea.NmeaDataSourceRT;
import com.serotonin.mango.rt.dataSource.nmea.NmeaReceiver;
import com.serotonin.mango.rt.dataSource.onewire.Network;
import com.serotonin.mango.rt.dataSource.onewire.OneWireDataSourceRT;
import com.serotonin.mango.rt.dataSource.pachube.PachubeDataSourceRT;
import com.serotonin.mango.rt.dataSource.snmp.SnmpDataSourceRT;
import com.serotonin.mango.rt.dataSource.sql.SqlDataSourceRT;
import com.serotonin.mango.rt.dataSource.viconics.ViconicsDataSourceRT;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.bacnet.BACnetIPDataSourceVO;
import com.serotonin.mango.vo.dataSource.ebro.EBI25DataSourceVO;
import com.serotonin.mango.vo.dataSource.galil.GalilDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;
import com.serotonin.mango.vo.dataSource.nmea.NmeaDataSourceVO;
import com.serotonin.mango.vo.dataSource.onewire.OneWireDataSourceVO;
import com.serotonin.mango.vo.dataSource.pachube.PachubeDataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import com.serotonin.mango.vo.dataSource.viconics.ViconicsDataSourceVO;
import com.serotonin.messaging.MessageControl;
import com.serotonin.messaging.StreamTransport;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.tcp.TcpMaster;
import com.serotonin.modbus4j.serial.ascii.AsciiMaster;
import com.serotonin.modbus4j.serial.rtu.RtuMaster;
import com.serotonin.viconics.ViconicsNetwork;
import gnu.io.SerialPort;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.scada_lts.ds.messaging.MessagingDataSourceRT;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.service.MessagingService;
import org.scada_lts.ds.messaging.service.MessagingServiceFactory;
import org.snmp4j.mp.SnmpConstants;
import org.springframework.jdbc.core.JdbcOperations;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.resetUnreliableDataPoints;
import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.setUnreliableDataPoints;
import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

public final class InitializeDataSourceRtMockUtils {

    private InitializeDataSourceRtMockUtils() {}

    public static SqlDataSourceRT createSql() {
        SqlDataSourceRT dataSource = new SqlDataSourceRT(new SqlDataSourceVO());
        sqlMock(dataSource);
        return dataSource;
    }

    public static OneWireDataSourceRT createOneWire() {
        OneWireDataSourceRT dataSource = new OneWireDataSourceRT(new OneWireDataSourceVO());
        oneWireMock(dataSource);
        return dataSource;
    }

    public static OPCDataSource createOpc() {
        OPCDataSource dataSource = new OPCDataSource(new OPCDataSourceVO<>());
        opcMock(dataSource);
        return dataSource;
    }

    public static ModbusDataSource createModbusIpDataSource() {
        ModbusDataSource dataSource = new ModbusIpDataSource(new ModbusIpDataSourceVO());
        modbusMock(dataSource);
        return dataSource;
    }

    public static ModbusDataSource createModbusSerialDataSource() {
        ModbusDataSource dataSource = new ModbusSerialDataSource(new ModbusSerialDataSourceVO());
        modbusMock(dataSource);
        return dataSource;
    }

    public static IEC101DataSource createIec101Ethernet() {
        IEC101DataSource dataSource = new IEC101EthernetDataSource(new IEC101EthernetDataSourceVO());
        iec101Mock(dataSource);
        return dataSource;
    }

    public static IEC101DataSource createIec101Serial() {
        IEC101DataSource dataSource = new IEC101SerialDataSource(new IEC101SerialDataSourceVO());
        iec101Mock(dataSource);
        return dataSource;
    }

    public static ViconicsDataSourceRT createViconics() {
        ViconicsDataSourceRT dataSource = new ViconicsDataSourceRT(new ViconicsDataSourceVO());
        viconicsMock(dataSource);
        return dataSource;
    }

    public static NmeaDataSourceRT createNmea() {
        NmeaDataSourceRT dataSource = new NmeaDataSourceRT(new NmeaDataSourceVO());
        nmeaMock(dataSource);
        return dataSource;
    }

    public static EBI25DataSourceRT createEbi25() {
        EBI25DataSourceRT dataSource = new EBI25DataSourceRT(new EBI25DataSourceVO());
        ebi25Mock(dataSource);
        return dataSource;
    }

    public static Alpha2DataSource createAlpha2() {
        Alpha2DataSource dataSource = new Alpha2DataSource(new Alpha2DataSourceVO<>());
        alpha2Mock(dataSource);
        return dataSource;
    }

    public static Dnp3DataSource createDnp3Ip() {
        Dnp3DataSource dataSource = new Dnp3IpDataSource(new Dnp3IpDataSourceVO());
        dnp3Mock(dataSource);
        return dataSource;
    }

    public static Dnp3DataSource createDnp3Serial() {
        Dnp3DataSource dataSource = new Dnp3SerialDataSource(new Dnp3SerialDataSourceVO());
        dnp3Mock(dataSource);
        return dataSource;
    }

    public static BACnetIPDataSourceRT createBacNet() {
        BACnetIPDataSourceRT dataSource = new BACnetIPDataSourceRT(new BACnetIPDataSourceVO());
        bacNetMock(dataSource);
        return dataSource;
    }

    public static PachubeDataSourceRT createPachube() {
        PachubeDataSourceRT dataSource = new PachubeDataSourceRT(new PachubeDataSourceVO());
        pachubeMock(dataSource);
        return dataSource;
    }

    public static ASCIISerialDataSource createAsciiSerial() {
        ASCIISerialDataSource dataSource = new ASCIISerialDataSource(new ASCIISerialDataSourceVO<>());
        asciiSerialMock(dataSource);
        return dataSource;
    }

    public static RadiuinoEventDataSource createRadiuino() {
        RadiuinoEventDataSource dataSource = new RadiuinoEventDataSource(new RadiuinoDataSourceVO<>());
        radiuinoMock(dataSource);
        return dataSource;
    }

    public static RadiuinoPollingDataSource createRadiuinoPolling() {
        RadiuinoPollingDataSource dataSource = new RadiuinoPollingDataSource(new RadiuinoDataSourceVO<>());
        radiuinoPollingMock(dataSource);
        return dataSource;
    }

    public static DrStorageHt5bDataSource createDrStorageMock() {
        DrStorageHt5bDataSource dataSource = new DrStorageHt5bDataSource(new DrStorageHt5bDataSourceVO<>());
        drStorageMock(dataSource);
        return dataSource;
    }

    public static MessagingDataSourceRT createAmqp() {
        AmqpDataSourceVO amqpDataSourceVO = new AmqpDataSourceVO();
        MessagingService messagingService = mock(MessagingService.class);
        return new MessagingDataSourceRT(amqpDataSourceVO, messagingService);
    }

    public static MessagingDataSourceRT createMqtt() {
        MqttDataSourceVO mqttDataSourceVO = new MqttDataSourceVO();
        MessagingService messagingService = MessagingServiceFactory.newService(mqttDataSourceVO);
        return new MessagingDataSourceRT(mqttDataSourceVO, messagingService);
    }

    public static SnmpDataSourceRT createSnmp() {
        SnmpDataSourceVO snmpDataSourceVO = new SnmpDataSourceVO();
        snmpDataSourceVO.setSnmpVersion(SnmpConstants.version1);
        snmpDataSourceVO.setCommunity("comm");
        SnmpDataSourceRT dataSource = new SnmpDataSourceRT(snmpDataSourceVO);
        return dataSource;
    }

    public static GalilDataSourceRT createGalil() {
        GalilDataSourceVO galilDataSourceVO = new GalilDataSourceVO();
        galilDataSourceVO.setHost("localhost");
        galilDataSourceVO.setTimeout(1);
        galilDataSourceVO.setPort(1111);
        GalilDataSourceRT dataSource = new GalilDataSourceRT(galilDataSourceVO);
        galilMock(dataSource);
        return dataSource;
    }

    public static <T> Consumer<T> consumer(Consumer<T> consumer) {
        return consumer;
    }

    public static <T> Supplier<T> supplier(Supplier<T> function) {
        return function;
    }

    public static <T extends DataSourceVO<T>> Function<T, DataPointVO> function(Function<T, DataPointVO> function) {
        return function;
    }

    public static void resetUnreliable(List<DataPointRT> list1, List<DataPointRT> list2, List<DataPointRT> list3) {
        resetUnreliableDataPoints(list1);
        resetUnreliableDataPoints(list2);
        resetUnreliableDataPoints(list3);
    }

    public static void setUnreliable(List<DataPointRT> list1, List<DataPointRT> list2, List<DataPointRT> list3) {
        setUnreliableDataPoints(list1);
        setUnreliableDataPoints(list2);
        setUnreliableDataPoints(list3);
    }

    public static void sqlMock(SqlDataSourceRT dataSource) {
        mockStatic(SqlDataSourceUtils.class);
        JdbcOperations jdbcOperations = mock(JdbcOperations.class);
        when(jdbcOperations.queryForObject(anyString(), eq(int.class))).thenReturn(1);
        try {
            when(SqlDataSourceUtils.createJdbcOperations(any()))
                    .thenReturn(jdbcOperations);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void oneWireMock(OneWireDataSourceRT dataSource) {
        Network networkMock = mock(Network.class);
        try {
            whenNew(Network.class).withAnyArguments()
                    .thenReturn(networkMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void opcMock(OPCDataSource dataSource) {
        RealOPCMaster realOPCMasterMock = mock(RealOPCMaster.class);
        try {
            whenNew(RealOPCMaster.class).withAnyArguments()
                    .thenReturn(realOPCMasterMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void modbusMock(ModbusDataSource dataSource) {
        AsciiMaster asciiMasterMock = mock(AsciiMaster.class);
        TcpMaster tcpMasterMock = mock(TcpMaster.class);
        RtuMaster rtuMasterMock = mock(RtuMaster.class);
        try {
            whenNew(AsciiMaster.class).withAnyArguments()
                    .thenReturn(asciiMasterMock);
            whenNew(TcpMaster.class).withAnyArguments()
                    .thenReturn(tcpMasterMock);
            whenNew(RtuMaster.class).withAnyArguments()
                    .thenReturn(rtuMasterMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void iec101Mock(IEC101DataSource dataSource) {
        IEC101Master iec101MasterMock = mock(IEC101Master.class);
        try {
            whenNew(IEC101Master.class).withAnyArguments()
                    .thenReturn(iec101MasterMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void galilMock(GalilDataSourceRT dataSource) {
        InputStream inputStreamMock = mock(InputStream.class);
        OutputStream outputStreamMock = mock(OutputStream.class);

        Socket socketMock = mock(Socket.class);

        MessageControl messageControlMock = mock(MessageControl.class);
        try {
            when(messageControlMock.send(any(GalilRequest.class)))
                    .thenReturn(new GalilResponse(new byte[]{}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StreamTransport streamTransportMock = mock(StreamTransport.class);
        doAnswer(a -> {
            return null;
        }).when(streamTransportMock).start(anyString());

        try {
            doAnswer(a -> {
                return null;
            }).when(socketMock).connect(any());

            when(socketMock.getInputStream()).thenReturn(inputStreamMock);
            when(socketMock.getOutputStream()).thenReturn(outputStreamMock);

            whenNew(Socket.class).withAnyArguments()
                    .thenReturn(socketMock);

            whenNew(MessageControl.class).withAnyArguments()
                    .thenReturn(messageControlMock);

            whenNew(StreamTransport.class).withAnyArguments()
                    .thenReturn(streamTransportMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void viconicsMock(ViconicsDataSourceRT dataSource) {
        ViconicsNetwork viconicsNetworkMock = mock(ViconicsNetwork.class);
        try {
            whenNew(ViconicsNetwork.class).withAnyArguments()
                    .thenReturn(viconicsNetworkMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void nmeaMock(NmeaDataSourceRT dataSource) {
        NmeaReceiver nmeaReceiverMock = mock(NmeaReceiver.class);
        TimeoutTask timeoutTaskMock = mock(TimeoutTask.class);
        try {
            whenNew(NmeaReceiver.class).withAnyArguments()
                    .thenReturn(nmeaReceiverMock);
            whenNew(TimeoutTask.class).withAnyArguments()
                    .thenReturn(timeoutTaskMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ebi25Mock(EBI25DataSourceRT dataSource) {
        ModbusMaster modbusMasterMock = mock(ModbusMaster.class);
        mockStatic(EBI25Constants.class);
        try {
            when(EBI25Constants.initModbusMaster(anyString(), anyInt(), anyBoolean(), anyInt(), anyInt(), any()))
                    .thenReturn(modbusMasterMock);
        } catch (ModbusInitException e) {
            throw new RuntimeException(e);
        }
    }

    public static void alpha2Mock(Alpha2DataSource dataSource) {
        Alpha2Master alpha2MasterMock = mock(Alpha2Master.class);
        try {
            doAnswer(invocation -> {
                return null;
            }).when(alpha2MasterMock).init();
            whenNew(Alpha2Master.class)
                    .withAnyArguments()
                    .thenReturn(alpha2MasterMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void dnp3Mock(Dnp3DataSource dataSource) {
        DNP3Master dnp3MasterMock = mock(DNP3Master.class);
        try {
            whenNew(DNP3Master.class)
                    .withAnyArguments()
                    .thenReturn(dnp3MasterMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void bacNetMock(BACnetIPDataSourceRT dataSource) {
        LocalDevice localDeviceMock = mock(LocalDevice.class);

        DeviceEventHandler deviceEventHandlerMock = mock(DeviceEventHandler.class);
        when(localDeviceMock.getEventHandler()).thenReturn(deviceEventHandlerMock);
        when(localDeviceMock.isInitialized()).thenReturn(true);

        try {
            whenNew(LocalDevice.class)
                    .withAnyArguments()
                    .thenReturn(localDeviceMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void pachubeMock(PachubeDataSourceRT dataSource) {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.getParams()).thenReturn(new HttpClientParams());
        mockStatic(Common.class);
        when(Common.getHttpClient(anyInt())).thenReturn(httpClient);
    }

    public static void asciiSerialMock(ASCIISerialDataSource dataSource) {
        SerialPort serialPortMock = mock(SerialPort.class);
        dataSource.setsPort(serialPortMock);
    }

    public static void radiuinoMock(RadiuinoEventDataSource dataSource) {
        SerialPort serialPortMock = mock(SerialPort.class);
        dataSource.setsPort(serialPortMock);
    }

    public static void radiuinoPollingMock(RadiuinoPollingDataSource dataSource) {
        SerialPort serialPortMock = mock(SerialPort.class);
        dataSource.setsPort(serialPortMock);
    }

    public static void drStorageMock(DrStorageHt5bDataSource dataSource) {
        SerialPort serialPortMock = mock(SerialPort.class);
        dataSource.setsPort(serialPortMock);
        InputStream inputStreamMock = mock(InputStream.class);
        try {
            when(inputStreamMock.available()).thenReturn(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataSource.setInSerialStream(inputStreamMock);
        dataSource.setsPort(serialPortMock);
    }
}
