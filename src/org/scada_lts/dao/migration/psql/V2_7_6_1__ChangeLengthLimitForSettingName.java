package org.scada_lts.dao.migration.psql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_6_1__ChangeLengthLimitForSettingName extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_6_1__ChangeLengthLimitForSettingName.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            String alterSettingNameType =
                    "DO $$ BEGIN " +
                            "IF EXISTS ( " +
                            "  SELECT 1 FROM information_schema.columns " +
                            "  WHERE table_name = 'systemsettings' AND column_name = 'settingname' " +
                            "    AND data_type != 'character varying' " +
                            ") THEN " +
                            "  EXECUTE 'ALTER TABLE systemsettings ALTER COLUMN \"settingName\" TYPE VARCHAR(255)'; " +
                            "END IF; " +
                            "END $$;";

            jdbcTemplate.execute(alterSettingNameType);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
