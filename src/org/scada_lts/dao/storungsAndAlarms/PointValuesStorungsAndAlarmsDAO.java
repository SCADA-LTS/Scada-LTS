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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scada_lts.dao.DAO;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class PointValuesStorungsAndAlarmsDAO {

    public JSONArray getHistoryAlarmsByDateDayAndFilterFromOffset(
            String date_day,
            String filter_with_mysqlrlike,
            int offset,
            int limit)  throws JSONException,Exception
    {
        JSONArray jsonArray=new JSONArray();
        List<PointValuesStorungsAndAlarms> pointValuesStorungsAndAlarms =  DAO.getInstance().getJdbcTemp().query(

                SqlCommandGenerator.getCommandForHistoryAlarmsByDateDayAndFilterFromOffset(
                        date_day,
                        filter_with_mysqlrlike,
                        offset,
                        limit),

                new PointValuesStorungsAndAlarmsRowMapper());
        if ( pointValuesStorungsAndAlarms.size() != 0) {
            for (PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms1 : pointValuesStorungsAndAlarms) {
                jsonArray.put(pointValuesStorungsAndAlarms1.toJSONObject());
            }
        }

        return jsonArray;
    }
    public JSONObject getStorungs(int id) throws JSONException,Exception{

        JSONObject jsonObject=new JSONObject();
        List<PointValuesStorungsAndAlarms> pointValuesStorungsAndAlarms =  DAO.getInstance().getJdbcTemp().query(
                        "SELECT * FROM pointValuesStorungsAndAlarms", new PointValuesStorungsAndAlarmsRowMapper());

        String pointValuesStorungsAndAlarmsAsString="";

        for(PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms1 : pointValuesStorungsAndAlarms){
            pointValuesStorungsAndAlarmsAsString+=pointValuesStorungsAndAlarms1.toString();

        }
        return jsonObject.put("1",pointValuesStorungsAndAlarmsAsString);
    }
    public JSONObject getHistoryAlarmsById(int id) throws JSONException{
        JSONObject jsonObject=null;
        try {
            //  pointValuesStorungsAndAlarms = (PointValues_)  DAO.getInstance().getJdbcTemp().queryForObject(SqlCommandGenerator.generateStringBuilderSqlForStorungs(), new PointValuesStorungsAndAlarmsRowMapper());
            //   } catch (DataAccessException e) {
            //        e.printStackTrace();
            //    }
            jsonObject = new JSONObject().put("test", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public JSONObject getAlarms(int id) throws JSONException{

        JSONObject jsonObject=null;
        try {
                String  j="";
              //  pointValuesStorungsAndAlarms = (PointValues_)  DAO.getInstance().getJdbcTemp().queryForObject(SqlCommandGenerator.generateStringBuilderSqlForStorungs(), new PointValuesStorungsAndAlarmsRowMapper());
         //   } catch (DataAccessException e) {
        //        e.printStackTrace();
        //    }
            jsonObject = new JSONObject().put("test", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public void setAcknowledge(int id) throws DataAccessException {

        DAO.getInstance().getJdbcTemp().update(SqlCommandGenerator.setAcknowledge(), new Object[] {id});

    }

}
