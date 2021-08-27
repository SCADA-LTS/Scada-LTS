package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpPointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.snmp.SnmpPointLocatorJson;
import org.scada_lts.web.mvc.api.datasources.virtual.VirtualPointLocatorJson;

public class DataPointLocatorJsonFactory {

    public static DataPointLocatorJson getDataPointLocatorJson(PointLocatorVO vo) {
        if(vo instanceof VirtualPointLocatorVO) {
            return new VirtualPointLocatorJson((VirtualPointLocatorVO) vo);
        } else if (vo instanceof SnmpPointLocatorVO) {
            return new SnmpPointLocatorJson((SnmpPointLocatorVO) vo);
        }
        return null;
    }
}
