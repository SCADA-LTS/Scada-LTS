package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski mateusz@gmail.com on 20.04.2020
 */

public class V2_2_0_1__AdditionalTablePlcAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {
            jdbcTmp.execute(""
                        + "create table plcAlarms ("
                        + "id mediumint(8) unsigned not null auto_increment,"
                        + "pointId  varchar(45) not null,"
                        + "pointXid  varchar(45) not null,"
                        + "pointType  varchar(45) not null,"
                        + "pointName  varchar(45) not null,"
                        + "triggerTime  varchar(45) not null,"
                        + "inactiveTime  varchar(45) not null,"
                        + "acknowledgeTime  varchar(45) not null,"
                        + "lastpointValue  varchar(45) not null,"
                        + "primary key (id)"
                        + ") ENGINE=InnoDB;");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

    }
}

