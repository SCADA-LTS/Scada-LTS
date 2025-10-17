package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_1__AddTypeRef3ColumnToEvents extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_1__AddTypeRef3ColumnToEvents.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            migrate(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void migrate(JdbcTemplate jdbcTmp) {

        boolean existsTypeRef3Column = jdbcTmp.queryForObject("SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='events' AND `COLUMN_NAME`='typeRef3') IS NOT NULL;", boolean.class);

        if(!existsTypeRef3Column)
            jdbcTmp.update( "ALTER TABLE events ADD COLUMN typeRef3 int not null");
    }
}

