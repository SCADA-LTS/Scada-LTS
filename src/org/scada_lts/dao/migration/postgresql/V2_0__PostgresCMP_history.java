package org.scada_lts.dao.migration.postgresql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_0__PostgresCMP_history extends BaseJavaMigration {

    private static final Logger LOG = LoggerFactory.getLogger(V1__PostgresBaseVersion.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        final String multiChangesHistory = ""
                + "create table IF NOT EXISTS multi_changes_history ("
                + "id SERIAL,"                                           // id
                + "userId INTEGER,"                                      // the user id of the user making the record in the database
                + "username varchar(50),"                                // the name of the user who writes the values
                + "viewAndComponentIdentification varchar(50) not null," // component and view identifier
                + "interpretedState varchar(50) not null,"               // the state we were trying to get into
                + "ts bigint,"                                       // recording time
                + "primary key (id)"
                + ")";

        final String valuesMultiChangesHistory = ""
                + "create table IF NOT EXISTS values_multi_changes_history ("
                + "id SERIAL,"                                          // id
                + "multiChangesHistoryId INTEGER,"                      // id to the multi_changes_history table ( without a foreign key)
                + "valueId bigint,"                                 // id of the value stored in pointValues (without a foreign key)
                + "value varchar(50) not null,"                         // a copy of the value recorded in pointValues
                + "dataPointId INTEGER,"                                // id to the dataPoins table (without a foreign key)
                + "ts bigint,"                                         // recording time
                + "primary key (id)"
                + ")";

        final String prcAddCMPHistory = ""
                + "CREATE OR REPLACE PROCEDURE \"prc_add_cmp_history\"(\n"
                + "    IN a_userId INTEGER,\n"
                + "    IN a_viewAndCmpId VARCHAR(50),\n"
                + "    IN a_interpretedState VARCHAR(50),\n"
                + "    IN a_ts BIGINT,\n"
                + "    IN a_list_of_values JSON\n"
                + ")\n"
                + "LANGUAGE plpgsql\n"
                + "AS $$\n"
                + "DECLARE\n"
                + "    v_usr_name VARCHAR(50);\n"
                + "    v_multiChangesHistoryId INTEGER;\n"
                + "    v_length INTEGER := json_array_length(a_list_of_values);\n"
                + "    v_index INTEGER := 0;\n"
                + "    v_data_point_id INTEGER;\n"
                + "    v_data_point_value VARCHAR(50);\n"
                + "BEGIN\n"
                + "    SELECT username INTO v_usr_name FROM users WHERE id = a_userId;\n"
                + "\n"
                + "    INSERT INTO multi_changes_history (userId, username, viewAndComponentIdentification, interpretedState, ts)\n"
                + "    VALUES (a_userId, v_usr_name, a_viewAndCmpId, a_interpretedState, a_ts);\n"
                + "\n"
                + "    SELECT currval(pg_get_serial_sequence('multi_changes_history', 'id')) INTO v_multiChangesHistoryId;\n"
                + "\n"
                + "    WHILE v_index < v_length LOOP\n"
                + "        SELECT json_extract_path_text(elem, 'value') INTO v_data_point_value\n"
                + "        FROM json_array_elements(a_list_of_values) WITH ORDINALITY AS arr(elem, ord)\n"
                + "        WHERE ord = v_index + 1;\n"
                + "\n"
                + "        SELECT id INTO v_data_point_id\n"
                + "        FROM dataPoints\n"
                + "        WHERE xid = (\n"
                + "            SELECT json_extract_path_text(elem, 'xid')\n"
                + "            FROM json_array_elements(a_list_of_values) WITH ORDINALITY AS arr(elem, ord)\n"
                + "            WHERE ord = v_index + 1\n"
                + "        );\n"
                + "\n"
                + "        INSERT INTO values_multi_changes_history (multiChangesHistoryId, value, dataPointId, ts)\n"
                + "        VALUES (v_multiChangesHistoryId, v_data_point_value, v_data_point_id, a_ts);\n"
                + "\n"
                + "        v_index := v_index + 1;\n"
                + "    END LOOP;\n"
                + "\n"
                + "    CREATE TEMP TABLE tmp_to_delete AS\n"
                + "        SELECT id FROM multi_changes_history\n"
                + "        WHERE viewAndComponentIdentification = a_viewAndCmpId\n"
                + "        ORDER BY ts DESC\n"
                + "        LIMIT 10;\n"
                + "\n"
                + "    DELETE FROM multi_changes_history\n"
                + "    WHERE id NOT IN (SELECT id FROM tmp_to_delete);\n"
                + "\n"
                + "    DROP TABLE tmp_to_delete;\n"
                + "END;\n"
                + "$$;";

        jdbcTemplate.execute(multiChangesHistory);
        jdbcTemplate.execute(valuesMultiChangesHistory);
        jdbcTemplate.execute(prcAddCMPHistory);
    }
}