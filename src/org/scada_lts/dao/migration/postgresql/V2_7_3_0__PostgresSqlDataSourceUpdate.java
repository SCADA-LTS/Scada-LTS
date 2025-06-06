package org.scada_lts.dao.migration.postgresql;

import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import com.serotonin.util.StringUtils;
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

public class V2_7_3_0__PostgresSqlDataSourceUpdate extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_3_0__PostgresSqlDataSourceUpdate.class);

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
        List<SqlDataSourceVO> dataSources = jdbcTemplate.query(
                "SELECT id, data FROM dataSources WHERE dataSourceType = 6",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        SqlDataSourceVO ds = (SqlDataSourceVO) ois.readObject();
                        ds.setId(rs.getInt("id"));
                        if (StringUtils.isEmpty(ds.getJndiResourceName())) {
                            ds.setJndiResource(false);
                        }
                        return ds;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize SqlDataSourceVO id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        if (dataSources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("SqlDataSourceVO is null!");
        }

        for (SqlDataSourceVO dataSource : dataSources) {
            ByteArrayInputStream bais = new SerializationData().writeObject(dataSource);
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE dataSources SET data = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, dataSource.getId());
                return ps;
            });
        }
    }
}

