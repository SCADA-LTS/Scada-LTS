package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski mateusz@gmail.com on 27.04.2020
 */
public class V2_2_0__AdditionalColumnPlcAlarmLevelInDataPointTable implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {
        final String additionaplcalarmlevelcolumnindatapointstable = ""
                + "ALTER TABLE dataPoints " +
                  "ADD COLUMN `plcAlarmLevel` TINYINT(8) NULL AFTER `data`;"; //this additional column will have defined level of alarm as a 0-8 steps.

        jdbcTmp.execute(additionaplcalarmlevelcolumnindatapointstable);
    }
}