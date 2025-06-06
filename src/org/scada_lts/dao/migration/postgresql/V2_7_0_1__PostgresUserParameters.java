package org.scada_lts.dao.migration.postgresql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_0_1__PostgresUserParameters extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='hidemenu') THEN " +
                        "  ALTER TABLE users ADD COLUMN hideMenu BOOLEAN DEFAULT false; " +
                        "END IF; " +
                        "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='theme') THEN " +
                        "  ALTER TABLE users ADD COLUMN theme VARCHAR(255) DEFAULT 'DEFAULT'; " +
                        "END IF; " +
                        "END $$;"
        );
    }
}