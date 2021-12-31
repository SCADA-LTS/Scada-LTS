/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao.migration.query.postgres;


import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_0_0__CreatePointValuesDenormalized extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.query().getJdbcTemp();

        final String pointValueAnnotations = ""
                + "create table IF NOT EXISTS pointValuesDenormalized ("
                + "dataPointId int,"
                + "dataType int,"
                + "pointValue DOUBLE PRECISION,"
                + "ts bigint,"
                + "textPointValueShort varchar(128),"
                + "textPointValueLong text,"
                + "sourceType smallint,"
                + "sourceId int,"
                + "username varchar(40));";

        jdbcTemplate.execute(pointValueAnnotations);

    }
}
