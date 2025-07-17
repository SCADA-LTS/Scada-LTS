package org.scada_lts.dao.migration.psql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class V2_7_5_3__AddLangToUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_3__AddLangToUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        try {
            final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
            migrateLangDefault(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void migrateLangDefault(JdbcTemplate jdbcTemplate) {
        try {
            String defaultLangSql = "SELECT settingValue FROM systemsettings WHERE settingName='" + SystemSettingsDAO.LANGUAGE + "'";
            String defaultLang;
            try {
                defaultLang = jdbcTemplate.queryForObject(defaultLangSql, String.class);
            } catch (Exception ex) {
                LOG.debug("Could not fetch default language, fallback to 'en': " + ex.getMessage(), ex);
                defaultLang = "en";
            }

            List<Integer> results = jdbcTemplate.query(
                    "SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'lang'",
                    (rs, rowNum) -> rs.getInt(1)
            );

            if (results.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN lang VARCHAR(10) DEFAULT '" + defaultLang + "'");
            }
        } catch (Exception ex) {
            LOG.error("Error during migrateLangDefault: " + ex.getMessage(), ex);
            throw ex;
        }
    }
}
