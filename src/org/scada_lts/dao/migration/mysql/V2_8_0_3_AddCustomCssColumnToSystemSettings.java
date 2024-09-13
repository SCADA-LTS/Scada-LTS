package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_3_AddCustomCssColumnToSystemSettings extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_3_AddCustomCssColumnToSystemSettings.class);

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            addCustomCssColumn(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void addCustomCssColumn(JdbcTemplate jdbcTmp) {
        boolean existsCustomCssColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='customCss') IS NOT NULL;",
                boolean.class
        );

        if (!existsCustomCssColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN customCss TEXT DEFAULT NULL;");
        }
    }
}
