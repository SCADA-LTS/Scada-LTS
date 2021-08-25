package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;


public class V2_7_1_0__CreateViewPointValuesDenormalized extends BaseJavaMigration {


    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate mysqlJdbcTmp = DAO.getInstance().getJdbcTemp();
        createViewForPointValues(mysqlJdbcTmp);
    }


    public void createViewForPointValues(JdbcTemplate jdbcTmp) {
        String createViewSQL = "CREATE OR REPLACE VIEW pointValuesDenormalized AS select \n" +
                "pv.dataPointId, pv.dataType, \n" +
                "pv.pointValue,  concat(date(from_unixtime(pv.ts * 0.001)), \"T\", time(from_unixtime(pv.ts * 0.001)), \"Z\") as ts, " +
                "ifnull(pva.textPointValueShort, '') as textPointValueShort, \n" +
                "ifnull(pva.textPointValueLong, '') as textPointValueLong, \n" +
                "ifnull(pva.sourceType, '') as sourceType, ifnull(pva.sourceId, '') as sourceId,\n" +
                "ifnull(us.username, '') as username\n" +
                "from pointValues pv\n" +
                "left join pointValueAnnotations pva on pv.id = pva.pointValueId\n" +
                "left join users us on pva.sourceId = us.id";

        jdbcTmp.execute(createViewSQL);
    }


}