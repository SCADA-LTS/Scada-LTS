package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_7__AddAcceptedColumnsToEvents extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_7__AddAcceptedColumnsToEvents.class);

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
        jdbcTmp.update("ALTER TABLE events ADD COLUMN acceptedTs bigint DEFAULT NULL;");
        jdbcTmp.update("ALTER TABLE events ADD COLUMN acceptedUsername VARCHAR(40) DEFAULT NULL;");
    }
}
