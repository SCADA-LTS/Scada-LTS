package org.scada_lts.dao.migration.psql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @autor grzegorz.bylica@gmail.com on 12.10.2020
 */
public class V2_4__ extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        jdbcTemplate.execute(
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (SELECT 1 FROM information_schema.columns " +
                        "WHERE table_name='mangoviews' AND column_name='modification_time') THEN " +
                        "ALTER TABLE mangoViews ADD COLUMN modification_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP; " +
                        "END IF; " +
                        "END $$;"
        );

        jdbcTemplate.execute(
                "CREATE OR REPLACE FUNCTION update_modification_time() " +
                        "RETURNS TRIGGER AS $$ " +
                        "BEGIN " +
                        "  NEW.modification_time = CURRENT_TIMESTAMP; " +
                        "  RETURN NEW; " +
                        "END; " +
                        "$$ LANGUAGE plpgsql;"
        );

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM pg_trigger " +
                        "  WHERE tgname = 'trg_mangoviews_modtime' " +
                        ") THEN " +
                        "  CREATE TRIGGER trg_mangoViews_modtime " +
                        "  BEFORE UPDATE ON mangoViews " +
                        "  FOR EACH ROW " +
                        "  EXECUTE FUNCTION update_modification_time(); " +
                        "END IF; " +
                        "END $$;"
        );


    }


}
