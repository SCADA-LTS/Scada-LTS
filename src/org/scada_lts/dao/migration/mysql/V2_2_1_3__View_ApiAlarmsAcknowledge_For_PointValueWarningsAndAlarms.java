package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski mateusz@gmail.com on 07.05.2020
 */
public class V2_2_1_3__View_ApiAlarmsAcknowledge_For_PointValueWarningsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {


        //view for Rest API  -> /api/alarms/acknowledge

        try {
            jdbcTmp.execute(""
                    + " create view apiAlarmsAcknowledge as"
                    + "  select id,request,error from plcAlarms");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
