package org.scada_lts.dao.storungsAndAlarms;
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
/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class SqlCommandGenerator {

    static String setAcknowledge(){
        return new StringBuilder("update plcAlarms set acknowledgeTime=(select from_unixtime(unix_timestamp())) where id=?").toString();
    }
    static String getCommandForHistoryAlarmsByDateDayAndFilterFromOffset(
            String date_day,
            String filter_with_mysqlrlike,
            int offset,
            int limit
    ){
        // Please look into org.scada_lts.dao.migration.mysql , if you want to know how look a base of apiAlarmsHistory view sql command

        return new StringBuilder("select * from apiAlarmsHistory where DATE_FORMAT(time, '%Y-%m-%d')='"+date_day+"' "+addRLIKE(filter_with_mysqlrlike)+" limit "+limit+" offset "+offset).toString();
    }
    private static String addRLIKE(String filter_with_mysqlrlike){
        return (filter_with_mysqlrlike.equals("EMPTY"))
                ?" "
                :"  and name RLIKE '"+filter_with_mysqlrlike+"' ";
    }
    static String getliveAlarms(int offset,int limit){

        // In org.scada_lts.dao.migration.mysql , exist an a body of prc_sort_alarms_and_storungs_depend_on_state

        return new StringBuilder("call prc_sort_alarms_and_storungs_depend_on_state("+limit+","+offset+")").toString();
    }
}
