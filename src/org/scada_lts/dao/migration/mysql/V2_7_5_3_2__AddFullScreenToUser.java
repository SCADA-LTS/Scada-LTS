package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_5_3_2__AddFullScreenToUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_3_2__AddFullScreenToUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        try {
            final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

            String addEnableFullScreenSql = "ALTER TABLE users ADD COLUMN enableFullScreen BOOLEAN DEFAULT false";
            jdbcTemplate.execute(addEnableFullScreenSql);

            String addHideShortcutDisableFullScreenSql = "ALTER TABLE users ADD COLUMN hideShortcutDisableFullScreen BOOLEAN DEFAULT false";
            jdbcTemplate.execute(addHideShortcutDisableFullScreenSql);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
