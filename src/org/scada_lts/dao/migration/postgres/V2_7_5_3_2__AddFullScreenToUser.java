package org.scada_lts.dao.migration.postgres;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_5_3_2__AddFullScreenToUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_3_2__AddFullScreenToUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        try {
            final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

            String addEnableFullScreen =
                    "DO $$ BEGIN " +
                            "IF NOT EXISTS ( " +
                            "  SELECT 1 FROM information_schema.columns " +
                            "  WHERE table_name = 'users' AND column_name = 'enablefullscreen' " +
                            ") THEN " +
                            "  EXECUTE 'ALTER TABLE users ADD COLUMN enablefullscreen BOOLEAN DEFAULT false'; " +
                            "END IF; " +
                            "END $$;";

            String addHideShortcutDisable =
                    "DO $$ BEGIN " +
                            "IF NOT EXISTS ( " +
                            "  SELECT 1 FROM information_schema.columns " +
                            "  WHERE table_name = 'users' AND column_name = 'hideshortcutdisablefullscreen' " +
                            ") THEN " +
                            "  EXECUTE 'ALTER TABLE users ADD COLUMN hideshortcutdisablefullscreen BOOLEAN DEFAULT false'; " +
                            "END IF; " +
                            "END $$;";

            jdbcTemplate.execute(addEnableFullScreen);
            jdbcTemplate.execute(addHideShortcutDisable);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
