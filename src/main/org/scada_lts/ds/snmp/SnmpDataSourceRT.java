//package org.scada_lts.ds.snmp;
//
//import com.serotonin.ShouldNeverHappenException;
//import com.serotonin.mango.rt.dataImage.DataPointRT;
//import com.serotonin.mango.rt.dataImage.PointValueTime;
//import com.serotonin.mango.rt.dataImage.SetPointSource;
//import com.serotonin.mango.rt.dataSource.EventDataSource;
//import com.serotonin.mango.rt.dataSource.snmp.Version;
//import com.serotonin.mango.vo.dataSource.DataSourceVO;
//import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.snmp4j.Snmp;
//import org.snmp4j.Target;
//import org.snmp4j.transport.DefaultUdpTransportMapping;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//public class SnmpDataSourceRT extends EventDataSource  {
//
//    private final Log log = LogFactory.getLog(SnmpDataSourceRT.class);
//
//    private SnmpDataSourceVO vo;
//    private String address;
//    private Target target;
//    private Version version;
//    private Snmp snmp;
//
//    public SnmpDataSourceRT(SnmpDataSourceVO vo) {
//        super(vo);
//        this.vo = vo;
//        version = Version.getVersion(vo.getSnmpVersion(), vo.getCommunity(),
//                vo.getSecurityName(), vo.getAuthProtocol(),
//                vo.getAuthPassphrase(), vo.getPrivProtocol(),
//                vo.getPrivPassphrase(), vo.getEngineId(),
//                vo.getContextEngineId(), vo.getContextName());
//    }
//
//    @Override
//    public void initialize() {
//        try {
//            address = InetAddress.getByName(vo.getHost()).getHostAddress();
//            target = version.getTarget(vo.getHost(), vo.getPort(), vo.getRetries(), vo.getTimeout());
//            snmp = new Snmp(new DefaultUdpTransportMapping());
//            snmp.listen();
//            super.initialize();
//        } catch (UnknownHostException | SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void terminate() {
//        super.terminate();
//        try {
//            if(snmp != null) {
//                snmp.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
//
//
//    }
//}
