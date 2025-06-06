package org.scada_lts.dao.migration.postgresql;

import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
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

public class V2_7_7_1__PostgresSqlDataSourceLimit extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_7_1__PostgresSqlDataSourceLimit.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            migrateSqlDataSourceLimit(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void migrateSqlDataSourceLimit(JdbcTemplate jdbcTemplate) {
        List<SqlDataSourceVO> dataSources = jdbcTemplate.query(
                "SELECT id, data FROM dataSources WHERE dataSourceType = 6",
                (resultSet, i) -> {
                    try (InputStream inputStream = resultSet.getBinaryStream("data");
                         ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                        SqlDataSourceVO dataSourceVO = (SqlDataSourceVO) objectInputStream.readObject();
                        dataSourceVO.setId(resultSet.getInt("id"));
                        dataSourceVO.setStatementLimit(0);
                        return dataSourceVO;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Deserialization error for SqlDataSourceVO id=" + resultSet.getInt("id"), ex);
                        return null;
                    }
                }
        );

        if (dataSources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one SqlDataSourceVO is null!");
        }

        for (SqlDataSourceVO dataSource : dataSources) {
            jdbcTemplate.update(
                    "UPDATE dataSources SET data = ? WHERE id = ?",
                    new SerializationData().writeObject(dataSource),
                    dataSource.getId()
            );
        }
    }
}

