package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorVO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;

import java.util.List;
import java.util.UUID;

public final class MqttUtils {

    private MqttUtils() {}

    public static boolean isExistsClientId(String clientId, DataPointService dataPointService,
                                           String dataPointXid, DataSourceService dataSourceService) {
        if(clientId == null || clientId.isBlank() || dataPointXid == null || dataPointXid.isBlank())
            return true;
        List<DataSourceVO<?>> dataSources = dataSourceService.getDataSources(DataSourceVO.Type.MQTT);
        for(DataSourceVO<?> dataSource : dataSources) {
            if(dataSource instanceof MqttDataSourceVO) {
                List<DataPointVO> dataPoints = dataPointService.getDataPoints(dataSource.getId(), null);
                for (DataPointVO dataPoint : dataPoints) {
                    if(!dataPointXid.equals(dataPoint.getXid())) {
                        PointLocatorVO pointLocator = dataPoint.getPointLocator();
                        if (pointLocator instanceof MqttPointLocatorVO) {
                            MqttPointLocatorVO mqttPointLocator = (MqttPointLocatorVO) pointLocator;
                            if (clientId.equals(mqttPointLocator.getClientId())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String generateUniqueClientId() {
        return UUID.randomUUID().toString();
    }
}
