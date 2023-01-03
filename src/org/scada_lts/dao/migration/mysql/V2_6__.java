package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.view.event.NoneEventRenderer;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_6__ extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_6__.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        updateDataPointsTable(jdbcTmp);

        String correctLiveAlarms = ""+
                "CREATE OR REPLACE VIEW liveAlarms AS SELECT" +
                "  id," +
                "  func_fromats_date(activeTime) AS 'activation-time'," +
                "  func_fromats_date(inactiveTime) AS 'inactivation-time'," +
                "  dataPointType AS 'level'," +
                "  dataPointName AS 'name'," +
                "  dataPointId AS dataPointId " +
                "FROM plcAlarms WHERE acknowledgeTime = 0 " +
                "  AND (inactiveTime = 0 OR (inactiveTime > UNIX_TIMESTAMP(NOW() - INTERVAL 24 HOUR) * 1000)) " +
                "ORDER BY inactiveTime = 0 DESC, activeTime DESC, inactiveTime DESC, id DESC";

        jdbcTmp.execute("ALTER TABLE events ADD shortMessage LONGTEXT;");
        jdbcTmp.update("UPDATE events SET message = CONCAT(message, '||') WHERE typeId = 1;");
        jdbcTmp.execute(correctLiveAlarms);


    }

    private void updateDataPointsTable(JdbcTemplate jdbcTmp) throws Exception {

        try {
            List<DataPointVO> dataPoints = jdbcTmp.query("SELECT id, data FROM dataPoints", (resultSet, i) -> {
                try (InputStream inputStream = resultSet.getBinaryStream("data");
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    DataPointVO dataPointVO = (DataPointVO) objectInputStream.readObject();
                    dataPointVO.setId(resultSet.getInt("id"));
                    if (dataPointVO.getEventTextRenderer() == null)
                        dataPointVO.setEventTextRenderer(new NoneEventRenderer());
                    if (dataPointVO.getDescription() == null)
                        dataPointVO.setDescription("");
                    return dataPointVO;
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    return null;
                }
            });

            boolean isNull = dataPoints.stream().anyMatch(Objects::isNull);
            if (isNull) {
                throw new IllegalStateException("DataPointVO is null!");
            }

            for (DataPointVO dataPoint : dataPoints) {
                jdbcTmp.update("UPDATE dataPoints set data = ? WHERE id = ?",
                        new SerializationData().writeObject(dataPoint), dataPoint.getId());
            }

        } catch (EmptyResultDataAccessException empty) {
            LOG.warn(empty);
        }
    }
    
}
