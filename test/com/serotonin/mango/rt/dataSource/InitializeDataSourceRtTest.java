package com.serotonin.mango.rt.dataSource;

import br.org.scadabr.rt.dataSource.alpha2.Alpha2DataSource;
import br.org.scadabr.rt.dataSource.asciiFile.ASCIIFileDataSource;
import br.org.scadabr.rt.dataSource.asciiSerial.ASCIISerialDataSource;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3IpDataSource;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3SerialDataSource;
import br.org.scadabr.rt.dataSource.drStorageHt5b.DrStorageHt5bDataSource;
import br.org.scadabr.rt.dataSource.iec101.IEC101EthernetDataSource;
import br.org.scadabr.rt.dataSource.iec101.IEC101SerialDataSource;
import br.org.scadabr.rt.dataSource.nodaves7.NodaveS7DataSource;
import br.org.scadabr.rt.dataSource.opc.OPCDataSource;
import br.org.scadabr.vo.dataSource.asciiFile.ASCIIFileDataSourceVO;
import br.org.scadabr.vo.dataSource.nodaves7.NodaveS7DataSourceVO;
import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoEventDataSource;
import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoPollingDataSource;
import com.i2msolucoes.alpha24j.layer.user.Alpha2Master;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.dataSource.bacnet.BACnetIPDataSourceRT;
import com.serotonin.mango.rt.dataSource.ebro.EBI25Constants;
import com.serotonin.mango.rt.dataSource.ebro.EBI25DataSourceRT;
import com.serotonin.mango.rt.dataSource.galil.GalilDataSourceRT;
import com.serotonin.mango.rt.dataSource.http.*;
import com.serotonin.mango.rt.dataSource.internal.InternalDataSourceRT;
import com.serotonin.mango.rt.dataSource.jmx.JmxDataSourceRT;
import com.serotonin.mango.rt.dataSource.mbus.MBusDataSourceRT;
import com.serotonin.mango.rt.dataSource.meta.MetaDataSourceRT;
import com.serotonin.mango.rt.dataSource.modbus.ModbusIpDataSource;
import com.serotonin.mango.rt.dataSource.modbus.ModbusSerialDataSource;
import com.serotonin.mango.rt.dataSource.nmea.NmeaDataSourceRT;
import com.serotonin.mango.rt.dataSource.onewire.OneWireDataSourceRT;
import com.serotonin.mango.rt.dataSource.openv4j.OpenV4JDataSourceRT;
import com.serotonin.mango.rt.dataSource.pachube.PachubeDataSourceRT;
import com.serotonin.mango.rt.dataSource.persistent.PersistentDataSourceRT;
import com.serotonin.mango.rt.dataSource.pop3.Pop3DataSourceRT;
import com.serotonin.mango.rt.dataSource.snmp.SnmpDataSourceRT;
import com.serotonin.mango.rt.dataSource.sql.SqlDataSourceRT;
import com.serotonin.mango.rt.dataSource.viconics.ViconicsDataSourceRT;
import com.serotonin.mango.rt.dataSource.virtual.VirtualDataSourceRT;
import com.serotonin.mango.rt.dataSource.vmstat.VMStatDataSourceRT;
import com.serotonin.mango.util.InitializeDataSourceRtMockUtils;
import com.serotonin.mango.util.SqlDataSourceUtils;
import com.serotonin.mango.vo.dataSource.http.HttpImageDataSourceVO;
import com.serotonin.mango.vo.dataSource.http.HttpRetrieverDataSourceVO;
import com.serotonin.mango.vo.dataSource.internal.InternalDataSourceVO;
import com.serotonin.mango.vo.dataSource.jmx.JmxDataSourceVO;
import com.serotonin.mango.vo.dataSource.mbus.MBusDataSourceVO;
import com.serotonin.mango.vo.dataSource.meta.MetaDataSourceVO;
import com.serotonin.mango.vo.dataSource.openv4j.OpenV4JDataSourceVO;
import com.serotonin.mango.vo.dataSource.persistent.PersistentDataSourceVO;
import com.serotonin.mango.vo.dataSource.pop3.Pop3DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.vmstat.VMStatDataSourceVO;
import com.serotonin.mango.web.ContextWrapper;
import com.serotonin.modbus4j.ModbusFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.serial.gnu.io.ScadaCommPortIdentifier;

