package org.scada_lts.dao.migration.mysql;
/*
 * (c) 2020 hyski.mateusz@gmail.com
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
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski.mateusz@gmail.com on 30.04.2020
 */
public class V2_2_0_0_1__Trigger_On_PointValues implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {
            jdbcTmp.execute( ""
                    +"CREATE TRIGGER onlyForStorungsAndAlarmValues BEFORE INSERT "
                    +"ON pointValues "
                    +"FOR EACH ROW BEGIN "
                    +"DECLARE PLC_ALARM_LEVEL INT(1); "
                    +"DECLARE LAST_POINT_VALUE INT(1); "
                    +"DECLARE PRESENT_POINT_VALUE INT(1); "
                    +"DECLARE ALARM_OR_STORUNG_WITH_HIGH_STATE_EXIST_IN_PLCALARM_FOR_POINT_NAME INT(1); "
                    +"DECLARE actualIdRow INT(10); "
                    +"DECLARE ALARM_IS_GEGANGEN VARCHAR(40) DEFAULT 'Alarm is gegangen'; "
                    +"DECLARE STORUNG_IS_GEGANGEN VARCHAR(40) DEFAULT 'Storung is gegangen'; "
                    +"DECLARE ALARM_AUSGELOST VARCHAR(40) DEFAULT 'Alarm ausgelost'; "
                    +"DECLARE STORUNG_KOMMT VARCHAR(40) DEFAULT 'Storung kommt'; "
                    +"DECLARE EMPTY_STRING VARCHAR(40) DEFAULT ' '; "
                    +"DECLARE DESCRIPTION_FOR_FIRST_INSERT VARCHAR(40) DEFAULT ' '; "

                    // TRIGGER_TIME means, when this trigger has been executed
                    +"DECLARE TRIGGER_TIME VARCHAR(20); "
                    +""
                    +" select plcAlarmLevel into PLC_ALARM_LEVEL from dataPoints where id=new.dataPointId;  "
                    +""

                    +" select new.pointValue into PRESENT_POINT_VALUE; "
                    +""

                    // PLC_ALARM_LEVEL - 1 -> for data points defined as Alarm
                    // PLC_ALARM_LEVEL - 2 -> for data points defined as Storungs

                    +" IF (PLC_ALARM_LEVEL = 1 OR PLC_ALARM_LEVEl = 2) THEN "
                    + ""
                    +" select count(*) into ALARM_OR_STORUNG_WITH_HIGH_STATE_EXIST_IN_PLCALARM_FOR_POINT_NAME from plcAlarms where pointName=(select pointName from dataPoints where id=new.dataPointId) and state=1; "

                    +" IF (ALARM_OR_STORUNG_WITH_HIGH_STATE_EXIST_IN_PLCALARM_FOR_POINT_NAME=1) THEN"
                    +""
                    + "     select id into actualIdRow from plcAlarms where pointName=(select pointName from dataPoints where id=new.dataPointId) and state=1; "

                    //update also description regarding a state which has been changed
                    +"      IF (PLC_ALARM_LEVEL = 1) THEN "
                    +"          update plcAlarms set description=ALARM_IS_GEGANGEN where id=actualIdRow; "
                    +"      END IF; "
                    +"      IF (PLC_ALARM_LEVEL = 2) THEN "
                    +"          update plcAlarms set description=STORUNG_IS_GEGANGEN where id=actualIdRow; "
                    +"      END IF; "

                    //update the state regarding a state which has been changed
                    +"      update plcAlarms set state=2,inactiveTime=substring(from_unixtime(new.ts/1000),1,19) where id=actualIdRow; "
                    +" END IF;"

                    +" IF (PRESENT_POINT_VALUE=1) THEN"
                    +""
                    // first define correct description which is needed for appeared state

                    +"      IF (PLC_ALARM_LEVEL = 1) THEN "
                    +"          SET DESCRIPTION_FOR_FIRST_INSERT = ALARM_AUSGELOST; "
                    +"      END IF; "
                    +"      IF (PLC_ALARM_LEVEL = 2) THEN "
                    +"          SET DESCRIPTION_FOR_FIRST_INSERT = STORUNG_KOMMT; "
                    +"      END IF; "

                    +"  select substring(from_unixtime(new.ts/1000),1,19) into TRIGGER_TIME; "

                    +"  insert into plcAlarms "
                    +" ("
                    +"      pointId,"
                    +"      pointXid,"
                    +"      pointType,"
                    +"      pointName,"
                    +"      insertTime,"
                    +"      triggerTime,"
                    +"      inactiveTime,"
                    +"      acknowledgeTime,"
                    +"      lastpointValue,"
                    +"      description,"
                    +"      state"
                    +"  ) "
                    +"values "
                    +" ("
                    +"  (select id from dataPoints where id=new.dataPointId),"
                    +"  (select xid from dataPoints where id=new.dataPointId),"
                    +"  PLC_ALARM_LEVEL,"
                    +"  (select pointName from dataPoints where id=new.dataPointId),"
                    +"  TRIGGER_TIME,"
                    +"  TRIGGER_TIME,"
                    +"  EMPTY_STRING,"
                    +"  EMPTY_STRING,"
                    +"  PRESENT_POINT_VALUE,"
                    +"  DESCRIPTION_FOR_FIRST_INSERT,"
                    +"  1"
                    +"); "
                    +""
                    +" END IF;"
                    +""
                    +" END IF;"
                    + ""
                    +"end; ");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}