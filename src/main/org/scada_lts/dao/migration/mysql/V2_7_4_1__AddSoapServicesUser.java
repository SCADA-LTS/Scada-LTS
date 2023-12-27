package org.scada_lts.dao.migration.mysql;

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

public class V2_7_4_1__AddSoapServicesUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_4_1__AddSoapServicesUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        try {
            final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

            String userInsert = "insert into users (username, password, email, phone, admin, disabled, " +
                    "homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) values (?,?,?,?,?,?,?,?,?);";
            addSoapServicesUser(jdbcTemplate, userInsert);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void addSoapServicesUser(JdbcTemplate jdbcTmp, String userInsert) {
        List<Integer> ids = jdbcTmp.queryForList("select id from users where username = ?", new Object[]{"soap-services"}, Integer.class);
        if(ids.isEmpty()) {
            jdbcTmp.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(userInsert);
                new ArgumentPreparedStatementSetter(new Object[]{
                        "soap-services",
                        Common.encrypt("soap-services"),
                        "soap-services@mail.com",
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
}
