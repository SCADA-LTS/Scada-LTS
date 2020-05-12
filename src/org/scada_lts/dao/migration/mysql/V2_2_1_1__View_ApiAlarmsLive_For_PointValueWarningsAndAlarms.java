package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
/**
 * @author  hyski mateusz@gmail.com on 07.05.2020
 */
public class V2_2_1_1__View_ApiAlarmsLive_For_PointValueWarningsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {


        //view for Rest API  -> /api/alarms/live

        try {
            jdbcTmp.execute(""
                    + " create view apiAlarmsLive as"
                    + "  select id,activationTime as 'activation-time',inactivationTime as 'inactivation-time', pointXid as 'name', pointType as 'level' from plcAlarms");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        //        + "  select id,activationTime as activation-Time, inactivationTime as inactivation-Time,name,level* from pointValues

    }
}
