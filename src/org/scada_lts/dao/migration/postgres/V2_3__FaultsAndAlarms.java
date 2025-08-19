/*
 * (c) 2020 hyski.mateusz@gmail.com, kamil.jarmusik@gmail.com
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

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;


public class V2_3__FaultsAndAlarms extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_3__FaultsAndAlarms.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        //V2_3__FaultsAndAlarms

        jdbcTemplate.execute("DROP VIEW IF EXISTS apiAlarmsAcknowledge;");
        //(1)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.3';");

        jdbcTemplate.execute("DROP VIEW IF EXISTS apiAlarmsHistory;");
        //(2)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.2';");

        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS prc_sort_alarms_and_storungs_depend_on_state;");
        jdbcTemplate.execute("DROP TABLE IF EXISTS \"tmp_sortedAlarmsStorungs\";");
        //(3)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.1.1';");

        jdbcTemplate.execute("DROP VIEW IF EXISTS apiAlarmsLive;");
        //(4)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.1';");

        jdbcTemplate.execute("DROP VIEW IF EXISTS viewAllStorungs;");
        jdbcTemplate.execute("DROP VIEW IF EXISTS viewAllAlarms;");
        //(5)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1';");

        try {
            jdbcTemplate.execute("ALTER TABLE dataPoints DROP COLUMN pointName;");
        } catch (Exception e) {
            LOG.warn(String.valueOf(e));
        }
        //(6)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0.2';");

        jdbcTemplate.execute("DROP TABLE IF EXISTS plcAlarms CASCADE;");
        //(7)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0.1';");

        //(8)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0.0.1';");

        try {
            jdbcTemplate.execute("ALTER TABLE dataPoints DROP COLUMN plcAlarmLevel;");
        } catch (Exception e) {
            LOG.warn(String.valueOf(e));
        }
        //(9)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0';");


        addColumnsToDataPointsTable(jdbcTemplate);
        updateDataPointsTable(jdbcTemplate);
        createPlcAlarmsTable(jdbcTemplate);
        createFunctions(jdbcTemplate);
        createViews(jdbcTemplate);
        createProcedure(jdbcTemplate);
        createTrigger(jdbcTemplate);

    }

    private void addColumnsToDataPointsTable(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute(
                "ALTER TABLE dataPoints ADD COLUMN pointName VARCHAR(250);"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN dataPoints.pointName IS 'copy point name from data';"
        );

        jdbcTemplate.execute(
                "ALTER TABLE dataPoints ADD COLUMN plcAlarmLevel SMALLINT;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN dataPoints.plcAlarmLevel IS '1 - FAULT, 2 - ALARM';"
        );
    }

    private void updateDataPointsTable(JdbcTemplate jdbcTemplate) throws Exception {

        try {
            List<DataPointVO> dataPoints = jdbcTemplate.query("SELECT id, data FROM dataPoints", (resultSet, i) -> {
                try (InputStream inputStream = resultSet.getBinaryStream("data");
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    DataPointVO dataPointVO = (DataPointVO) objectInputStream.readObject();
                    dataPointVO.setId(resultSet.getInt("id"));
                    return dataPointVO;
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    return null;
                }
            });

            boolean isNull = dataPoints.stream().anyMatch(Objects::isNull);
            if (isNull) {
                throw new IllegalStateException("DataPointVO is null!");
            }

            for (DataPointVO dataPointPart : dataPoints) {
                String dataPointName = dataPointPart.getName();
                int plcAlarmLevel = 0;
                if (dataPointName.contains(" AL ")) {
                    plcAlarmLevel = 2;
                }
                if (dataPointName.contains(" ST ")) {
                    plcAlarmLevel = 1;
                }
                jdbcTemplate.update("UPDATE dataPoints SET plcAlarmLevel = ?, pointName = ? WHERE id = ?",
                        plcAlarmLevel, dataPointName, dataPointPart.getId());
            }
        } catch (EmptyResultDataAccessException empty) {
            LOG.warn(String.valueOf(empty));
        }

    }

    private void createPlcAlarmsTable(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS plcAlarms (\n" +
                        "  id SERIAL PRIMARY KEY,\n" +
                        "  dataPointId INTEGER NOT NULL,\n" +
                        "  dataPointXid VARCHAR(50),\n" +
                        "  dataPointType VARCHAR(45),\n" +
                        "  dataPointName VARCHAR(45),\n" +
                        "  activeTime BIGINT DEFAULT 0,\n" +
                        "  inactiveTime BIGINT DEFAULT 0,\n" +
                        "  acknowledgeTime BIGINT DEFAULT 0,\n" +
                        "  level SMALLINT,\n" +
                        "  UNIQUE (dataPointId, inactiveTime),\n" +
                        "  FOREIGN KEY (dataPointId) REFERENCES dataPoints(id) ON DELETE CASCADE\n" +
                        ");"
        );
    }

    private void createFunctions(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.execute(
                "CREATE OR REPLACE FUNCTION func_fromats_date(ts BIGINT)\n" +
                        "  RETURNS VARCHAR(19)\n" +
                        "  LANGUAGE plpgsql AS $$\n" +
                        "BEGIN\n" +
                        "  IF ts = 0 THEN\n" +
                        "    RETURN ' ';\n" +
                        "  END IF;\n" +
                        "  RETURN to_char(to_timestamp(ts/1000.0), 'YYYY-MM-DD HH24:MI:SS');\n" +
                        "END;\n" +
                        "$$;"
        );
    }

    private void createViews(JdbcTemplate jdbcTemplate) throws Exception {


        jdbcTemplate.execute(
                "CREATE VIEW historyAlarms AS\n" +
                        " SELECT\n" +
                        "   func_fromats_date(activeTime) AS activeTime,\n" +
                        "   func_fromats_date(inactiveTime) AS inactiveTime,\n" +
                        "   func_fromats_date(acknowledgeTime) AS acknowledgeTime,\n" +
                        "   level,\n" +
                        "   dataPointName AS name\n" +
                        " FROM plcAlarms\n" +
                        " ORDER BY (inactiveTime = 0) DESC, inactiveTime DESC, id DESC;"
        );

        jdbcTemplate.execute(
                "CREATE VIEW liveAlarms AS\n" +
                        " SELECT\n" +
                        "   id,\n" +
                        "   func_fromats_date(activeTime) AS activation_time,\n" +
                        "   func_fromats_date(inactiveTime) AS inactivation_time,\n" +
                        "   level,\n" +
                        "   dataPointName AS name\n" +
                        " FROM plcAlarms\n" +
                        " WHERE acknowledgeTime = 0\n" +
                        "   AND (inactiveTime = 0\n" +
                        "        OR inactiveTime > (EXTRACT(EPOCH FROM now() - INTERVAL '24 hours') * 1000))\n" +
                        " ORDER BY (inactiveTime = 0) DESC, activeTime DESC, inactiveTime DESC, id DESC;"
        );
    }

    private void createProcedure(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.execute(
                "CREATE OR REPLACE FUNCTION prc_alarms_notify() RETURNS TRIGGER\n" +
                        "LANGUAGE plpgsql AS $$\n" +
                        "DECLARE\n" +
                        "  plc_level SMALLINT;\n" +
                        "  present_val INTEGER;\n" +
                        "  actual_row_id INTEGER;\n" +
                        "BEGIN\n" +
                        "  SELECT plcAlarmLevel INTO plc_level FROM dataPoints WHERE id = NEW.dataPointId;\n" +
                        "  present_val := NEW.pointValue::INTEGER;\n" +
                        "\n" +
                        "  IF plc_level IN (1,2) THEN\n" +
                        "    SELECT id INTO actual_row_id\n" +
                        "      FROM plcAlarms\n" +
                        "     WHERE dataPointId = NEW.dataPointId AND inactiveTime = 0\n" +
                        "     LIMIT 1;\n" +
                        "\n" +
                        "    IF (present_val = 1 AND actual_row_id IS NULL)\n" +
                        "       OR (present_val = 0 AND actual_row_id IS NOT NULL) THEN\n" +
                        "      INSERT INTO plcAlarms (\n" +
                        "        dataPointId,\n" +
                        "        dataPointXid,\n" +
                        "        dataPointType,\n" +
                        "        dataPointName,\n" +
                        "        activeTime,\n" +
                        "        inactiveTime,\n" +
                        "        acknowledgeTime,\n" +
                        "        level\n" +
                        "      )\n" +
                        "      VALUES (\n" +
                        "        NEW.dataPointId,\n" +
                        "        (SELECT xid FROM dataPoints WHERE id = NEW.dataPointId),\n" +
                        "        plc_level,\n" +
                        "        (SELECT pointName FROM dataPoints WHERE id = NEW.dataPointId),\n" +
                        "        NEW.ts,\n" +
                        "        0,\n" +
                        "        0,\n" +
                        "        plc_level\n" +
                        "      )\n" +
                        "      ON CONFLICT (dataPointId, inactiveTime)\n" +
                        "      DO UPDATE SET inactiveTime = EXCLUDED.inactiveTime;\n" +
                        "    END IF;\n" +
                        "  END IF;\n" +
                        "\n" +
                        "  RETURN NULL;\n" +
                        "END;\n" +
                        "$$;"
        );

    }

    private void createTrigger(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "    SELECT 1 FROM pg_trigger WHERE tgname = 'tri_notify_faults_or_alarms' " +
                        ") THEN " +
                        "    CREATE TRIGGER tri_notify_faults_or_alarms " +
                        "    AFTER INSERT ON pointValues " +
                        "    FOR EACH ROW " +
                        "    EXECUTE FUNCTION prc_alarms_notify(); " +
                        "END IF; " +
                        "END $$;"
        );

    }
}