package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski mateusz@gmail.com on 27.04.2020
 */
public class V2_2_1__Views_For_PointValueWarningsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute(""
        + " create view viewStorungs as"
        + "  select * from pointValues");

        jdbcTmp.execute(""
                + " create view viewAlarms as"
                + "  select * from pointValues");

        //view for Rest API  -> /api/alarms/live

        //jdbcTmp.execute(""
        //        + " create view apiAlarmsLive as"
        //        + "  select id,activationTime as activation-Time, inactivationTime as inactivation-Time,name,level* from pointValues"




        //view for Rest API  -> /api/alarms/history

        //jdbcTmp.execute(""
        //        + " create view apiAlarmsHistory as"
        //        + "  select time, name,description* from pointValues"




        //view for Rest API  -> /api/alarms/acknowledge

        //jdbcTmp.execute(""
        //        + " create view apiAlarmsAcknowledge as"
        //        + "  select id,request,error* from pointValues"
    }
}