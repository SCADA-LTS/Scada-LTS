package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_5_4__RemovedAnonymousUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_4__RemovedAnonymousUser.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        String deleteUser = "delete from users where username=?;";

        jdbcTmp.update(deleteUser, "anonymous-user");
    }
}
