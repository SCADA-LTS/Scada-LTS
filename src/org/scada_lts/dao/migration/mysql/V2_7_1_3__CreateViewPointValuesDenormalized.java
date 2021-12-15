package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;


public class V2_7_1_3__CreateViewPointValuesDenormalized extends BaseJavaMigration {


    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate mysqlJdbcTmp = DAO.getInstance().getJdbcTemp();
        createViewForPointValues(mysqlJdbcTmp);
    }


    public void createViewForPointValues(JdbcTemplate jdbcTmp) {

        String createViewSQL = "CREATE OR REPLACE VIEW pointValuesDenormalized AS select pv.dataPointId, pvm.pointValue, \n" +
                "concat(date(from_unixtime(pv.ts * 0.001)), \"T\", time(from_unixtime(pv.ts * 0.001)), \"Z\") as timestamp, pv.ts, \n" +
                "(SELECT JSON_OBJECT('dataType', pv.dataType, 'sourceType', pva.sourceType, \n" +
                " 'sourceId', pva.sourceId, 'username', us.username)) as metaData from \n" +
                " (select id, CONCAT_WS('', IF(dataType in (4,5), null, pointValue), pva.textPointValueShort, pva.textPointValueLong) AS pointValue \n" +
                "from pointValues pv left join pointValueAnnotations pva on pv.id = pva.pointValueId) as pvm, pointValues pv \n" +
                "left join pointValueAnnotations pva on pv.id = pva.pointValueId \n" +
                "left join users us on pva.sourceId = us.id \n" +
                "where pvm.id = pv.id;";

        jdbcTmp.execute(createViewSQL);
    }


}