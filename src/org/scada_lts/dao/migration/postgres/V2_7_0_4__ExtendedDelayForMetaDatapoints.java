package org.scada_lts.dao.migration.postgres;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

public class V2_7_0_4__ExtendedDelayForMetaDatapoints extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_0_4__ExtendedDelayForMetaDatapoints.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            migrateExtendedDelayForMetaDatapoints(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

    }

    private void migrateExtendedDelayForMetaDatapoints (JdbcTemplate jdbcTemplate) {
        List<DataPointVO> dataPoints = jdbcTemplate.query("SELECT id, data FROM dataPoints", (rs, i) -> {
            try (InputStream is = rs.getBinaryStream("data");
                 ObjectInputStream ois = new ObjectInputStream(is)) {
                DataPointVO dp = (DataPointVO) ois.readObject();
                dp.setId(rs.getInt("id"));
                return dp;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error("Deserialization failed for dataPoint id=" + rs.getInt("id"), ex);
                return null;
            }
        });

        try {
            if (dataPoints.stream().anyMatch(Objects::isNull)) {
                throw new IllegalStateException("At least one DataPointVO is null!");
            }

            for (DataPointVO dp : dataPoints) {
                PointLocatorVO locator = dp.getPointLocator();
                if (locator instanceof MetaPointLocatorVO) {
                    MetaPointLocatorVO meta = (MetaPointLocatorVO) locator;
                    if (meta.getExecutionDelayPeriodTypeCode() == 0) {
                        meta.setExecutionDelayPeriodType(TimePeriodType.SECONDS);
                        dp.setPointLocator(meta);

                        ByteArrayInputStream bais = new SerializationData().writeObject(dp);

                        jdbcTemplate.update(conn -> {
                            PreparedStatement ps = conn.prepareStatement("UPDATE dataPoints SET data = ? WHERE id = ?");
                            ps.setBinaryStream(1, bais, bais.available());
                            ps.setInt(2, dp.getId());
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
