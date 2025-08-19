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
package org.scada_lts.dao.migration.postgres;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_3__SetXidPointHierarchy extends BaseJavaMigration {

    private static final Logger LOG = LoggerFactory.getLogger(V1_3__SetXidPointHierarchy.class);

    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        final String addXidInPointHierarchy = ""
                + "DO $$ BEGIN "
                + "IF NOT EXISTS (SELECT 1 FROM information_schema.columns "
                + "WHERE table_name='pointhierarchy' AND column_name='xid') THEN "
                + "ALTER TABLE pointhierarchy ADD COLUMN xid VARCHAR(100); "
                + "END IF; "
                + "END $$;";

        jdbcTemplate.execute(addXidInPointHierarchy);

        final String dropPrcIfExist =
                "DROP FUNCTION IF EXISTS func_gen_xid_point_hierarchy(INT);";

        jdbcTemplate.execute(dropPrcIfExist);

        final String addPrcGenerateXid =
                "CREATE FUNCTION func_gen_xid_point_hierarchy(id INTEGER) "
                        + "RETURNS TEXT LANGUAGE sql AS $$ "
                        + "  SELECT 'DIR_' || FLOOR(EXTRACT(EPOCH FROM NOW())) || '_' || id; "
                        + "$$;";

        jdbcTemplate.execute(addPrcGenerateXid);

        final String runPrcToGenerateXid =
                "UPDATE pointhierarchy SET xid=func_gen_xid_point_hierarchy(id)";

        jdbcTemplate.execute(runPrcToGenerateXid);

        final String addConstraintUniqueXidPointHierarchy =
                "ALTER TABLE pointHierarchy ADD CONSTRAINT unique_xid_point_hierarchy UNIQUE (xid);";

        try {
            jdbcTemplate.execute(addConstraintUniqueXidPointHierarchy);
        } catch (Exception e) {
            LOG.warn("Constraint unique_xid_point_hierarchy already exists, skipping.");
        }

        final String addIndex = ""
                + "DO $$ BEGIN "
                + "IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_xid_point_hierarchy') THEN "
                + "CREATE INDEX idx_xid_point_hierarchy ON pointhierarchy (xid); "
                + "END IF; "
                + "END $$;";

        jdbcTemplate.execute(addIndex);


    }
}
