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
 * @author  hyski mateusz@gmail.com on 27.04.2020
 */
public class V2_2_0__AdditionalColumnPlcAlarmLevelInDataPointTable  implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {
            jdbcTmp.execute(
                    new AlterTable().AlterTableWithSpecification(
                            new StringBuilder("dataPoints"),
                            new StringBuilder("plcAlarmLevel"),
                            AlterTable.Fields.TINYINT,
                            8,
                            0,
                            true)
                    );
            //this additional column will have defined level of alarm as a 0-8 steps.
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
