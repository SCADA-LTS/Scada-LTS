package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_2__FixedAggregationEnabled extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0_2__FixedAggregationEnabled.class);

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
        String settingName = "aggregationEnabled";
        try {
            String aggregationEnabled = jdbcTmp.queryForObject("SELECT settingValue FROM systemSettings WHERE settingName=? ", new Object[]{settingName}, String.class);
            if(aggregationEnabled != null) {
                boolean value = "true".equalsIgnoreCase(aggregationEnabled) || "Y".equalsIgnoreCase(aggregationEnabled);
                String settingValue = value ? "Y" : "N";
                jdbcTmp.update("UPDATE systemSettings SET settingValue=? WHERE settingName=?", settingValue, settingName);
            }
        } catch (EmptyResultDataAccessException ex) {
        } catch (Exception ex) {
            throw ex;
        }
    }
}