import org.springframework.security.web.savedrequest.Enumerator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Supplier;

import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.supplier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({Common.class, ASCIISerialDataSource.class, ScadaCommPortIdentifier.class, Alpha2DataSource.class,
        Alpha2Master.class, BACnetIPDataSourceRT.class, Dnp3IpDataSource.class, Dnp3SerialDataSource.class,
        EBI25DataSourceRT.class, EBI25Constants.class, NmeaDataSourceRT.class, Runtime.class, VMStatDataSourceRT.class,
        ViconicsDataSourceRT.class, GalilDataSourceRT.class, IEC101EthernetDataSource.class, IEC101SerialDataSource.class,
        ModbusFactory.class, OPCDataSource.class, OneWireDataSourceRT.class, SqlDataSourceUtils.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class InitializeDataSourceRtTest<T extends DataSourceRT> {

    @Parameterized.Parameters(name = "{index}: data source: {0}, raiseEventTimes: {2}, returnToNormalTimes: {3}")
    public static Object[][] data() throws Exception {

        return new Object[][] {

                {ASCIISerialDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createAsciiSerial), 1, 0},
                {Alpha2DataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createAlpha2), 1, 0},
                {BACnetIPDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createBacNet), 3, 0},
                {Dnp3IpDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createDnp3Ip), 1, 0},
                {Dnp3SerialDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createDnp3Serial), 1, 0},
                {DrStorageHt5bDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createDrStorageMock), 1, 0},
                {EBI25DataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createEbi25), 1, 0},
                {NmeaDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createNmea), 1, 0},
                {RadiuinoEventDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createRadiuino), 1, 0},
                {RadiuinoPollingDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createRadiuinoPolling), 1, 0},
                {VMStatDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtTest::createVmStat), 1, 0},
                {ViconicsDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createViconics), 1, 0},
                {GalilDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createGalil), 1, 0},
                {IEC101EthernetDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createIec101Ethernet), 1, 0},
                {IEC101SerialDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createIec101Serial), 1, 0},
                {AmqpDataSourceVO.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createAmqp), 1, 0},
                {MqttDataSourceVO.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createMqtt), 1, 0},
                {ModbusIpDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createModbusIpDataSource), 1, 0},
                {ModbusSerialDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createModbusSerialDataSource), 1, 0},
                {OPCDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createOpc), 1, 0},
                {OneWireDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createOneWire), 1, 0},
                {SqlDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createSql), 1, 0},
                {HttpReceiverDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createSql), 1, 0},
                {PachubeDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createPachube), 1, 0},
                {SnmpDataSourceRT.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createSnmp), 1, 0},
                {PersistentDataSourceRT.class.getSimpleName(), supplier(() -> new PersistentDataSourceRT(new PersistentDataSourceVO())), 1, 0},
                {ModbusIpDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createModbusIpDataSource), 1, 0},
                {ModbusSerialDataSource.class.getSimpleName(), supplier(InitializeDataSourceRtMockUtils::createModbusSerialDataSource), 1, 0},


                {VirtualDataSourceRT.class.getSimpleName(), supplier(() -> new VirtualDataSourceRT(new VirtualDataSourceVO())), 0, 0},
                {ASCIIFileDataSource.class.getSimpleName(), supplier(() -> new ASCIIFileDataSource(new ASCIIFileDataSourceVO<>())), 0, 0},
                {HttpImageDataSourceRT.class.getSimpleName(), supplier(() -> new HttpImageDataSourceRT(new HttpImageDataSourceVO())), 0, 0},
                {HttpRetrieverDataSourceRT.class.getSimpleName(), supplier(() -> new HttpRetrieverDataSourceRT(new HttpRetrieverDataSourceVO())), 0, 0},
                {InternalDataSourceRT.class.getSimpleName(), supplier(() -> new InternalDataSourceRT(new InternalDataSourceVO())), 0, 0},
                {JmxDataSourceRT.class.getSimpleName(), supplier(() -> new JmxDataSourceRT(new JmxDataSourceVO())), 0, 0},
                {MBusDataSourceRT.class.getSimpleName(), supplier(() -> new MBusDataSourceRT(new MBusDataSourceVO())), 0, 0},
                {MetaDataSourceRT.class.getSimpleName(), supplier(() -> new MetaDataSourceRT(new MetaDataSourceVO())), 0, 0},
                {NodaveS7DataSource.class.getSimpleName(), supplier(() -> new NodaveS7DataSource(new NodaveS7DataSourceVO<>())), 0, 0},
                {OpenV4JDataSourceRT.class.getSimpleName(), supplier(() -> new OpenV4JDataSourceRT(new OpenV4JDataSourceVO())), 0, 0},
                {Pop3DataSourceRT.class.getSimpleName(), supplier(() -> new Pop3DataSourceRT(new Pop3DataSourceVO())), 0, 0},

        };
    }

    private DataSourceRT dataSourceRT;
    private final int raiseEventTimes;
    private final int returnToNormalTimes;
    private final Supplier<T> mockConfig;

    public InitializeDataSourceRtTest(String name, Supplier<T> mockConfig, int returnToNormalTimes, int raiseEventTimes) {
        this.raiseEventTimes = raiseEventTimes;
        this.returnToNormalTimes = returnToNormalTimes;
        this.mockConfig = mockConfig;
    }

    private EventManager eventManager;

    @Before
    public void config() throws Exception {
        eventManager = mock(EventManager.class);
        HttpReceiverMulticaster multicastListener = mock(HttpReceiverMulticaster.class);

        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        when(contextWrapper.getEventManager()).thenReturn(eventManager);
        when(contextWrapper.getHttpReceiverMulticaster()).thenReturn(multicastListener);

        Common.ctx = contextWrapper;

        PowerMockito.mockStatic(ScadaCommPortIdentifier.class);
        when(ScadaCommPortIdentifier.getPortIdentifiers()).thenReturn(new Enumerator(new ArrayList()));

        dataSourceRT = mockConfig.get();
        reset(eventManager);
    }

    @After
    public void clean() {
        dataSourceRT.terminate();
        reset(eventManager);
    }

    @Test
    public void when_invoke_initialize_then_initialized() {

        //when:
        dataSourceRT.initialize();

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());
    }


    @Test
    public void when_never_invoke_initialize_then_not_initialized() {

        //then:
        Assert.assertEquals(false, dataSourceRT.isInitialized());
    }

    @Test
    public void when_invoke_initialize_then_invoke_returnToNormal_times() {

        //when:
        dataSourceRT.initialize();

        //then:
        Mockito.verify(eventManager, times(returnToNormalTimes)).returnToNormal(any(), anyLong());
    }

    @Test
    public void when_invoke_initialize_with_errors_then_invoke_raiseEvent_times() {

        //when:
        dataSourceRT.initialize();

        //then:
        Mockito.verify(eventManager, times(raiseEventTimes)).raiseEvent(any(), anyLong(), anyBoolean(), anyInt(), any(), anyMap());
    }

    public static DataSourceRT createVmStat() {
        InputStream inputStreamMock = mock(InputStream.class);
        Process processMock = mock(Process.class);
        when(processMock.getInputStream()).thenReturn(inputStreamMock);

        java.lang.Runtime runtimeMock = mock(Runtime.class);
        try {
            when(runtimeMock.exec(anyString())).thenReturn(processMock);
            PowerMockito.mockStatic(java.lang.Runtime.class);
            when(java.lang.Runtime.getRuntime()).thenReturn(runtimeMock);

            BufferedReader bufferedReader = mock(BufferedReader.class);
            when(bufferedReader.readLine()).thenReturn("abc");
            PowerMockito.whenNew(BufferedReader.class).withAnyArguments()
                    .thenReturn(bufferedReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new VMStatDataSourceRT(new VMStatDataSourceVO());
    }
}
