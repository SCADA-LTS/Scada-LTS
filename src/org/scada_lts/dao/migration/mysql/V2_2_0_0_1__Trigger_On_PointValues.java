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

                    // TRIGGER_TIME means, when this trigger has been executed


                    +"DECLARE TRIGGER_TIME VARCHAR(20);"
                    +""
                    +" select plcAlarmLevel into PLC_ALARM_LEVEL from dataPoints where id=new.dataPointId;  "
                    +""
                    +" select pointValue into LAST_POINT_VALUE from pointValues where ts=(select max(ts) from pointValues where dataPointId=new.dataPointId); "
                    +""
                    +" select from_unixtime(unix_timestamp()) into TRIGGER_TIME; "
                    +""
                    +" IF (PLC_ALARM_LEVEL = 1 OR PLC_ALARM_LEVEl = 2) THEN "
                    + ""
                    +"insert into plcAlarms ("
                    +"  pointId,"
                    +"  pointXid,"
                    +"  pointType,"
                    +"  pointName,"
                    +"  insertTime,"
                    +"  triggerTime,"
                    +"  inactiveTime,"
                    +"  acknowledgeTime,"
                    +"  lastpointValue"
                    +") values ("
                    +"  (select id from dataPoints where id=new.dataPointId),"
                    +"  (select xid from dataPoints where id=new.dataPointId),"
                    +"  PLC_ALARM_LEVEL,"
                    +"  (select pointName from dataPoints where id=new.dataPointId),"
                    +"  TRIGGER_TIME,"
                    +"  null,"
                    +"  null,"
                    +"  (select max(ts) from pointValues where dataPointId=new.dataPointId),"
                    +" LAST_POINT_VALUE"
                    +"); "
                    +""
                    +" select new.pointValue into PRESENT_POINT_VALUE; "
                    +""

                    // point has been changed status from  1 -ACTIVE ALARM / STORUNG to 0 -INACTIVE ALARM / STORUNG
                    // state 1-0

                    +" IF (LAST_POINT_VALUE = 1 AND PRESENT_POINT_VALUE = 0) THEN "
                    +" update plcAlarms set inactiveTime=(select from_unixtime(unix_timestamp())) where insertTime=TRIGGER_TIME; "
                    +" END IF;"

                    // point has been changed status from 0 -INACTIVE ALARM / STORUNG  to 1 -ACTIVE ALARM / STORUNG
                    // state 0-1

                    +""
                    +" IF (LAST_POINT_VALUE = 0 AND PRESENT_POINT_VALUE = 1) THEN "
                    +" update plcAlarms set triggerTime=(select from_unixtime(unix_timestamp())) where insertTime=TRIGGER_TIME; "
                    +" END IF;"
                    +""
                    +" END IF;"
                    + ""
                    + ""
                    +"end; ");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}