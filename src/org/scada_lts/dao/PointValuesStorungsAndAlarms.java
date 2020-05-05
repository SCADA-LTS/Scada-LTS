package org.scada_lts.dao;

import org.json.JSONObject;

public interface PointValuesStorungsAndAlarms {

    JSONObject getStorungs(int id);

    JSONObject getAlarms(int id);

    JSONObject getHistoryAlarmsById(int id);

    JSONObject getHistoryAlarmsByDateDayAndFilter( String date_day, String filter_with_mysqlrlike );

}
