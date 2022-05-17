package org.scada_lts.dao.migration.mysql;


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

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();
        try {
            dropPrcAlarmsNotify(jdbcTmp);
            createPrcAlarmsNotify(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private static void dropPrcAlarmsNotify(JdbcTemplate jdbcTmp) {
        jdbcTmp.execute("DROP PROCEDURE IF EXISTS prc_alarms_notify");
    }

    private static void createPrcAlarmsNotify(JdbcTemplate jdbcTmp) {
        jdbcTmp.execute("CREATE PROCEDURE prc_alarms_notify(IN newDataPointId INT, IN newTs BIGINT, IN newPointValue DOUBLE)\n" +
                "BEGIN\n" +
                "\tDECLARE PLC_ALARM_LEVEL INT(1);\n" +
                "\tDECLARE PRESENT_POINT_VALUE INT(1);\n" +
                "\tDECLARE ACTUAL_ID_ROW INT UNSIGNED;\n" +
                "\tDECLARE IS_RISING_SLOPE BOOLEAN DEFAULT FALSE;\n" +
                "    DECLARE IS_FALLING_SLOPE BOOLEAN DEFAULT FALSE;\n" +
                "\n" +
                "\tSELECT plcAlarmLevel INTO PLC_ALARM_LEVEL FROM dataPoints WHERE id = newDataPointId;\n" +
                "\n" +
                "\tIF ((newPointValue=0 OR newPointValue=1) AND (PLC_ALARM_LEVEL = 1 OR PLC_ALARM_LEVEl = 2)) THEN\n" +
                "\t\n" +
                "\t\tSELECT newPointValue INTO PRESENT_POINT_VALUE;\n" +
                "\n" +
                "\t\tSELECT id INTO ACTUAL_ID_ROW FROM plcAlarms WHERE\n" +
                "\t\t\tdataPointId = newDataPointId AND\n" +
                "            inactiveTime = 0;\n" +
                "\n" +
                "\t\tSET IS_RISING_SLOPE = PRESENT_POINT_VALUE = 1 AND ACTUAL_ID_ROW IS NULL;\n" +
                "        SET IS_FALLING_SLOPE = PRESENT_POINT_VALUE = 0 AND ACTUAL_ID_ROW IS NOT NULL;\n" +
                "\n" +
                "        IF (IS_RISING_SLOPE OR IS_FALLING_SLOPE) THEN\n" +
                "\t\t\tINSERT INTO plcAlarms (\n" +
                "\t\t\t\t\tdataPointId,\n" +
                "\t\t\t\t\tdataPointXid,\n" +
                "\t\t\t\t\tdataPointType,\n" +
                "\t\t\t\t\tdataPointName,\n" +
                "\t\t\t\t\tactiveTime,\n" +
                "\t\t\t\t\tinactiveTime,\n" +
                "\t\t\t\t\tacknowledgeTime,\n" +
                "\t\t\t\t\tlevel\n" +
                "\t\t\t\t)\n" +
                "\t\t\t\tVALUES (\n" +
                "\t\t\t\t\tnewDataPointId,\n" +
                "\t\t\t\t\t(SELECT xid FROM dataPoints WHERE id = newDataPointId),\n" +
                "\t\t\t\t\tPLC_ALARM_LEVEL,\n" +
                "\t\t\t\t\t(SELECT pointName FROM dataPoints WHERE id = newDataPointId),\n" +
                "\t\t\t\t\tnewTs,\n" +
                "\t\t\t\t\t0,\n" +
                "\t\t\t\t\t0,\n" +
                "\t\t\t\t\tPLC_ALARM_LEVEL\n" +
                "\t\t\t\t) ON DUPLICATE KEY UPDATE\n" +
                "\t\t\t\t\tinactiveTime = newTs;\n" +
                "\t\tEND IF;\n" +
                "\tEND IF;" +
                "END");
    }
}
