package org.scada_lts.dao.migration.postgres;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_1_0__UserNames extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        String checkAndAddFirstName =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='users' AND column_name='firstname'" +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE users ADD COLUMN firstname VARCHAR(255) DEFAULT NULL;'; " +
                        "END IF; " +
                        "END $$;";

        String checkAndAddLastName =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='users' AND column_name='lastname'" +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE users ADD COLUMN lastname VARCHAR(255) DEFAULT NULL;'; " +
                        "END IF; " +
                        "END $$;";

        jdbcTemplate.execute(checkAndAddFirstName);
        jdbcTemplate.execute(checkAndAddLastName);
    }

}
