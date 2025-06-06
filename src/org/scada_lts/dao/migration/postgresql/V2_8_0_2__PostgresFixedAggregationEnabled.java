package org.scada_lts.dao.migration.postgresql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_2__PostgresFixedAggregationEnabled extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_2__PostgresFixedAggregationEnabled.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            migrateFixedAggregationEnabled(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void migrateFixedAggregationEnabled(JdbcTemplate jdbcTemplate) {
        String settingName = "aggregationEnabled";
        try {
            String aggregationEnabled = jdbcTemplate.queryForObject(
                    "SELECT settingValue FROM systemsettings WHERE settingName = ?",
                    new Object[]{settingName},
                    String.class
            );

            if (aggregationEnabled != null) {
                boolean value = "true".equalsIgnoreCase(aggregationEnabled) || "Y".equalsIgnoreCase(aggregationEnabled);
                String settingValue = value ? "Y" : "N";
                jdbcTemplate.update(
                        "UPDATE systemsettings SET settingValue = ? WHERE settingName = ?",
                        settingValue,
                        settingName
                );
            }
        } catch (EmptyResultDataAccessException ex) {
        } catch (Exception ex) {
            throw ex;
        }
    }
}

