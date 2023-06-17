package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_7_5_4__EnabledAnonymousUser extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_4__EnabledAnonymousUser.class);

    @Override
    public void migrate(Context context) throws Exception {
        try {
            final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();
            try {
                int id = jdbcTmp.queryForObject("select id from users where username='anonymous-user'", Integer.class);
            } catch (Exception ex) {
                new V2_7_0_3__AnonymousUser().migrate(context);
            }
            String updateAnonymousUser = "update users set disabled=? where username='anonymous-user';";
            jdbcTmp.update(updateAnonymousUser, "N");
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
