/*
 * (c) 2019 VROC.ai https://vroc.ai/
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
 */
package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @autor grzegorz.bylica@gmail.com (SoftQ) on 03.07.19 update task timezone
 */
public class V1_5__UniqUserName implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        final String setUniqForUserName = ""
                + "alter table users " +
                "add constraint uc_username unique (`username`);";

        jdbcTemplate.execute(setUniqForUserName);
    }
}
