package org.scada_lts.dao.migration.postgres;

import com.serotonin.mango.Common;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;

public class V2_7_0_3__AnonymousUser extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        String userInsert = "INSERT INTO users " +
                "(username, password, email, phone, admin, disabled, homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(userInsert);
            new ArgumentPreparedStatementSetter(new Object[]{
                    "anonymous-user",
                    Common.encrypt("anonymous"),
                    "anonymous@mail.com",
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
