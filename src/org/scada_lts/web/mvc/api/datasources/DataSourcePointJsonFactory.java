package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpPointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.snmp.SnmpDataSourceJson;
import org.scada_lts.web.mvc.api.datasources.snmp.SnmpPointLocatorJson;
import org.scada_lts.web.mvc.api.datasources.virtual.VirtualDataSourceJson;
import org.scada_lts.web.mvc.api.datasources.virtual.VirtualPointLocatorJson;

public class DataSourcePointJsonFactory {

    private DataSourcePointJsonFactory() {}

    public static DataPointLocatorJson getDataPointLocatorJson(PointLocatorVO vo) {
        if(vo instanceof VirtualPointLocatorVO) {
            return new VirtualPointLocatorJson((VirtualPointLocatorVO) vo);
        } else if (vo instanceof SnmpPointLocatorVO) {
            return new SnmpPointLocatorJson((SnmpPointLocatorVO) vo);
        }
        return null;
    }

    public static DataSourceJson getDataSourceJson(DataSourceVO<?> ds) {
        if(ds instanceof VirtualDataSourceVO) {
            return new VirtualDataSourceJson((VirtualDataSourceVO) ds);
        } else if (ds instanceof SnmpDataSourceVO) {
            return new SnmpDataSourceJson((SnmpDataSourceVO) ds);
        }
        return new DataSourceJson(ds);
    }
}
