package com.serotonin.mango.rt.dataSource;

import br.org.scadabr.rt.dataSource.alpha2.Alpha2DataSource;
import br.org.scadabr.rt.dataSource.asciiSerial.ASCIISerialDataSource;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3IpDataSource;
import br.org.scadabr.rt.dataSource.dnp3.Dnp3SerialDataSource;
import br.org.scadabr.rt.dataSource.drStorageHt5b.DrStorageHt5bDataSource;
import br.org.scadabr.rt.dataSource.iec101.IEC101EthernetDataSource;
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
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.dataSource.bacnet.BACnetIPDataSourceRT;
import com.serotonin.mango.rt.dataSource.ebro.EBI25DataSourceRT;
import com.serotonin.mango.rt.dataSource.galil.GalilDataSourceRT;
import com.serotonin.mango.rt.dataSource.http.HttpReceiverDataSourceRT;
import com.serotonin.mango.rt.dataSource.modbus.ModbusDataSource;
import com.serotonin.mango.rt.dataSource.modbus.ModbusIpDataSource;
import com.serotonin.mango.rt.dataSource.modbus.ModbusSerialDataSource;
import com.serotonin.mango.rt.dataSource.nmea.NmeaDataSourceRT;
import com.serotonin.mango.rt.dataSource.onewire.OneWireDataSourceRT;
import com.serotonin.mango.rt.dataSource.pachube.PachubeDataSourceRT;
import com.serotonin.mango.rt.dataSource.persistent.PersistentDataSourceRT;
import com.serotonin.mango.rt.dataSource.snmp.SnmpDataSourceRT;
import com.serotonin.mango.rt.dataSource.sql.SqlDataSourceRT;
import com.serotonin.mango.rt.dataSource.viconics.ViconicsDataSourceRT;
import com.serotonin.mango.rt.dataSource.vmstat.VMStatDataSourceRT;
import com.serotonin.mango.vo.dataSource.bacnet.BACnetIPDataSourceVO;
import com.serotonin.mango.vo.dataSource.ebro.EBI25DataSourceVO;
import com.serotonin.mango.vo.dataSource.galil.GalilDataSourceVO;
import com.serotonin.mango.vo.dataSource.http.HttpReceiverDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;
import com.serotonin.mango.vo.dataSource.nmea.NmeaDataSourceVO;
import com.serotonin.mango.vo.dataSource.onewire.OneWireDataSourceVO;
import com.serotonin.mango.vo.dataSource.pachube.PachubeDataSourceVO;
import com.serotonin.mango.vo.dataSource.persistent.PersistentDataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import com.serotonin.mango.vo.dataSource.viconics.ViconicsDataSourceVO;
import com.serotonin.mango.vo.dataSource.vmstat.VMStatDataSourceVO;
import com.serotonin.mango.web.ContextWrapper;
import org.apache.derby.iapi.services.i18n.MessageService;
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
import org.scada_lts.ds.messaging.MessagingDataSourceRT;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.service.MessagingService;
import org.scada_lts.ds.messaging.service.MessagingServiceFactory;

import java.util.function.Supplier;

