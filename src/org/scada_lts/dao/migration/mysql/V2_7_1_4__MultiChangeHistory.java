package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;

public class V2_7_1_4__MultiChangeHistory extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_4__MultiChangeHistory.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        try {

            final String indexHistory = "CREATE INDEX index_multiChangesHistoryId ON values_multi_changes_history (multiChangesHistoryId);";
            // TODO
            // select if (
            // exists(
            // select distinct index_name from information_schema.statistics
            // where table_schema = 'schema_db_name'
            // and table_name = 'tab_name' and index_name like 'index_1'
            // )
            // ,'select ''index index_1 exists'' _______;'
            // ,'create index index_1 on tab_name(column_name_names)') into @a;
            // PREPARE stmt1 FROM @a;
            // EXECUTE stmt1;
            // DEALLOCATE PREPARE stmt1;

            // select distinct index_name from
            // information_schema.statistics where table_schema = 'scadalts' and
            // table_name='values_multi_changes_history' and index_name like '%index_%'

            jdbcTemplate.execute(indexHistory);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
