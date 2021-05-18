package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7__ extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        jdbcTmp.execute("ALTER TABLE users " +
                "ADD hiddenMenu BOOLEAN, " +
                "ADD defaultTheme ENUM('MODERN', 'STANDARD');");

        jdbcTmp.update("UPDATE users SET hiddenMenu = false, defaultTheme = 'STANDARD';");
    }
}
