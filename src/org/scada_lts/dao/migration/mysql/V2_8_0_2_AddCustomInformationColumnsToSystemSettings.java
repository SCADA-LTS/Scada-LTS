package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_2_AddCustomInformationColumnsToSystemSettings extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_2_AddCustomInformationColumnsToSystemSettings.class);

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            addCustomInformationColumns(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void addCustomInformationColumns(JdbcTemplate jdbcTmp) {
        boolean existsCustomInformationColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='customInformation') IS NOT NULL;",
                boolean.class
        );

        boolean existsCustomInformationPrefixColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='customInformationPrefix') IS NOT NULL;",
                boolean.class
        );

        boolean existsCustomInformationStylesheetColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='customInformationStylesheet') IS NOT NULL;",
                boolean.class
        );

        if (!existsCustomInformationColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN customInformation VARCHAR(255) DEFAULT '';");
        }

        if (!existsCustomInformationPrefixColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN customInformationPrefix VARCHAR(255) DEFAULT '';");
        }

        if (!existsCustomInformationStylesheetColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN customInformationStylesheet VARCHAR(255) DEFAULT 'color: green; font-size: 2em;';");
        }
    }
}
