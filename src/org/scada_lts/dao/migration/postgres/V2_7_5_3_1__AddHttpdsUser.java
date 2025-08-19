package org.scada_lts.dao.migration.postgres;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

public class V2_7_5_3_1__AddHttpdsUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_3_1__AddHttpdsUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        try {
            final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
            addHttpdsBasicUser(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    public static void addHttpdsBasicUser(JdbcTemplate jdbcTemplate) {
        String username = "httpds-basic";

        List<Integer> ids = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE username = ?",
                new Object[]{username},
                Integer.class
        );

        if (ids.isEmpty()) {
            String sql = "INSERT INTO users (username, password, email, phone, admin, disabled, " +
                    "homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) VALUES (?,?,?,?,?,?,?,?,?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                new ArgumentPreparedStatementSetter(new Object[]{
                        username,
                        Common.encrypt(username),
                        "null@null.com",
                        "",
                        "N",
                        "Y",
                        "",
                        0,
                        "N"
                }).setValues(ps);
                return ps;
            });

        }
    }
}
