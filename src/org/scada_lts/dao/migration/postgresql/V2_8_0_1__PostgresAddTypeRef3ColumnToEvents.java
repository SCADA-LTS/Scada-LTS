package org.scada_lts.dao.migration.postgresql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_1__PostgresAddTypeRef3ColumnToEvents extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_1__PostgresAddTypeRef3ColumnToEvents.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            boolean existsTypeRef3Column = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (" +
                            "SELECT 1 FROM information_schema.columns " +
                            "WHERE table_name = 'events' AND column_name = 'typeref3'" +
                            ")",
                    Boolean.class
            );

            if (!existsTypeRef3Column) {
                jdbcTemplate.execute("ALTER TABLE events ADD COLUMN typeRef3 INTEGER NOT NULL DEFAULT 0");
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}

