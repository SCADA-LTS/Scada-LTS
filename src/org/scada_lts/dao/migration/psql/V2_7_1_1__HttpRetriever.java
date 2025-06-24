package org.scada_lts.dao.migration.psql;

import com.serotonin.mango.vo.dataSource.http.HttpRetrieverDataSourceVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class V2_7_1_1__HttpRetriever extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_1__HttpRetriever.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            migrateHttpRetriever(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }
    private void migrateHttpRetriever(JdbcTemplate jdbcTemplate) {
        List<HttpRetrieverDataSourceVO> dataSources = jdbcTemplate.query(
                "SELECT id, xid, name, data FROM dataSources WHERE dataSourceType = 11",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        HttpRetrieverDataSourceVO ds = (HttpRetrieverDataSourceVO) ois.readObject();
                        ds.setId(rs.getInt("id"));
                        ds.setXid(rs.getString("xid"));
                        ds.setName(rs.getString("name"));
                        if (ds.getStaticHeaders() == null) {
                            ds.setStaticHeaders(new ArrayList<>());
                        }
                        return ds;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Deserialization error for datasource ID " + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        if (dataSources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one HttpRetrieverDataSourceVO is null!");
        }

        for (HttpRetrieverDataSourceVO ds : dataSources) {
            ByteArrayInputStream bais = new SerializationData().writeObject(ds);

            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE dataSources SET data = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, ds.getId());
                return ps;
            });
        }
    }
}
