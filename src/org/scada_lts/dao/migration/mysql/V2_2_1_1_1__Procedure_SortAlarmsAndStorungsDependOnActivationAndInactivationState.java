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
 * @author  hyski mateusz@gmail.com on 07.05.2020
 */
public class V2_2_1_1_1__Procedure_SortAlarmsAndStorungsDependOnActivationAndInactivationState implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {


        //here is a full name because flyway can't add a procedure with this because of too long
        //so...

        //procedure  prc_sort_alarms_and_storungs_depend_on_activation_and_inactivation_state

        try {
            jdbcTmp.execute(""
                    + "create procedure prc_sort_alarms_and_storungs_depend_on_state( "
                    + " in param_limit int(3),"
                    + " in param_offset int(3)"
                    + ") "
                    + "begin "
                    + ""
                    + " DROP TEMPORARY TABLE IF EXISTS tmp_sortedAlarmsStorungs;"

                    + "CREATE TEMPORARY TABLE tmp_sortedAlarmsStorungs ( "
                    + " id int(11) AUTO_INCREMENT,"
                    + " pointId  varchar(45) default null,"
                    + " pointXid  varchar(45) default null,"
                    + " pointType  varchar(45) default null,"
                    + " pointName  varchar(45) default null,"
                    + " insertTime  varchar(45) default null,"
                    + " triggerTime  varchar(45) default null,"
                    + " inactiveTime  varchar(45) default null,"
                    + " acknowledgeTime  varchar(45) default null,"
                    + " lastpointValue  varchar(45) default null,"
                    + " description  varchar(45) default null,"
                    + " state int(1) default null,"
                    + " primary key (id) "
                    + " );"
                    + ""
                    //first group Storungs
                    // sort Storungs activatio time -> triggerTime
                    + " insert into tmp_sortedAlarmsStorungs("
                    + " pointId,pointXid,pointType,pointName,insertTime,"
                    + " triggerTime,inactiveTime,acknowledgeTime,lastpointValue,description,state) "
                    + ""
                    + " select pointId,pointXid,pointType,pointName,insertTime,"
                    + " triggerTime,inactiveTime,acknowledgeTime,lastpointValue,description,state "
                    + " from plcAlarms where pointType=2 and inactiveTime='' and acknowledgeTime=''"
                    + " order by triggerTime desc;"
                    + ""
                    //second one group Alarms
                    // sort Alarms activatio time -> triggerTime
                    + " insert into tmp_sortedAlarmsStorungs(pointId,pointXid,pointType,pointName,insertTime,"
                    + " triggerTime,inactiveTime,acknowledgeTime,lastpointValue,description,state)"
                    + ""
                    + " select pointId,pointXid,pointType,pointName,insertTime,"
                    + " triggerTime,inactiveTime,acknowledgeTime,lastpointValue,description,state "
                    + " from plcAlarms where pointType=1 and inactiveTime='' and acknowledgeTime=''"
                    + " order by triggerTime desc;"
                    + ""
                    //third one group -> all Storungs and Alarms with activation and inactivation time
                    // alarms and storungs with both times - activation and inactivation
                    + " insert into tmp_sortedAlarmsStorungs(pointId,pointXid,pointType,pointName,insertTime,"
                    + " triggerTime,inactiveTime,acknowledgeTime,lastpointValue,description,state) "
                    + ""
                    + " select pointId,pointXid,pointType,pointName,insertTime,"
                    + " triggerTime,inactiveTime,acknowledgeTime,lastpointValue,description,state "
                    + " from plcAlarms where pointType in (1,2) and inactiveTime<>'' and acknowledgeTime=''"
                    + " order by triggerTime asc;"

                    // all rows from plcAlarms for specific groups has been copies into temporary table
                    // so give it by....

                    + "select "
                    + " id,"
                    + " triggerTime as 'activation-time',"
                    + " inactiveTime as 'inactivation-time',"
                    + " pointType as 'level',"
                    + " pointName as 'name' "
                    + " from "
                    + " tmp_sortedAlarmsStorungs "
                    + " limit param_limit offset param_offset; "

                    + "DROP TEMPORARY TABLE tmp_sortedAlarmsStorungs; "

                    + "end; ");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
