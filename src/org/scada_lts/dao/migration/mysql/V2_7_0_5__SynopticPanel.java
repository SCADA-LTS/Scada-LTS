package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_0_5__SynopticPanel extends BaseJavaMigration {
    private static final Log LOG = LogFactory.getLog(V2_7_0_5__SynopticPanel.class);

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();
        createSynopticPanelsTable(jdbcTmp);
    }

    void createSynopticPanelsTable(JdbcTemplate jdbc) {
        try {
            String tableName = "synopticPanels";
            String create = "" +
                    "CREATE TABLE " + tableName + " (" +
                    "id INT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "xid VARCHAR(50) DEFAULT NULL, " +
                    "name VARCHAR(50) DEFAULT NULL, " +
                    "vectorImage TEXT DEFAULT NULL, " +
                    "componentData TEXT DEFAULT NULL, " +
                    "PRIMARY KEY (id)" +
                    ");";
            jdbc.execute(create);
        } catch (Exception e) {
            LOG.error("Failed to create Synoptic Panel table!");
        }

    }
}
