package org.scada_lts.dao.migration.postgres;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_0_5__SynopticPanel extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS synopticPanels (" +
                        "id SERIAL PRIMARY KEY, " +
                        "xid VARCHAR(50), " +
                        "name VARCHAR(50), " +
                        "vectorImage TEXT, " +
                        "componentData TEXT" +
                        ")"
        );
    }
}
