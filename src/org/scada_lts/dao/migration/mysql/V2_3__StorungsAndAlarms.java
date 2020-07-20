package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_3__StorungsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute(""
                + "create table plcAlarms ("
                + "id mediumint(8) unsigned not null auto_increment,"
                + "pointId  varchar(45) default null,"
                + "pointXid  varchar(45) default null,"
                + "pointType  varchar(45) default null,"
                + "pointName  varchar(45) default null,"
                + "insertTime  varchar(45) default null,"
                + "triggerTime  varchar(45) default null,"
                + "inactiveTime  varchar(45) default null,"
                + "acknowledgeTime  varchar(45) default null,"
                + "lastpointValue  varchar(45) default null,"
                + "description  varchar(45) default null,"
                + "state int(1) default null,"
                + "uniquenessToken int default null,"
                + "primary key (id), UNIQUE(pointId, uniquenessToken)"
                + ") ENGINE=InnoDB;");

        addColumnToDataPointsTable(jdbcTmp);
        createViews(jdbcTmp);
        createProcedure(jdbcTmp);

        jdbcTmp.execute("DROP TRIGGER IF EXISTS onlyForStorungsAndAlarmValues\n");
        jdbcTmp.execute("CREATE TRIGGER scadalts.onlyForStorungsAndAlarmValues BEFORE INSERT ON scadalts.pointValues \n" +
                "FOR EACH ROW CALL notify(new.dataPointId, new.ts, new.pointValue);");


    }

    private void createProcedure(JdbcTemplate jdbcTmp) throws Exception {
        jdbcTmp.execute("CREATE PROCEDURE prc_sort_alarms_and_storungs_depend_on_state(in param_limit int, in param_offset int)\n" +
                "BEGIN \n" +
                "SELECT \n" +
                "`id`, \n" +
                "`activation-time`,\n" +
                "`inactivation-time`,\n" +
                "`level`,\n" +
                "`name`" +
                "FROM apiAlarmsLive LIMIT param_limit OFFSET param_offset;\n" +
                "END");

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
                "    \tSELECT pointValue INTO LAST_POINT_VALUE FROM pointValues WHERE id = (SELECT max(pv.id) FROM pointValues AS pv WHERE pv.dataPointId = newDataPointId);  \n" +
                "\t\tSELECT id INTO actualIdRow FROM plcAlarms WHERE pointId = newDataPointId AND uniquenessToken = 0;\n" +
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
                "\t\t\tUPDATE plcAlarms SET uniquenessToken = actualIdRow, inactiveTime = substring(from_unixtime(newTs/1000),1,19) WHERE id = actualIdRow;\n" +
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
                "\t\t\t\tstate,\n" +
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
                "\t\t\t\t1,\n" +
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
                "pointName AS 'name' \n" +
                "FROM plcalarms;\n");

        jdbcTmp.execute("CREATE VIEW apiAlarmsAcknowledge AS SELECT " +
                "id,\n" +
                "pointType AS 'request',\n" +
                "pointType AS 'error' \n" +
                "FROM plcalarms WHERE acknowledgeTime <> '' \n");

        jdbcTmp.execute("CREATE VIEW viewAllAlarms AS SELECT " +
                "* \n" +
                "FROM plcalarms WHERE pointType = 1;\n");

        jdbcTmp.execute("CREATE VIEW viewAllStorungs AS SELECT " +
                "* \n" +
                "FROM plcalarms WHERE pointType = 2;\n");

        jdbcTmp.execute("CREATE VIEW apiAlarmsLive AS SELECT " +
                "id, \n" +
                "triggerTime AS 'activation-time',\n" +
                "inactiveTime AS 'inactivation-time',\n" +
                "pointType AS 'level',\n" +
                "pointName AS 'name' \n" +
                "FROM plcalarms WHERE acknowledgeTime='' AND unix_timestamp(inactiveTime) < NOW() - INTERVAL 24 HOUR ORDER BY inactiveTime=' ' DESC, triggerTime DESC, inactiveTime DESC, id DESC;\n");

    }
}