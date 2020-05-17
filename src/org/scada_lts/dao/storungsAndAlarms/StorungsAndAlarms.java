package org.scada_lts.dao.storungsAndAlarms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scada_lts.dao.PointValuesStorungsAndAlarms;
import org.springframework.dao.DataAccessException;

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
