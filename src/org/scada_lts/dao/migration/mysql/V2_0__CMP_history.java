package org.scada_lts.dao.migration.mysql;

import net.sf.json.JSON;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  grzegorz.bylica@abilit.eu on 15.10.2019
 */
public class V2_0__CMP_history implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {
        final String multiChangesHistory = ""
                + "create table multi_changes_history ("
                + "id int(11) not null auto_increment,"                  // id
                + "userId int(11),"                                      // the user id of the user making the record in the database
                + "username varchar(50),"                                // the name of the user who writes the values
                + "viewAndComponentIdentification varchar(50) not null," // component and view identifier
                + "interpretedState varchar(50) not null,"               // the state we were trying to get into
                + "ts bigint(20),"                                       // recording time
                + "primary key (id)"
                + ") engine=myisam;";                                    // without handling the transaction does not affect the rest of the system

        final String valuesMultiChangesHistory = ""
                + "create table values_multi_changes_history ("
                + "id int(11) not null auto_increment,"                 // id
                + "multiChangesHistoryId int(11),"                      // id to the multi_changes_history table ( without a foreign key)
                + "valueId bigint(20),"                                 // id of the value stored in pointValues (without a foreign key)
                + "value varchar(50) not null,"                         // a copy of the value recorded in pointValues
                + "dataPointId int(11),"                                // id to the dataPoins table (without a foreign key)
                + "ts bigint(20),"                                         // recording time
                + "primary key (id)"
                + ") engine=myisam;";                                   // without handling the transaction does not affect the rest of the system

        final String prcAddCMPHistory = ""
          //+ "drop procedure if exists prc_add_cmp_history;"
        + "create procedure prc_add_cmp_history( "
                + "in a_userId int(11), "
                + "in a_viewAndCmpId varchar(50), "
                + "in a_interpretedState varchar(50), "
                + "in a_ts bigint(20), "
                + "in a_list_of_values JSON) "
         + "begin "
                + "declare v_usr_name varchar(50); "
                + "declare v_multiChangesHistoryId int(11); "
                + "-- example a_list_of_values [ { \"xid\":\"DP_auto\",\"val\":\"1\" }, { \"xid\":\"3\",\"val\":\"1\"} ] "
                + "declare v_length bigint unsigned default JSON_LENGTH(a_list_of_values); "
                + "declare v_index bigint unsigned default 0; "
                + "declare v_data_point_id int(11); "
                + "declare v_data_point_value varchar(50); "

                + "select username into v_usr_name from users where id=a_userId; "

                + "insert into multi_changes_history ( "
                  + "userId, "
                  + "username, "
                  + "viewAndComponentIdentification, "
                  + "interpretedState, "
                 + "ts "
                + ") values ( "
                  + "a_userId, "
                  + "v_usr_name, "
                  + "a_viewAndCmpId, "
                  + "a_interpretedState, "
                  + "a_ts); "

                + "select last_insert_id() into v_multiChangesHistoryId; "

                + "while v_index < v_length DO "
                  + "set v_data_point_value = (select JSON_EXTRACT(a_list_of_values, CONCAT('$[', v_index, '].value'))); "
                  + "set v_data_point_id = (select id from dataPoints where xid=(select JSON_EXTRACT(a_list_of_values, CONCAT('$[',v_index,'].xid')))); "
                  + "insert into values_multi_changes_history ( "
                    + "multiChangesHistoryId, "
                    + "value, "
                    + "dataPointId, "
                    + "ts) values ( "
                    + "v_multiChangesHistoryId, "
                    + "v_data_point_value, "
                    + "v_data_point_id, "
                    + "a_ts "
                  + "); "
                  + "set v_index = v_index + 1; "
                + "end while; "
         + "end; ";

        jdbcTmp.execute(multiChangesHistory);
        jdbcTmp.execute(valuesMultiChangesHistory);

        jdbcTmp.execute(prcAddCMPHistory);
    }
}