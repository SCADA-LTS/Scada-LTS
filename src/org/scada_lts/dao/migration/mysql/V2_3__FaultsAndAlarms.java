package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.vo.DataPointVO;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_3__FaultsAndAlarms implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {
            addColumnsAndUpdateInDataPointsTable(jdbcTmp);
            createTable(jdbcTmp);
            createViews(jdbcTmp);
            createProcedure(jdbcTmp);
            createTrigger(jdbcTmp);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

    private void addColumnsAndUpdateInDataPointsTable(JdbcTemplate jdbcTmp) throws Exception {

        //this additional column will contain ONLY data point name which trigger needs
        jdbcTmp.execute("ALTER TABLE dataPoints ADD pointName VARCHAR(250);");

        //this additional column will have defined level of alarm as a 0-8 steps.
        jdbcTmp.execute("ALTER TABLE dataPoints ADD plcAlarmLevel TINYINT(8);");

        List<DataPointVO> dataPoints = jdbcTmp.query("SELECT id, data FROM dataPoints", (resultSet, i) -> {
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
        if(isNull) {
            throw new IllegalStateException("DataPointVO is null!");
        }

        for(DataPointVO dataPointPart: dataPoints) {
            String dataPointName = dataPointPart.getName();
            int plcAlarmLevel = 0;
            if(dataPointName.contains(" AL ")) {
                plcAlarmLevel = 3;
            }
            if(dataPointName.contains(" ST ")) {
                plcAlarmLevel = 2;
            }
            jdbcTmp.update("UPDATE dataPoints SET plcAlarmLevel = ?, pointName = ? WHERE id = ?",
                    plcAlarmLevel, dataPointName, dataPointPart.getId());
        }

    }

    private void createTable(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE TABLE plcAlarms (\n" +
                "id INT NOT NULL auto_increment,\n" +
                "dataPointId INT NOT NULL,\n" +
                "dataPointXid  VARCHAR(50) DEFAULT NULL,\n" +
                "dataPointType  VARCHAR(45) DEFAULT NULL,\n" +
                "dataPointName  VARCHAR(45) DEFAULT NULL,\n" +
                "insertTime  VARCHAR(45) DEFAULT NULL,\n" +
                "triggerTime  VARCHAR(45) DEFAULT NULL,\n" +
                "inactiveTime  VARCHAR(45) DEFAULT NULL,\n" +
                "acknowledgeTime  VARCHAR(45) DEFAULT NULL,\n" +
                "pointValue  VARCHAR(45) DEFAULT NULL,\n" +
                "description  VARCHAR(45) DEFAULT NULL,\n" +
                "uniquenessToken INT NOT NULL,\n" +
                "PRIMARY KEY (id), FOREIGN KEY (dataPointId) REFERENCES dataPoints(id) ON DELETE CASCADE," +
                "UNIQUE(dataPointId, uniquenessToken)) ENGINE=InnoDB;");


    }

    private static void createViews(JdbcTemplate jdbcTmp) throws Exception {


        jdbcTmp.execute("CREATE VIEW historyAlarms AS SELECT " +
                "inactiveTime AS 'time',\n" +
                "description AS 'description',\n" +
                "dataPointName AS 'name' \n" +
                "FROM plcAlarms ORDER BY inactiveTime DESC, id DESC;\n");

        jdbcTmp.execute("CREATE VIEW liveAlarms AS SELECT " +
                "id, \n" +
                "triggerTime AS 'activation-time',\n" +
                "inactiveTime AS 'inactivation-time',\n" +
                "dataPointType AS 'level',\n" +
                "dataPointName AS 'name' \n" +
                "FROM plcAlarms WHERE acknowledgeTime='' AND unix_timestamp(inactiveTime) < NOW() - INTERVAL 24 HOUR ORDER BY inactiveTime=' ' DESC, triggerTime DESC, inactiveTime DESC, id DESC;\n");

    }

    private void createProcedure(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE PROCEDURE notify(IN newDataPointId varchar(45), IN newTs varchar(45), IN newPointValue varchar(45))\n" +
                "BEGIN\n" +
                "\tDECLARE PLC_ALARM_LEVEL INT(1);\n" +
                "\tDECLARE LAST_POINT_VALUE INT(1); \n" +
                "\tDECLARE PRESENT_POINT_VALUE INT(1); \n" +
                "\tDECLARE ACTUAL_ID_ROW INT(10); \n" +
                "\tDECLARE ALARM_IST_GEGANGEN VARCHAR(40) DEFAULT 'plcalarms.alarm.inactive'; \n" +
                "\tDECLARE STORUNG_IST_GEGANGEN VARCHAR(40) DEFAULT 'plcalarms.fault.inactive'; \n" +
                "\tDECLARE ALARM_AUSGELOST VARCHAR(40) DEFAULT 'plcalarms.alarm.active'; \n" +
                "\tDECLARE STORUNG_KOMMT VARCHAR(40) DEFAULT 'plcalarms.fault.active';  \n" +
                "\tDECLARE EMPTY_STRING VARCHAR(40) DEFAULT ' '; \n" +
                "\tDECLARE DESCRIPTION_FOR_FIRST_INSERT VARCHAR(40) DEFAULT ' '; \n" +
                "\tDECLARE TRIGGER_TIME VARCHAR(20); \n" +
                "\n" +
                "\tSELECT plcAlarmLevel INTO PLC_ALARM_LEVEL FROM dataPoints WHERE id = newDataPointId;\n" +
                "    SELECT newPointValue INTO PRESENT_POINT_VALUE;  \n" +
                "\n" +
                "\tIF (PLC_ALARM_LEVEL = 3 OR PLC_ALARM_LEVEl = 2) THEN \n" +
                "    \n" +
                "    \tSELECT pointValue INTO LAST_POINT_VALUE FROM plcAlarms WHERE id = (SELECT max(pv.id) FROM plcAlarms AS pv WHERE pv.dataPointId = newDataPointId);  \n" +
                "\t\tSELECT id INTO ACTUAL_ID_ROW FROM plcAlarms WHERE dataPointId = newDataPointId AND uniquenessToken = 0;\n" +
                "        \n" +
                "\t\tIF (LAST_POINT_VALUE = 1 AND PRESENT_POINT_VALUE = 0 AND ACTUAL_ID_ROW IS NOT NULL) THEN\n" +
                "\n" +
                "            IF (PLC_ALARM_LEVEL = 3) THEN           \n" +
                "\t\t\t\tSET DESCRIPTION_FOR_FIRST_INSERT = ALARM_IST_GEGANGEN;      \n" +
                "\t\t\tEND IF;       \n" +
                "\n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 2) THEN           \n" +
                "\t\t\t\tSET DESCRIPTION_FOR_FIRST_INSERT = STORUNG_IST_GEGANGEN;       \n" +
                "\t\t\tEND IF;\n" +
                "\n" +
                "\t\t\tUPDATE plcAlarms SET description = DESCRIPTION_FOR_FIRST_INSERT, uniquenessToken = ACTUAL_ID_ROW, inactiveTime = substring(from_unixtime(newTs/1000),1,19), pointValue = 0 WHERE id = ACTUAL_ID_ROW;\n" +
                "\n" +
                "        END IF;\n" +
                "        \n" +
                "\t\tIF ((LAST_POINT_VALUE IS NULL AND PRESENT_POINT_VALUE = 1) OR (LAST_POINT_VALUE = 0 AND PRESENT_POINT_VALUE = 1 AND ACTUAL_ID_ROW IS NULL)) THEN      \n" +
                "\n" +
                "\t\t\tIF (PLC_ALARM_LEVEL = 3) THEN           \n" +
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

    private void createTrigger(JdbcTemplate jdbcTmp) throws Exception {

        jdbcTmp.execute("CREATE TRIGGER notifyFaultsOrAlarms AFTER INSERT ON pointValues \n" +
                "FOR EACH ROW CALL notify(new.dataPointId, new.ts, new.pointValue);");

    }
}