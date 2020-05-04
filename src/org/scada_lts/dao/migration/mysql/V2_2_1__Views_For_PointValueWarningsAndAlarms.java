package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski mateusz@gmail.com on 27.04.2020
 */
public class V2_2_1__Views_For_PointValueWarningsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute(""
                + " create view view1 as"
                + "  select * from pointValues");
    }
}