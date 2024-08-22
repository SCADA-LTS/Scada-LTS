package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_2_AddTopDescriptionColumnsToSystemSettings extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_2_AddTopDescriptionColumnsToSystemSettings.class);

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            addTopDescriptionColumns(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void addTopDescriptionColumns(JdbcTemplate jdbcTmp) {
        boolean existsTopDescriptionColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='topDescription') IS NOT NULL;",
                boolean.class
        );

        boolean existsTopDescriptionPrefixColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='topDescriptionPrefix') IS NOT NULL;",
                boolean.class
        );

        boolean existsTopDescriptionStylesheetColumn = jdbcTmp.queryForObject(
                "SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='systemSettings' AND `COLUMN_NAME`='topDescriptionStylesheet') IS NOT NULL;",
                boolean.class
        );

        if (!existsTopDescriptionColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN topDescription VARCHAR(255) DEFAULT '';");
        }

        if (!existsTopDescriptionPrefixColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN topDescriptionPrefix VARCHAR(255) DEFAULT '';");
        }

        if (!existsTopDescriptionStylesheetColumn) {
            jdbcTmp.update("ALTER TABLE systemSettings ADD COLUMN topDescriptionStylesheet VARCHAR(255) DEFAULT 'color: green; font-size: 2em;';");
        }
    }
}
