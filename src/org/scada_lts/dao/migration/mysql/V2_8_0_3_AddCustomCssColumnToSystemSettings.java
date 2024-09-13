package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.scada_lts.web.mvc.api.css.CssUtils.getCustomCssFileFromPath;

public class V2_8_0_3_AddCustomCssColumnToSystemSettings extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_3_AddCustomCssColumnToSystemSettings.class);

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            updateCustomCssContent(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void updateCustomCssContent(JdbcTemplate jdbcTmp) {
        try {
            File customCssFile = getCustomCssFileFromPath();
            if (customCssFile != null && customCssFile.exists()) {
                String customCssContent = new String(Files.readAllBytes(customCssFile.toPath()));
                jdbcTmp.update("UPDATE systemSettings SET customCss = ?", customCssContent);
            }
        } catch (IOException e) {
            LOG.error("Error reading custom CSS file: " + e.getMessage(), e);
        }
    }
}
