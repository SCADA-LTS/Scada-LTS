package org.scada_lts.dao.migration.postgresql;

import com.serotonin.mango.view.event.NoneEventRenderer;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

public class V2_6__Postgres extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_6__Postgres.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        updateDataPointsTable(jdbcTemplate);

        String correctLiveAlarms =
                "CREATE OR REPLACE VIEW \"liveAlarms\" AS " +
                        "SELECT " +
                        "  id, " +
                        "  func_fromats_date(activeTime) AS \"activation-time\", " +
                        "  func_fromats_date(inactiveTime) AS \"inactivation-time\", " +
                        "  dataPointType AS \"level\", " +
                        "  dataPointName AS \"name\", " +
                        "  dataPointId " +
                        "FROM plcAlarms " +
                        "WHERE acknowledgeTime = 0 " +
                        "  AND (inactiveTime = 0 OR (inactiveTime > (EXTRACT(EPOCH FROM NOW() - INTERVAL '24 HOURS') * 1000))) " +
                        "ORDER BY (inactiveTime = 0) DESC, activeTime DESC, inactiveTime DESC, id DESC;";

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='events' AND column_name='shortmessage'" +
                        ") THEN " +
                        "  ALTER TABLE events ADD COLUMN shortMessage TEXT; " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.update("UPDATE events SET message = message || '||' WHERE typeId = 1;");
        jdbcTemplate.execute(correctLiveAlarms);

    }

    private void updateDataPointsTable(JdbcTemplate jdbcTemplate) throws Exception {
        try {
            List<DataPointVO> dataPoints = jdbcTemplate.query(
                    "SELECT id, data FROM dataPoints",
                    (rs, rowNum) -> {
                        try (InputStream is = rs.getBinaryStream("data");
                             ObjectInputStream ois = new ObjectInputStream(is)) {
                            DataPointVO dp = (DataPointVO) ois.readObject();
                            dp.setId(rs.getInt("id"));

                            if (dp.getEventTextRenderer() == null)
                                dp.setEventTextRenderer(new NoneEventRenderer());
                            if (dp.getDescription() == null)
                                dp.setDescription("");

                            return dp;
                        } catch (IOException | ClassNotFoundException ex) {
                            throw new RuntimeException("DataPointVO deserialization error id=" + rs.getInt("id"), ex);
                        }
                    }
            );

            if (dataPoints.stream().anyMatch(Objects::isNull)) {
                throw new IllegalStateException("At least one DataPointVO is null!");
            }

            for (DataPointVO dp : dataPoints) {
                ByteArrayInputStream bais = new SerializationData().writeObject(dp);

                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE dataPoints SET data = ? WHERE id = ?"
                    );
                    ps.setBinaryStream(1, bais, bais.available());
                    ps.setInt(2, dp.getId());
                    return ps;
                });
            }

        } catch (EmptyResultDataAccessException empty) {
            LOG.warn("No dataPoints found", empty);
        }
    }
}
