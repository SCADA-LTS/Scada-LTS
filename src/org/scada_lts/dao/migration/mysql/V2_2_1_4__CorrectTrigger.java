package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_2_1_4__CorrectTrigger implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE PROCEDURE notify(IN newDataPointId varchar(45), IN newTs varchar(45), IN newPointValue varchar(45))\n" +
                "BEGIN\n" +
                "    DECLARE PLC_ALARM_LEVEL INT(1);\n" +
                "    DECLARE LAST_POINT_VALUE INT(1); \n" +
                "\tDECLARE PRESENT_POINT_VALUE INT(1); \n" +
                "\tDECLARE ALARM_OR_STORUNG_WITH_HIGH_STATE_EXIST_IN_PLCALARM_FOR_POINT_NAME INT(1); \n" +
                "\tDECLARE actualIdRow INT(10); \n" +
                "    DECLARE ALARM_IS_GEGANGEN VARCHAR(40) DEFAULT 'Alarm is gegangen'; \n" +
                "\tDECLARE STORUNG_IS_GEGANGEN VARCHAR(40) DEFAULT 'Storung is gegangen'; \n" +
                "\tDECLARE ALARM_AUSGELOST VARCHAR(40) DEFAULT 'Alarm ausgelost'; \n" +
                "\tDECLARE STORUNG_KOMMT VARCHAR(40) DEFAULT 'Storung kommt'; \n" +
                "\tDECLARE EMPTY_STRING VARCHAR(40) DEFAULT ' '; \n" +
                "\tDECLARE DESCRIPTION_FOR_FIRST_INSERT VARCHAR(40) DEFAULT ' '; \n" +
                "\tDECLARE TRIGGER_TIME VARCHAR(20); \n" +
                "\t\n" +
                "\tSELECT plcAlarmLevel INTO PLC_ALARM_LEVEL FROM dataPoints WHERE id=newDataPointId;   \n" +
                "\n" +
                "\tIF (PLC_ALARM_LEVEL = 1 OR PLC_ALARM_LEVEl = 2) THEN \n" +
                "    \n" +
                "    \tSELECT pointValue INTO LAST_POINT_VALUE FROM pointValues WHERE id = (SELECT max(pv.id) FROM pointValues AS pv WHERE pv.dataPointId=newDataPointId);  \n" +
                "\t\tSELECT newPointValue INTO PRESENT_POINT_VALUE;  \n" +
                "        \n" +
                "        SELECT id INTO actualIdRow FROM plcAlarms WHERE pointName=(select pointName FROM dataPoints where id=newDataPointId) AND inactiveTime = '';\n" +
                "    \n" +
                "\t\tIF (LAST_POINT_VALUE = 1 AND PRESENT_POINT_VALUE = 0 AND actualIdRow IS NOT NULL) THEN\n" +
                "\t\n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 1) THEN           \n" +
                "\t\t\t\tupdate plcAlarms SET description=ALARM_IS_GEGANGEN where id=actualIdRow;       \n" +
                "\t\t\tEND IF;       \n" +
                "\n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 2) THEN           \n" +
                "\t\t\t\tupdate plcAlarms SET description=STORUNG_IS_GEGANGEN where id=actualIdRow;       \n" +
                "\t\t\tEND IF;\n" +
                "\t\t\t\n" +
                "\t\t\tupdate plcAlarms SET state=2,inactiveTime=substring(from_unixtime(newTs/1000),1,19) where id=actualIdRow;  \n" +
                "\t\t\n" +
                "        END IF;\n" +
                "        \n" +
                "\t\tIF ((LAST_POINT_VALUE IS NULL AND PRESENT_POINT_VALUE = 1) OR (LAST_POINT_VALUE = 0 AND PRESENT_POINT_VALUE = 1 AND actualIdRow IS NULL)) THEN      \n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 1) THEN           \n" +
                "\t\t\t\tSET DESCRIPTION_FOR_FIRST_INSERT = ALARM_AUSGELOST;       \n" +
                "\t\t\tEND IF;\n" +
                "\t\t\t\n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 2) THEN           \n" +
                "\t\t\t\tSET DESCRIPTION_FOR_FIRST_INSERT = STORUNG_KOMMT;       \n" +
                "\t\t\tEND IF;\n" +
                "\n" +
                "\t\t\tSELECT substring(from_unixtime(newTs/1000),1,19) INTO TRIGGER_TIME;   \n" +
                "\n" +
                "\t\t\tINSERT INTO plcAlarms (\n" +
                "\t\t\t\tpointId, \n" +
                "\t\t\t\tpointXid, \n" +
                "\t\t\t\tpointType, \n" +
                "\t\t\t\tpointName, \n" +
                "\t\t\t\tinsertTime, \n" +
                "\t\t\t\ttriggerTime, \n" +
                "\t\t\t\tinactiveTime, \n" +
                "\t\t\t\tacknowledgeTime, \n" +
                "\t\t\t\tlastpointValue, \n" +
                "\t\t\t\tdescription,      \n" +
                "\t\t\t\tstate \n" +
                "\t\t\t) \n" +
                "            \n" +
                "\t\t\tVALUES (\n" +
                "\t\t\t\t(SELECT id FROM dataPoints WHERE id=newDataPointId),  \n" +
                "\t\t\t\t(SELECT xid FROM dataPoints WHERE id=newDataPointId),  \n" +
                "\t\t\t\tPLC_ALARM_LEVEL,  \n" +
                "\t\t\t\t(SELECT pointName FROM dataPoints WHERE id=newDataPointId),  \n" +
                "\t\t\t\tTRIGGER_TIME,  \n" +
                "\t\t\t\tTRIGGER_TIME,  \n" +
                "\t\t\t\tEMPTY_STRING,  \n" +
                "\t\t\t\tEMPTY_STRING,  \n" +
                "\t\t\t\tLAST_POINT_VALUE,  \n" +
                "\t\t\t\tDESCRIPTION_FOR_FIRST_INSERT, \n" +
                "\t\t\t\t1\n" +
                "\t\t\t);\n" +
                "\n" +
                "\t\tEND IF;\n" +
                "\tEND IF;\n" +
                "END");


        jdbcTmp.execute("DROP trigger onlyForStorungsAndAlarmValues\n");
        jdbcTmp.execute("CREATE TRIGGER onlyForStorungsAndAlarmValues BEFORE INSERT ON pointValues \n" +
                "FOR EACH ROW CALL notify(new.dataPointId, new.ts, new.pointValue);");

    }
}
