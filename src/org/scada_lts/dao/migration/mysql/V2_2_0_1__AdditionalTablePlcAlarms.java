package org.scada_lts.dao.migration.mysql;
/*
 * (c) 2020 hyski.mateusz@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
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

