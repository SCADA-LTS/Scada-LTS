package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_7_0_4__ExtendedDelayForMetaDatapoints extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_0_4__ExtendedDelayForMetaDatapoints.class);

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
        List<DataPointVO> dataPoints = jdbcTmp.query("SELECT id, data FROM dataPoints", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                DataPointVO dataPoint = (DataPointVO) objectInputStream.readObject();
                dataPoint.setId(resultSet.getInt("id"));
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
                if (locator instanceof MetaPointLocatorVO) {
                    MetaPointLocatorVO metaPointLocator = (MetaPointLocatorVO) locator;
                    if (metaPointLocator.getExecutionDelayPeriodTypeCode() == 0) {
                        metaPointLocator.setExecutionDelayPeriodType(TimePeriodType.SECONDS);
                        dataPoint.setPointLocator(metaPointLocator);
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
