package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_0_4_2__MultiChangeHistory extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_0_4_2__MultiChangeHistory.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        try {
            final String indexHistory = "CREATE INDEX index_multiChangesHistoryId ON values_multi_changes_history (multiChangesHistoryId);";
            jdbcTemplate.execute(indexHistory);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
