package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_1__SynopticPanelChangeDataTypeForColumnVectorImage extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_1__SynopticPanelChangeDataTypeForColumnVectorImage.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            changeDataType(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void changeDataType(JdbcTemplate jdbcTmp) {
        try {
            String tableName = "synopticpanels";
            updateTable(jdbcTmp, tableName);
        } catch (Exception ex) {
            String tableName = "synopticPanels";
            updateTable(jdbcTmp, tableName);
        }

    }

    private void updateTable(JdbcTemplate jdbcTmp, String tableName) {
        String dataType = jdbcTmp.queryForObject("SELECT `DATA_TYPE` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`=? AND `COLUMN_NAME`='vectorImage';", new Object[] {tableName}, String.class);

        if ("text".equalsIgnoreCase(dataType))
            jdbcTmp.update("ALTER TABLE  " + tableName + " MODIFY vectorImage MEDIUMTEXT;");
    }
}
