package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_3__StorungsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE TABLE plcAlarms (\n" +
                "id INT NOT NULL auto_increment,\n" +
                "dataPointId INT DEFAULT NULL,\n" +
                "dataPointXid  VARCHAR(50) DEFAULT NULL,\n" +
                "dataPointType  VARCHAR(45) DEFAULT NULL,\n" +
                "dataPointName  VARCHAR(45) DEFAULT NULL,\n" +
                "insertTime  VARCHAR(45) DEFAULT NULL,\n" +
                "triggerTime  VARCHAR(45) DEFAULT NULL,\n" +
                "inactiveTime  VARCHAR(45) DEFAULT NULL,\n" +
                "acknowledgeTime  VARCHAR(45) DEFAULT NULL,\n" +
                "pointValue  VARCHAR(45) DEFAULT NULL,\n" +
                "description  VARCHAR(45) DEFAULT NULL,\n" +
                "uniquenessToken INT DEFAULT NULL,\n" +
                "PRIMARY KEY (id), UNIQUE(dataPointId, uniquenessToken)) ENGINE=InnoDB;");

        addColumnToDataPointsTable(jdbcTmp);
        createViews(jdbcTmp);
        createProcedure(jdbcTmp);

        jdbcTmp.execute("DROP TRIGGER IF EXISTS onlyForStorungsAndAlarmValues\n");
        jdbcTmp.execute("CREATE TRIGGER scadalts.onlyForStorungsAndAlarmValues BEFORE INSERT ON pointValues \n" +
                "FOR EACH ROW CALL notify(new.dataPointId, new.ts, new.pointValue);");

    }

    private void createProcedure(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE PROCEDURE notify(IN newDataPointId varchar(45), IN newTs varchar(45), IN newPointValue varchar(45))\n" +
                "BEGIN\n" +
                "\tDECLARE PLC_ALARM_LEVEL INT(1);\n" +
                "\tDECLARE LAST_POINT_VALUE INT(1); \n" +
                "\tDECLARE PRESENT_POINT_VALUE INT(1); \n" +
                "\tDECLARE ALARM_OR_STORUNG_WITH_HIGH_STATE_EXIST_IN_PLCALARM_FOR_POINT_NAME INT(1); \n" +
                "\tDECLARE actualIdRow INT(10); \n" +
                "\tDECLARE ALARM_IS_GEGANGEN VARCHAR(40) DEFAULT 'Alarm ist gegangen'; \n" +
                "\tDECLARE STORUNG_IS_GEGANGEN VARCHAR(40) DEFAULT 'Storung ist gegangen'; \n" +
                "\tDECLARE ALARM_AUSGELOST VARCHAR(40) DEFAULT 'Alarm ausgelost'; \n" +
                "\tDECLARE STORUNG_KOMMT VARCHAR(40) DEFAULT 'Storung kommt'; \n" +
                "\tDECLARE EMPTY_STRING VARCHAR(40) DEFAULT ' '; \n" +
                "\tDECLARE DESCRIPTION_FOR_FIRST_INSERT VARCHAR(40) DEFAULT ' '; \n" +
                "\tDECLARE TRIGGER_TIME VARCHAR(20); \n" +
                "\n" +
                "\tSELECT plcAlarmLevel INTO PLC_ALARM_LEVEL FROM dataPoints WHERE id = newDataPointId;\n" +
                "    SELECT newPointValue INTO PRESENT_POINT_VALUE;  \n" +
                "\n" +
                "\tIF (PLC_ALARM_LEVEL = 1 OR PLC_ALARM_LEVEl = 2) THEN \n" +
                "    \n" +
                "    \tSELECT pointValue INTO LAST_POINT_VALUE FROM plcAlarms WHERE id = (SELECT max(pv.id) FROM plcAlarms AS pv WHERE pv.dataPointId = newDataPointId);  \n" +
                "\t\tSELECT id INTO actualIdRow FROM plcAlarms WHERE dataPointId = newDataPointId AND uniquenessToken = 0;\n" +
                "        \n" +
                "\t\tIF (LAST_POINT_VALUE = 1 AND PRESENT_POINT_VALUE = 0 AND actualIdRow IS NOT NULL) THEN\n" +
                "\n" +
                "            IF (PLC_ALARM_LEVEL = 1) THEN           \n" +
                "\t\t\t\tUPDATE plcAlarms SET description=ALARM_IS_GEGANGEN WHERE id=actualIdRow;       \n" +
                "\t\t\tEND IF;       \n" +
                "\n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 2) THEN           \n" +
                "\t\t\t\tUPDATE plcAlarms SET description=STORUNG_IS_GEGANGEN WHERE id=actualIdRow;       \n" +
                "\t\t\tEND IF;\n" +
                "\n" +
                "\t\t\tUPDATE plcAlarms SET uniquenessToken = actualIdRow, inactiveTime = substring(from_unixtime(newTs/1000),1,19), pointValue = 0 WHERE id = actualIdRow;\n" +
                "\n" +
                "        END IF;\n" +
                "        \n" +
                "\t\tIF ((LAST_POINT_VALUE IS NULL AND PRESENT_POINT_VALUE = 1) OR (LAST_POINT_VALUE = 0 AND PRESENT_POINT_VALUE = 1 AND actualIdRow IS NULL)) THEN      \n" +
                "\n" +
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
                "\t\t\t\tdataPointId, \n" +
                "\t\t\t\tdataPointXid, \n" +
                "\t\t\t\tdataPointType, \n" +
                "\t\t\t\tdataPointName, \n" +
                "\t\t\t\tinsertTime, \n" +
                "\t\t\t\ttriggerTime, \n" +
                "\t\t\t\tinactiveTime, \n" +
                "\t\t\t\tacknowledgeTime, \n" +
                "\t\t\t\tpointValue, \n" +
                "\t\t\t\tdescription,      \n" +
                "\t\t\t\tuniquenessToken\n" +
                "\t\t\t) \n" +
                "            VALUES (\n" +
                "\t\t\t\tnewDataPointId,  \n" +
                "\t\t\t\t(SELECT xid FROM dataPoints WHERE id=newDataPointId),  \n" +
                "\t\t\t\tPLC_ALARM_LEVEL,  \n" +
                "\t\t\t\t(SELECT pointName FROM dataPoints WHERE id=newDataPointId),  \n" +
                "\t\t\t\tTRIGGER_TIME,  \n" +
                "\t\t\t\tTRIGGER_TIME,  \n" +
                "\t\t\t\tEMPTY_STRING,  \n" +
                "\t\t\t\tEMPTY_STRING,  \n" +
                "\t\t\t\tPRESENT_POINT_VALUE,  \n" +
                "\t\t\t\tDESCRIPTION_FOR_FIRST_INSERT, \n" +
                "\t\t\t\t0\n" +
                "            ) ON DUPLICATE KEY UPDATE uniquenessToken = 0;\n" +
                "            \n" +
                "\t\tEND IF;\n" +
                "\tEND IF;\n" +
                "END");
    }

    private void addColumnToDataPointsTable(JdbcTemplate jdbcTmp) throws Exception {
        //this additional column will contain ONLY data point name which trigger needs
        jdbcTmp.execute(
                new AlterTable().AlterTableWithSpecification(
                        new StringBuilder("dataPoints"),
                        new StringBuilder("pointName"),
                        AlterTable.Fields.VARCHAR,
                        250,
                        -1,
                        true)
        );

        //this additional column will have defined level of alarm as a 0-8 steps.
        jdbcTmp.execute(
                new AlterTable().AlterTableWithSpecification(
                        new StringBuilder("dataPoints"),
                        new StringBuilder("plcAlarmLevel"),
                        AlterTable.Fields.TINYINT,
                        8,
                        -1,
                        true)
        );
    }

    private void createViews(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE VIEW apiAlarmsHistory AS SELECT " +
                "inactiveTime AS 'time',\n" +
                "description AS 'description',\n" +
                "dataPointName AS 'name' \n" +
                "FROM plcalarms;\n");

        jdbcTmp.execute("CREATE VIEW apiAlarmsAcknowledge AS SELECT " +
                "id,\n" +
                "dataPointType AS 'request',\n" +
                "dataPointType AS 'error' \n" +
                "FROM plcalarms WHERE acknowledgeTime <> '' \n");

        jdbcTmp.execute("CREATE VIEW viewAllAlarms AS SELECT " +
                "* \n" +
                "FROM plcalarms WHERE dataPointType = 1;\n");

        jdbcTmp.execute("CREATE VIEW viewAllStorungs AS SELECT " +
                "* \n" +
                "FROM plcalarms WHERE dataPointType = 2;\n");

        jdbcTmp.execute("CREATE VIEW apiAlarmsLive AS SELECT " +
                "id, \n" +
                "triggerTime AS 'activation-time',\n" +
                "inactiveTime AS 'inactivation-time',\n" +
                "dataPointType AS 'level',\n" +
                "dataPointName AS 'name' \n" +
                "FROM plcalarms WHERE acknowledgeTime='' AND unix_timestamp(inactiveTime) < NOW() - INTERVAL 24 HOUR ORDER BY inactiveTime=' ' DESC, triggerTime DESC, inactiveTime DESC, id DESC;\n");

    }
}