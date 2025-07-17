package org.scada_lts.dao.migration.psql;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;


public class V2_7_0_4_1__CorrectProcedurePrcAlarmsNotify extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_0_4_1__CorrectProcedurePrcAlarmsNotify.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        try {
            jdbcTemplate.execute(
                    "DROP TRIGGER IF EXISTS tri_notify_faults_or_alarms ON pointValues;"
            );

            jdbcTemplate.execute(
                    "DO $$ BEGIN " +
                            "IF NOT EXISTS (" +
                            "  SELECT 1 FROM pg_trigger " +
                            "  WHERE tgname = 'tri_notify_faults_or_alarms'" +
                            ") THEN " +
                            "  CREATE TRIGGER tri_notify_faults_or_alarms " +
                            "  AFTER INSERT ON pointValues " +
                            "  FOR EACH ROW " +
                            "  EXECUTE FUNCTION \"prc_alarms_notify\"(); " +
                            "END IF; " +
                            "END $$;"
            );
            dropPrcAlarmsNotify(jdbcTemplate);
            createPrcAlarmsNotify(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private static void dropPrcAlarmsNotify(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP FUNCTION IF EXISTS \"prc_alarms_notify\"() CASCADE");
    }

    private static void createPrcAlarmsNotify(JdbcTemplate jdbcTemplate) {
        String sql =
                "CREATE OR REPLACE FUNCTION \"prc_alarms_notify\"() " +
                        "RETURNS TRIGGER " +
                        "LANGUAGE plpgsql " +
                        "AS $$ " +
                        "DECLARE " +
                        "    plc_level INTEGER; " +
                        "    present_val INTEGER; " +
                        "    actual_row_id INTEGER; " +
                        "BEGIN " +
                        "    SELECT plcAlarmLevel INTO plc_level FROM dataPoints WHERE id = NEW.dataPointId; " +
                        "    present_val := NEW.pointValue::INTEGER; " +
                        "    IF plc_level IN (1, 2) THEN " +
                        "        SELECT id INTO actual_row_id " +
                        "        FROM plcAlarms " +
                        "        WHERE dataPointId = NEW.dataPointId AND inactiveTime = 0 " +
                        "        LIMIT 1; " +
                        "        IF (present_val = 1 AND actual_row_id IS NULL) " +
                        "            OR (present_val = 0 AND actual_row_id IS NOT NULL) THEN " +
                        "            INSERT INTO plcAlarms ( " +
                        "                dataPointId, " +
                        "                dataPointXid, " +
                        "                dataPointType, " +
                        "                dataPointName, " +
                        "                activeTime, " +
                        "                inactiveTime, " +
                        "                acknowledgeTime, " +
                        "                level " +
                        "            ) VALUES ( " +
                        "                NEW.dataPointId, " +
                        "                (SELECT xid FROM dataPoints WHERE id = NEW.dataPointId), " +
                        "                plc_level, " +
                        "                (SELECT pointName FROM dataPoints WHERE id = NEW.dataPointId), " +
                        "                NEW.ts, " +
                        "                0, " +
                        "                0, " +
                        "                plc_level " +
                        "            ) ON CONFLICT (dataPointId, inactiveTime) " +
                        "            DO UPDATE SET inactiveTime = EXCLUDED.inactiveTime; " +
                        "        END IF; " +
                        "    END IF; " +
                        "    RETURN NULL; " +
                        "END; " +
                        "$$;";

        jdbcTemplate.execute(sql);
    }
}
