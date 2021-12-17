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

        String createViewSQL = "CREATE OR REPLACE VIEW pointValuesDenormalized AS " +
                "SELECT " +
                "pv.dataPointId, " +
                "pvm.pointValue, " +
                "CONCAT(DATE(FROM_UNIXTIME(pv.ts * 0.001)), \"T\", TIME(FROM_UNIXTIME(pv.ts * 0.001)), \"Z\") AS timestamp, " +
                "pv.ts, " +
                "(SELECT REPLACE(JSON_OBJECT('dataType', pv.dataType, 'sourceType', pva.sourceType, " +
                " 'sourceId', pva.sourceId, 'username', us.username), '\"', '')) AS metaData " +
                "FROM " +
                " (SELECT id, CONCAT_WS('', IF(dataType IN (4,5), null, pointValue), pva.textPointValueShort, pva.textPointValueLong) AS pointValue " +
                "FROM pointValues pv LEFT JOIN pointValueAnnotations pva ON pv.id = pva.pointValueId) AS pvm, pointValues pv " +
                "LEFT JOIN pointValueAnnotations pva ON pv.id = pva.pointValueId " +
                "LEFT JOIN users us ON pva.sourceId = us.id " +
                "WHERE pvm.id = pv.id;";

        jdbcTmp.execute(createViewSQL);
    }


}