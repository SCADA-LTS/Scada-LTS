package org.scada_lts.dao.storungsAndAlarms;

import org.json.JSONException;
import org.json.JSONObject;
import org.scada_lts.dao.PointValuesStorungsAndAlarms;

public class StorungsAndAlarms implements PointValuesStorungsAndAlarms {

    private static PointValuesStorungsAndAlarmsDAO pointValuesStorungsAndAlarmsDAO = new PointValuesStorungsAndAlarmsDAO();

    public JSONObject getStorungs(int id){

        JSONObject jsonObject = null;

        try
        {
            jsonObject = pointValuesStorungsAndAlarmsDAO.getStorungs(1);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public JSONObject getAlarms(int id) {
        JSONObject jsonObject = null;

        try
        {
            jsonObject = pointValuesStorungsAndAlarmsDAO.getAlarms(1);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
