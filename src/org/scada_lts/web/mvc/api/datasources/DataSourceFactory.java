package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;

public class DataSourceFactory {

    public static GenericJsonDataSource getDataSource(DataSourceVO<?> ds) {
        switch (ds.getType()) {
            case VIRTUAL:
                return new VirtualDataSourceJson((VirtualDataSourceVO) ds);
            case SNMP:
                return new SnmpDataSourceJson((SnmpDataSourceVO) ds);
            default:
                return new GenericJsonDataSource(ds);
        }
    }

    public static DataSourceVO<?> parseDataSource(DataSourceJson<?> ds) {
        if (ds instanceof VirtualDataSourceJson) {
            return (VirtualDataSourceVO) ds.convertDataSourceToVO();
        } else if (ds instanceof SnmpDataSourceJson) {
            return (SnmpDataSourceVO) ds.convertDataSourceToVO();
        } else {
            return null;
        }
    }
}
