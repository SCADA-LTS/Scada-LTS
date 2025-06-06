package org.scada_lts.dao.migration.postgresql;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

public class V2_7_2_1__PostgresMqttPointLocatorUpdateDataPointXid extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_2_1__PostgresMqttPointLocatorUpdateDataPointXid.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            migrate(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

    }

    private void migrate(JdbcTemplate jdbcTemplate) {
        List<DataPointVO> dataPoints = jdbcTemplate.query(
                "SELECT id, xid, data FROM dataPoints",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        DataPointVO dp = (DataPointVO) ois.readObject();
                        dp.setId(rs.getInt("id"));
                        dp.setXid(rs.getString("xid"));
                        return dp;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize DataPointVO id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        try {
            if (dataPoints.stream().anyMatch(Objects::isNull)) {
                throw new IllegalStateException("DataPointVO is null!");
            }

            for (DataPointVO dataPoint : dataPoints) {
                PointLocatorVO locator = dataPoint.getPointLocator();
                if (locator instanceof MqttPointLocatorVO) {
                    MqttPointLocatorVO mqttLocator = (MqttPointLocatorVO) locator;
                    if (mqttLocator.getDataPointXid() == null) {
                        mqttLocator.setDataPointXid(dataPoint.getXid());
                        dataPoint.setPointLocator(mqttLocator);

                        ByteArrayInputStream bais = new SerializationData().writeObject(dataPoint);
                        jdbcTemplate.update(connection -> {
                            PreparedStatement ps = connection.prepareStatement(
                                    "UPDATE dataPoints SET data = ? WHERE id = ?"
                            );
                            ps.setBinaryStream(1, bais, bais.available());
                            ps.setInt(2, dataPoint.getId());
                            return ps;
                        });
                    }
                }
            }
        } finally {
            dataPoints.clear();
        }
    }
}
