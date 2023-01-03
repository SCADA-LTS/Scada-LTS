package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;

public class V2_7_0_3__AnonymousUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_0_3__AnonymousUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        String userInsert = "insert into users (username, password, email, phone, admin, disabled, " +
                "homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) values (?,?,?,?,?,?,?,?,?);";

        jdbcTmp.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(userInsert);
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
            }).setValues(preparedStatement);
            return preparedStatement;
        });
    }
}
