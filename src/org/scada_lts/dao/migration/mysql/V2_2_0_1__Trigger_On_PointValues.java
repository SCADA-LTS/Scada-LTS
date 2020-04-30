package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski.mateusz@gmail.com on 30.04.2020
 */
public class V2_2_0_1__Trigger_On_PointValues implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {
        final String addTrigger = "";


       // jdbcTmp.execute(addTrigger);
    }
}