package org.scada_lts.dao.migration.mysql;


import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @autor grzegorz.bylica@gmail.com
 */
public class V2_6__ extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        String sql = ""+
                "CREATE OR REPLACE VIEW " +
                " historyAlarms AS " +
                " SELECT " +
                "  func_fromats_date(activeTime) AS 'activeTime', " +
                "  func_fromats_date(inactiveTime) AS 'inactiveTime', " +
                "  func_fromats_date(acknowledgeTime) AS 'acknowledgeTime', " +
                "  level, " +
                "  dataPointName AS 'name', " +
                "  dataPointId AS dataPointId " +
                "FROM plcAlarms " +
                "ORDER BY " +
                " inactiveTime = 0 DESC, " +
                " inactiveTime DESC, " +
                " id DESC;";

        jdbcTmp.execute(sql);

    }
}
