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

    private static StringBuilder stringBuilder;
    private static String string;
    public static String generateStringBuilderSqlForStorungs(){
        stringBuilder = new StringBuilder("");
        stringBuilder.append("select * ");
        stringBuilder.append("from ");
        stringBuilder.append("pointValues ");
        stringBuilder.append("where id=1;");

        return stringBuilder.toString();
    }


    public static String generateStringSqlForStorungs(){
        string="";
        string+="select * ";
        string+="from ";
        string+="pointValues ";
        string+="where id=1;";

        return string;
    }
    static String getCommandForHistoryAlarmsByDateDayAndFilterFromOffset(
            String date_day,
            String filter_with_mysqlrlike,
            int offset,
            int limit){
        return new StringBuilder("select * from plcAlarms where pointType=1  limit "+limit+" offset "+offset).toString();
    }
    public static java.lang.String setAcknowledge(){
        return new StringBuilder("update plcAlarms set acknowledgeTime='1' where id=?").toString();
    }
}
