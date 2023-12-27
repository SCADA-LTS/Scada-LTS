package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

public class V2_7_5_3__AddLangToUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_3__AddLangToUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        try {
            final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
            String defaultLang = getLangDefault(jdbcTemplate);

            String addLangSql = "ALTER TABLE users ADD COLUMN lang VARCHAR(10) DEFAULT '" + defaultLang + "'";
            jdbcTemplate.execute(addLangSql);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private static String getLangDefault(JdbcTemplate jdbcTemplate) {
        String defaultLangSql = "SELECT settingValue FROM systemSettings WHERE settingName='" + SystemSettingsDAO.LANGUAGE + "'";
        try {
            return jdbcTemplate.queryForObject(defaultLangSql, String.class);
        } catch (Exception ex) {
            LOG.debug(ex.getMessage(), ex);
            return "en";
        }
    }
}
