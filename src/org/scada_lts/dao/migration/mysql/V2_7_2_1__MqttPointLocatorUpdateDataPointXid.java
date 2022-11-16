package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorVO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_7_2_1__MqttPointLocatorUpdateDataPointXid extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_2_1__MqttPointLocatorUpdateDataPointXid.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            migrate(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

    }

    private void migrate(JdbcTemplate jdbcTmp) {
        List<DataPointVO> dataPoints = jdbcTmp.query("SELECT id, xid, data FROM dataPoints", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                DataPointVO dataPoint = (DataPointVO) objectInputStream.readObject();
                dataPoint.setId(resultSet.getInt("id"));
                dataPoint.setXid(resultSet.getString("xid"));
                return dataPoint;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        });

        try {
            boolean isNull = dataPoints.stream().anyMatch(Objects::isNull);
            if (isNull) {
                throw new IllegalStateException("DataPointVO is null!");
            }

            for (DataPointVO dataPoint : dataPoints) {
                PointLocatorVO locator = dataPoint.getPointLocator();
                if (locator instanceof MqttPointLocatorVO) {
                    MqttPointLocatorVO mqttPointLocatorVO = (MqttPointLocatorVO) locator;
                    if (mqttPointLocatorVO.getDataPointXid() == null) {
                        mqttPointLocatorVO.setDataPointXid(dataPoint.getXid());
                        dataPoint.setPointLocator(mqttPointLocatorVO);
                        jdbcTmp.update("UPDATE dataPoints set data = ? WHERE id = ?",
                                new SerializationData().writeObject(dataPoint), dataPoint.getId());
                    }
                }
            }
        } finally {
            dataPoints.clear();
        }
    }
}
