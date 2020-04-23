package org.scada_lts.dao.storungsAndAlarms;

import org.json.JSONException;
import org.json.JSONObject;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.PointLinkDAO;

class PointValuesStorungsAndAlarmsDAO {

    public JSONObject getStorungs(int id) throws JSONException{

        //String templateSelectWhereId = POINT_LINK_SELECT + "where " + COLUMN_NAME_ID + "=? ";
        //return pointLinkVO;

        PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms=null;

        try {
            pointValuesStorungsAndAlarms = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new PointLinkDAO.PointLinkRowMapper());
            //put = new JSONObject().put("test", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return put;
    }
    public JSONObject getAlarms(int id) throws JSONException{

        //String templateSelectWhereId = POINT_LINK_SELECT + "where " + COLUMN_NAME_ID + "=? ";
        //return pointLinkVO;

        PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms=null;

        try {
            pointValuesStorungsAndAlarms = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new PointLinkDAO.PointLinkRowMapper());
            //put = new JSONObject().put("test", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return put;
    }

}
