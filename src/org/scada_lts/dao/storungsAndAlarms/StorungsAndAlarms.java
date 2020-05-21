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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scada_lts.dao.PointValuesStorungsAndAlarms;
import org.springframework.dao.DataAccessException;
/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */

public class StorungsAndAlarms implements PointValuesStorungsAndAlarms {

    private static final Log LOG = LogFactory.getLog(StorungsAndAlarms.class);

    private JSONArray getActiveStorugs(){
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i <10;i++) {
            try {
                jsonArray.put(new JSONObject().put("1", "value1"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    private JSONArray getActiveAlarms(){
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i <100;i++) {
            try {
                jsonArray.put(new JSONObject()
                .put("id",String.valueOf(i))
                .put("pointId",String.valueOf(i))
                .put("pointXid","DP_12321")
                        .put("pointType","ST")
                        .put("pointName","ST")
                        .put("triggerTime","ST")
                        .put("inactiveTime","ST")
                        .put("acknowledgeTime","ST")
                        .put("lastpointValue","ST"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    public  JSONObject getHistoryAlarmsById(int id){
        JSONObject jsonObject = null;

        try
        {
            jsonObject = DAOs.getPointValuesStorungsAndAlarms().getHistoryAlarmsById(id);
        }
        catch (JSONException e)
        {
            LOG.trace(e.getMessage());
        }
        catch(Exception exception)
        {
            LOG.trace(exception.getMessage());
        }

        return jsonObject;
    }

    @Override
    public JSONArray getHistoryAlarmsByDateDayAndFilterOnlySinceOffsetAndLimit(String date_day, String filter_with_mysqlrlike, int offset, int limit) {

        JSONArray jsonArray = null;

        try
        {
            jsonArray = DAOs.getPointValuesStorungsAndAlarms().getHistoryAlarmsByDateDayAndFilterFromOffset(date_day, filter_with_mysqlrlike,offset,limit);
        }
        catch (JSONException e)
        {
            LOG.trace(e.getMessage());
        }
        catch(Exception exception)
        {
            LOG.trace(exception.getMessage());
        }

        return jsonArray;
    }

    public JSONObject getStorungsTest(int id){

        JSONObject jsonObject = null;

        try
        {
            jsonObject = new JSONObject();
            JSONArray jsonArray = getActiveStorugs();
            JSONArray jsonArray1 = getActiveAlarms();

            jsonObject.put("ACTIVE_ST",jsonArray);
            jsonObject.put("ACTIVE_AL",jsonArray1);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }
    public JSONObject getStorungs(int id){

        JSONObject jsonObject = null;

        try
        {
            jsonObject = DAOs.getPointValuesStorungsAndAlarms().getStorungs(id);
        }
        catch (JSONException e)
        {
            LOG.trace(e.getMessage());
        }
        catch(Exception exception)
        {
            LOG.trace(exception.getMessage());
        }

        return jsonObject;
    }

    @Override
    public JSONObject getAlarms(int id) {
        JSONObject jsonObject = null;

        try
        {
            jsonObject = DAOs.getPointValuesStorungsAndAlarms().getAlarms(1);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public boolean setAcknowledge(int id) {

        boolean result = false;
        try
        {
            DAOs.getPointValuesStorungsAndAlarms().setAcknowledge(id);
            result = true;
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
