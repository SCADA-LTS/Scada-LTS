/*
 * (c) 2018 grzegorz.bylica@gmail.com
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
package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
public class V1_3__SetXidPointHierarchy extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        // alter table pointHierarchy drop column `xid`
        final String addXidInPointHierarchy =
                "ALTER TABLE pointHierarchy ADD COLUMN `xid` VARCHAR(100)  AFTER `name`;";

        jdbcTmp.execute(addXidInPointHierarchy);

        // create procedure to generate_xid
        final String dropPrcIfExist =
                "DROP FUNCTION IF EXISTS func_gen_xid_point_hierarchy;";

        jdbcTmp.execute(dropPrcIfExist);

        final String addPrcGenerateXid =
                "CREATE FUNCTION func_gen_xid_point_hierarchy(id INT(10)) "
                + "RETURNS VARCHAR(100) "
                + "BEGIN "
                + " RETURN CONCAT(\"DIR_\", UNIX_TIMESTAMP(),\"_\", id); "
                + "END";

        jdbcTmp.execute(addPrcGenerateXid);

        // complete the data for xid for the existing data.
        final String runPrcToGenerateXid =
                "UPDATE pointHierarchy SET xid=func_gen_xid_point_hierarchy(id)";

        jdbcTmp.execute(runPrcToGenerateXid);

        // add constraint unique
        final String addConstraintUniqueXidPointHierarchy =
                "ALTER TABLE pointHierarchy ADD CONSTRAINT unique_xid_point_hierarchy UNIQUE (xid);";

        jdbcTmp.execute(addConstraintUniqueXidPointHierarchy);

        // add index on xid
        final String addIdex =
                "ALTER TABLE pointHierarchy ADD INDEX idx_xid_point_hierarchy (xid);";

        jdbcTmp.execute(addIdex);

    }
}