import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.modbusMock;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.wrap;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({Common.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class InitializeWithErrorsDataSourceRtTest {

    @Parameterized.Parameters(name = "{index}: data source: {1}")
    public static Object[][] data() {
        PersistentDataSourceVO persistentDataSourceVO = new PersistentDataSourceVO();
        persistentDataSourceVO.setPort(-1);

        ModbusIpDataSourceVO modbusIpDataSourceVO = new ModbusIpDataSourceVO();
        modbusIpDataSourceVO.setTransportType(ModbusIpDataSourceVO.TransportType.TCP_KEEP_ALIVE);

        BACnetIPDataSourceVO baCnetIPDataSourceVO = new BACnetIPDataSourceVO();
        baCnetIPDataSourceVO.setPort(47808);

        AmqpDataSourceVO amqpDataSourceVO = new AmqpDataSourceVO();
        MqttDataSourceVO mqttDataSourceVO = new MqttDataSourceVO();
        mqttDataSourceVO.setServerHost("localhost");
        mqttDataSourceVO.setServerPortNumber(1234);

        MessagingService messageServiceMock = mock(MessagingService.class);
        doAnswer(a -> {
            throw new RuntimeException();
        }).when(messageServiceMock).open();

        return new Object[][] {

                {wrap(() -> new ASCIISerialDataSource(new ASCIISerialDataSourceVO<>())), ASCIISerialDataSource.class.getSimpleName()},
                {wrap(() -> new Alpha2DataSource(new Alpha2DataSourceVO<>())), Alpha2DataSource.class.getSimpleName()},
                {wrap(() -> new BACnetIPDataSourceRT(baCnetIPDataSourceVO)), BACnetIPDataSourceRT.class.getSimpleName()},
                {wrap(() -> new Dnp3IpDataSource(new Dnp3IpDataSourceVO())), Dnp3IpDataSource.class.getSimpleName()},
                {wrap(() -> new Dnp3SerialDataSource(new Dnp3SerialDataSourceVO())), Dnp3SerialDataSource.class.getSimpleName()},
                {wrap(() -> new DrStorageHt5bDataSource(new DrStorageHt5bDataSourceVO<>())), DrStorageHt5bDataSource.class.getSimpleName()},
                {wrap(() -> new EBI25DataSourceRT(new EBI25DataSourceVO())), EBI25DataSourceRT.class.getSimpleName()},
                {wrap(() -> new NmeaDataSourceRT(new NmeaDataSourceVO())), NmeaDataSourceRT.class.getSimpleName()},
                {wrap(() -> new RadiuinoEventDataSource(new RadiuinoDataSourceVO<>())), RadiuinoEventDataSource.class.getSimpleName()},
                {wrap(() -> new RadiuinoPollingDataSource(new RadiuinoDataSourceVO<>())), RadiuinoPollingDataSource.class.getSimpleName()},
                {wrap(() -> new VMStatDataSourceRT(new VMStatDataSourceVO())), VMStatDataSourceRT.class.getSimpleName()},
                {wrap(() -> new ViconicsDataSourceRT(new ViconicsDataSourceVO())), ViconicsDataSourceRT.class.getSimpleName()},
                {wrap(() -> new GalilDataSourceRT(new GalilDataSourceVO())), GalilDataSourceRT.class.getSimpleName()},
                {wrap(() -> new IEC101EthernetDataSource(new IEC101EthernetDataSourceVO())), IEC101EthernetDataSource.class.getSimpleName()},
                {wrap(() -> new IEC101SerialDataSource(new IEC101SerialDataSourceVO())), IEC101SerialDataSource.class.getSimpleName()},
                {wrap(() -> new MessagingDataSourceRT(amqpDataSourceVO, MessagingServiceFactory.newService(amqpDataSourceVO))), AmqpDataSourceVO.class.getSimpleName()},
                {wrap(() -> new MessagingDataSourceRT(mqttDataSourceVO, messageServiceMock)), MqttDataSourceVO.class.getSimpleName()},

                {wrap(() -> new ModbusSerialDataSource(new ModbusSerialDataSourceVO())), ModbusSerialDataSource.class.getSimpleName()},
                {wrap(() -> new ModbusIpDataSource(modbusIpDataSourceVO)), ModbusIpDataSource.class.getSimpleName()},
                {wrap(() -> new OPCDataSource(new OPCDataSourceVO<>())), OPCDataSource.class.getSimpleName()},
                {wrap(() -> new OneWireDataSourceRT(new OneWireDataSourceVO())), OneWireDataSourceRT.class.getSimpleName()},
                {wrap(() -> new SqlDataSourceRT(new SqlDataSourceVO())), SqlDataSourceRT.class.getSimpleName()},
                {wrap(() -> new HttpReceiverDataSourceRT(new HttpReceiverDataSourceVO())), HttpReceiverDataSourceRT.class.getSimpleName()},
                {wrap(() -> new PachubeDataSourceRT(new PachubeDataSourceVO())), PachubeDataSourceRT.class.getSimpleName()},
                {wrap(() -> new SnmpDataSourceRT(new SnmpDataSourceVO())), SnmpDataSourceRT.class.getSimpleName()},
                {wrap(() -> new PersistentDataSourceRT(persistentDataSourceVO)), PersistentDataSourceRT.class.getSimpleName()},

        };
    }

    private DataSourceRT dataSourceRT;
    private final Supplier<DataSourceRT> createDataSource;

    public InitializeWithErrorsDataSourceRtTest(Supplier<DataSourceRT> createDataSource, String name) {
        this.createDataSource = createDataSource;
    }

    private EventManager eventManager;

    @Before
    public void config() {

        eventManager = PowerMockito.mock(EventManager.class);

        ContextWrapper contextWrapper = PowerMockito.mock(ContextWrapper.class);
        PowerMockito.when(contextWrapper.getEventManager()).thenReturn(eventManager);

        Common.ctx = contextWrapper;

        this.dataSourceRT = this.createDataSource.get();

        if(dataSourceRT instanceof BACnetIPDataSourceRT) {
            DataSourceRT dataSourceRT1 = this.createDataSource.get();
            dataSourceRT1.initialize();
        }

        reset(eventManager);
    }

    @After
    public void clean() {
        dataSourceRT.terminate();
    }

    @Test
    public void when_invoke_initialize_with_errors_then_not_initialized() {

        //when:
        dataSourceRT.initialize();

        //then:
        Assert.assertEquals(false, dataSourceRT.isInitialized());
    }

    @Test
    public void when_never_invoke_initialize_then_not_initialized() {

        //then:
        Assert.assertEquals(false, dataSourceRT.isInitialized());
    }

    @Test
    public void when_invoke_initialize_with_errors_then_one_raiseEvent() {

        //when:
        dataSourceRT.initialize();

        //then:
        Mockito.verify(eventManager, times(1)).raiseEvent(any(), anyLong(), anyBoolean(), anyInt(), any(), anyMap());
    }

    @Test
    public void when_invoke_initialize_with_errors_then_never_returnToNormal() {

        //when:
        dataSourceRT.initialize();

        //then:
        Mockito.verify(eventManager, never()).returnToNormal(any(), anyLong());
    }

}
