package org.scada_lts.dao.migration.psql;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

public class V2_7_1_2__PurgeLimitStrategyDatapointProperty extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_2__PurgeLimitStrategyDatapointProperty.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        updatePurgeLimitStrategyDatapointProperty(jdbcTemplate);
    }

    private void updatePurgeLimitStrategyDatapointProperty(JdbcTemplate jdbcTemplate) throws Exception {
        try {
            List<DataPointVO> dataPoints = jdbcTemplate.query(
                    "SELECT id, data FROM dataPoints",
                    (rs, rowNum) -> {
                        try (InputStream inputStream = rs.getBinaryStream("data");
                             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {

                            DataPointVO dp = (DataPointVO) objectInputStream.readObject();
                            dp.setId(rs.getInt("id"));
                            dp.setPurgeStrategy(DataPointVO.PurgeStrategy.PERIOD);
                            dp.setPurgeValuesLimit(SystemSettingsDAO
                                    .getIntValue(SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE));
                            return dp;

                        } catch (IOException | ClassNotFoundException e) {
                            LOG.error("Error deserializing DataPointVO id=" + rs.getInt("id"), e);
                            return null;
                        }
                    }
            );

            boolean hasNull = dataPoints.stream().anyMatch(Objects::isNull);
            if (hasNull) {
                throw new IllegalStateException("At least one DataPointVO is null!");
            }

            for (DataPointVO dp : dataPoints) {
                ByteArrayInputStream bais = new SerializationData().writeObject(dp);

                jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE dataPoints SET data = ? WHERE id = ?"
                    );
                    ps.setBinaryStream(1, bais, bais.available());
                    ps.setInt(2, dp.getId());
                    return ps;
                });
            }

        } catch (EmptyResultDataAccessException e) {
            LOG.warn("No dataPoints found", e);
        }
    }
}
