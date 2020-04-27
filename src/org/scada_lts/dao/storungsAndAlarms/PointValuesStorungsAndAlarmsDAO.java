package org.scada_lts.dao.storungsAndAlarms;

import org.json.JSONException;
import org.json.JSONObject;
import org.scada_lts.dao.DAO;
import java.util.List;

class PointValuesStorungsAndAlarmsDAO {

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

    public JSONObject getAlarms(int id) throws JSONException{

        PointValues_ pointValuesStorungsAndAlarms=new PointValues_();
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

}
