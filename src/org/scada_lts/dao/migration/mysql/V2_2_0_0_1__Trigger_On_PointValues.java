package org.scada_lts.dao.migration.mysql;

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
                    +"CREATE TRIGGER event_name AFTER INSERT "
                    +"ON pointValues "
                    +"FOR EACH ROW BEGIN "
                    +"DECLARE VARIABLE1 VARCHAR(100); "
                    +"DECLARE VARIABLE2 VARCHAR(100); "
                    +"SET VARIABLE1 = (SELECT UNIX_TIMESTAMP(NOW())); "
                    +"SET VARIABLE2 = (select xid from dataPoints where id=new.dataPointId); "
                    +"SET VARIABLE2 = (select if(LOCATE('AL',VARIABLE2) <> 0 ,'AL', if(LOCATE('ST',VARIABLE2) <> 0 ,'ST','undefined'))); "
                    +""
                    +"insert into pointValuesStorungsAndAlarms ("
                    +"pointId,"
                    +"pointXid,"
                    +"pointType,"
                    +"pointName,"
                    +"triggerTime,"
                    +"inactiveTime,"
                    +"acknowledgeTime,"
                    +"lastpointValue"
                    +") values ("
                    +" (select id from dataPoints where id=new.dataPointId),"
                    +" (select xid from dataPoints where id=new.dataPointId),"
                    +"'TYP',"
                    +" (select xid from dataPoints where id=new.dataPointId),"
                    +" 'event_name',"
                    +" 'inactiveTime',"
                    +" 'acknowledgeTime',"
                    +" 'lastpointValue'"
                    +"); "
                    +"SET VARIABLE2 = (SELECT UNIX_TIMESTAMP(NOW()));"
                    +"end; ");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}